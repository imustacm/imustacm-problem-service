package cn.imustacm.problem.controller;

import cn.imustacm.common.domain.Resp;
import cn.imustacm.common.enums.ErrorCodeEnum;
import cn.imustacm.common.utils.OkHttpUtil;
import cn.imustacm.common.utils.RedisUtils;
import cn.imustacm.common.utils.SecretUtils;
import cn.imustacm.problem.client.UserClient;
import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.dto.ProblemToTagDTO;
import cn.imustacm.problem.dto.SubmitCodeDTO;
import cn.imustacm.problem.dto.SubmitInfoDTO;
import cn.imustacm.problem.model.JudgeServer;
import cn.imustacm.problem.model.Problem;
import cn.imustacm.problem.model.SpecialJudge;
import cn.imustacm.problem.model.Submission;
import cn.imustacm.problem.service.*;
import cn.imustacm.user.model.Option;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjianli
 * @date 2019/09/24
 */

@RestController
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private UserClient userClient;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private ProblemToTagService problemToTagService;
    @Autowired
    private JudgeServerService judgeServerService;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private SpecialJudgeService specialJudgeService;
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    /**
     * 分页获取题目列表
     */
    @GetMapping("/listProblem")
    public Resp getProblemList(Integer pageIndex, Integer pageSize) {
        if (Objects.isNull(pageIndex) || Objects.isNull(pageSize)) {
            pageIndex = 1;
            pageSize = 100;
        }
        List<ProblemListDTO> problemListDTO = problemService.getProblemByPage(pageIndex, pageSize);
        Integer problemNumber = problemService.getProblemTotalNumber();
        if (CollectionUtils.isEmpty(problemListDTO)) {
            return Resp.ok(new Page<>(pageIndex, 0, problemNumber));
        }
        List<ProblemToTagDTO> problemToTagDTOs = problemToTagService.getProblemToTag(
                problemListDTO.get(0).getId(), problemListDTO.get(problemListDTO.size() - 1).getId());
        DecimalFormat df = new DecimalFormat("###0.00");
        for (int i = 0; i < problemListDTO.size(); i++) {
            if (problemListDTO.get(i).getSubmitNumber().equals(0)) {
                problemListDTO.get(i).setAcceptedPercent(df.format(0) + "%");
            } else {
                problemListDTO.get(i).setAcceptedPercent(
                        df.format(Double.valueOf(problemListDTO.get(i).getAcceptedNumber())
                                / Double.valueOf(problemListDTO.get(i).getSubmitNumber()) * 100) + "%");
            }
            List<ProblemToTagDTO> problemToTagDTOItems = new ArrayList<ProblemToTagDTO>();
            for (int j = 0; j < problemToTagDTOs.size(); j++) {
                if (problemToTagDTOs.get(j).getProblemId().equals(problemListDTO.get(i).getId())) {
                    problemToTagDTOItems.add(problemToTagDTOs.get(j));
                }
            }
            problemListDTO.get(i).setTags(problemToTagDTOItems);
        }
        // 将dto集合封装到分页对象
        Page<ProblemListDTO> result = new Page<>(pageIndex, problemListDTO.size(), problemNumber);
        result.setRecords(problemListDTO);
        return Resp.ok(result);
    }

    /**
     * 测试feign调用user服务
     *
     * @param userId
     * @return
     */
    //@GetMapping("ceshi")
    //public Resp ceshiFeign(@RequestParam("userId") Long userId) {
        //return Resp.ok(userClient.getUser(userId));
    //}

    /**
     * 提交代码
     */
    @PostMapping("/submitCode")
    public Resp submitCode(@RequestBody SubmitCodeDTO submitCodeDTO) {
        if(submitCodeDTO.getLanguage() == null || "".equals(submitCodeDTO.getLanguage()))
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_LANGUAGE_NULL);
        Option option = userClient.getByKey("language");
        JSONArray language = JSONArray.parseArray(option.getValue());
        boolean flag = false;
        for(int i = 0; i < language.size(); i++) {
            if(language.get(i).toString().equals(submitCodeDTO.getLanguage())) {
                flag = true;
                break;
            }
        }
        if(!flag)
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_LANGUAGE_ERROR);
        if(submitCodeDTO.getCode() == null || "".equals(submitCodeDTO.getCode()))
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_CODE_NULL);
        Problem problem = problemService.getById(submitCodeDTO.getProblem_id());
        if(problem == null)
            return Resp.fail(ErrorCodeEnum.PROBLEM_NOT_EXIST);
        option = userClient.getByKey("judger");
        JSONObject value = JSONObject.parseObject(option.getValue());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("language", submitCodeDTO.getLanguage());
        jsonObject.put("src", submitCodeDTO.getCode());
        jsonObject.put("max_cpu_time", problem.getTimeLimit() * 1000);
        jsonObject.put("max_memory", problem.getMemoryLimit() * 1048576);
        jsonObject.put("problem_id", String.valueOf(submitCodeDTO.getProblem_id()));
        jsonObject.put("notify_url", value.getString("backurl"));
        jsonObject.put("output", false);
        String problemType = Integer.toBinaryString(problem.getProblemType());
        int length = problemType.length();
        char spjFlag = '0';
        if(length > 1)
             spjFlag = problemType.charAt(length - 2);
        if(spjFlag == '0')
            jsonObject.put("is_spj", false);
        else {
            jsonObject.put("is_spj", true);
            jsonObject.put("spj_version", submitCodeDTO.getProblem_id());
            SpecialJudge specialJudge = specialJudgeService.getByProblemId(submitCodeDTO.getProblem_id());
            if(specialJudge == null)
                jsonObject.put("spj_src", "");
            else {
                if (specialJudge.getCompileFlag() == false)
                    jsonObject.put("spj_src", "");
                else
                    jsonObject.put("spj_src", specialJudge.getCode());
            }
        }
        //jsonObject.put("test_case", "");
        //jsonObject.put("io_mode", "");

        JudgeServer judgeServer = judgeServerService.getJudgeServer();
        List<Map> maps = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("key", "X-Judge-Server-Token");
        map.put("value", SecretUtils.getSHA256(judgeServer.getToken()));
        maps.add(map);
        String back = OkHttpUtil.postJsonParams(judgeServer.getHost() + "judge", jsonObject.toJSONString(), maps);
        JSONObject result = new JSONObject();
        try {
            result = JSONObject.parseObject(back);
        } catch (Exception e) {
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_SERVER_ERROR);
        }

        Submission submission = new Submission();
        submission.setProblemId(submitCodeDTO.getProblem_id());
        if(submitCodeDTO.getContest_id() != null || !"".equals(submitCodeDTO.getContest_id()))
            submission.setContestId(submitCodeDTO.getContest_id());
        submission.setUserId(1);  //先写死
        submission.setCreateTime(LocalDateTime.now());
        submission.setCode(submitCodeDTO.getCode());
        submission.setResult(10);
        submission.setLanguage(submitCodeDTO.getLanguage().toLowerCase());
        submission.setCodeShare(false);
        submission.setIp("127.0.0.1");
        submission.setJudgeServer(judgeServer.getId());
        submissionService.save(submission);

        int id = submission.getId();
        if(result.getString("err") == null) {
            redisTemplate = RedisUtils.redisTemplate(redisConnectionFactory);
            redisTemplate.opsForValue().set("Submit:" + result.getJSONObject("data").getString("judge_id"), id);
        } else {
            submission = new Submission();
            submission.setId(id);
            submission.setResult(6);
            submissionService.updateById(submission);
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_SERVER_ERROR);
        }
        return Resp.ok(result);
    }

    /**
     * 判题机回调
     */
    @PostMapping("/getSubmitInfo")
    public Resp getSubmitInfo(@RequestBody SubmitInfoDTO submitInfoDTO) {
        if(submitInfoDTO.getErr() != null && !"".equals(submitInfoDTO.getErr()))
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_SERVER_ERROR);
        if(submitInfoDTO.getJudge_id() == null && "".equals(submitInfoDTO.getJudge_id()))
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_SERVER_ERROR);
        redisTemplate = RedisUtils.redisTemplate(redisConnectionFactory);
        String key = "Submit:" + submitInfoDTO.getJudge_id();
        boolean hasKey = redisTemplate.hasKey(key);
        if(!hasKey)
            return Resp.ok("任务已不存在");
        int submitId = Integer.valueOf(redisTemplate.opsForValue().get(key).toString());
        JSONObject result = submitInfoDTO.getJudge_result();
        if(result.getString("err") == null) {
            JSONArray judge = result.getJSONArray("judge_result");
            int finalCode = -99;
            int wrongNum = 0;
            if(judge.size() < 1)
                return Resp.fail(ErrorCodeEnum.PROBLEM_TESTDATA_NOT_EXIST);
            JSONArray ja = new JSONArray();
            for(int i = 0; i < judge.size(); i++) {
                JSONObject judgeChild = JSONObject.parseObject(JSONObject.toJSONString(judge.get(i)));
                int resCode = judgeChild.getInteger("result");
                if(resCode != 0) {
                    wrongNum++;
                    if(resCode > finalCode) {
                        finalCode = resCode;
                    }
                    JSONObject js = new JSONObject();
                    js.put("test_case", judgeChild.getString("test_case"));
                    js.put("result", judgeChild.getInteger("result"));
                    js.put("cpu_time", judgeChild.getInteger("cpu_time"));
                    js.put("real_time", judgeChild.getInteger("real_time"));
                    js.put("memory", judgeChild.getInteger("memory"));
                    ja.add(js);
                }
            }
            Submission submission = new Submission();
            submission.setId(submitId);
            submission.setJudgeTime(LocalDateTime.now());
            if(finalCode == -99) {
                submission.setResult(0);
                submission.setPassRate(100);
            }
            else {
                submission.setResult(finalCode);
                submission.setPassRate((int)((judge.size() - wrongNum) * 1.0 /judge.size() * 100));
                submission.setStatisticInfo(ja.toJSONString());
            }
            submissionService.updateById(submission);
        } else {
            String path = "/judger/run/" + submitInfoDTO.getJudge_id() +"/";
            Submission submission = new Submission();
            submission.setId(submitId);
            submission.setResult(7);
            submission.setStatisticInfo(result.getString("msg").replace(path, ""));
            submission.setJudgeTime(LocalDateTime.now());
            submission.setPassRate(0);
            submissionService.updateById(submission);
        }
        redisTemplate.delete(key);
        return Resp.ok("任务处理成功");
    }
}
