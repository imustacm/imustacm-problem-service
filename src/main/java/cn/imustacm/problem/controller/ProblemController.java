package cn.imustacm.problem.controller;

import cn.imustacm.common.domain.Resp;
import cn.imustacm.common.enums.ErrorCodeEnum;
import cn.imustacm.common.utils.OkHttpUtil;
import cn.imustacm.common.utils.RedisUtils;
import cn.imustacm.common.utils.SecretUtils;
import cn.imustacm.problem.client.UserClient;
import cn.imustacm.problem.dto.*;
import cn.imustacm.problem.model.*;
import cn.imustacm.problem.service.*;
import cn.imustacm.user.dto.RankListDTO;
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
import java.util.stream.Collectors;

import static cn.imustacm.common.consts.DatePatternConst.DATE_TIME_FORMATTER;

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
    private ProblemTagService problemTagService;
    @Autowired
    private ProblemFunctionService problemFunctionService;
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
    @GetMapping("/listProblems")
    public Resp listProblems(Integer pageIndex, Integer pageSize) {
        if (Objects.isNull(pageIndex) || Objects.isNull(pageSize)) {
            pageIndex = 1;
            pageSize = 100;
        }
        List<ProblemListDTO> problemListDTO = problemService.getProblemsByPage(pageIndex, pageSize);
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
        jsonObject.put("io_mode", false);
        //jsonObject.put("test_case", "");
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
        LocalDateTime localDateTime = LocalDateTime
                .parse(LocalDateTime.now().format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER);
        submission.setCreateTime(localDateTime);
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
    @PostMapping("/getSubmissionInfo")
    public Resp getSubmissionInfo(@RequestBody SubmitInfoDTO submitInfoDTO) {
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
            long cpuTime = 0;
            long realTime = 0;
            long memory = 0;
            if(judge.size() < 1)
                return Resp.fail(ErrorCodeEnum.PROBLEM_TESTDATA_NOT_EXIST);
            for(int i = 0; i < judge.size(); i++) {
                JSONObject judgeChild = JSONObject.parseObject(JSONObject.toJSONString(judge.get(i)));
                int resCode = judgeChild.getInteger("result");
                long cpu_time = judgeChild.getInteger("cpu_time");
                long real_time = judgeChild.getInteger("real_time");
                long use_memory = judgeChild.getInteger("memory");
                if(resCode != 0) {
                    wrongNum++;
                    if(resCode > finalCode)
                        finalCode = resCode;
                }
                if(cpu_time > cpuTime)
                    cpuTime = cpu_time;
                if(real_time > realTime)
                    realTime = real_time;
                if(use_memory > memory)
                    memory = use_memory;
            }
            Submission submission = new Submission();
            submission.setId(submitId);
            LocalDateTime localDateTime = LocalDateTime
                    .parse(LocalDateTime.now().format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER);
            submission.setJudgeTime(localDateTime);
            submission.setCpuTime(cpuTime);
            submission.setRealTime(realTime);
            submission.setMemory(memory);
            if(finalCode == -99) {
                submission.setResult(0);
                submission.setPassRate(100);
            }
            else {
                submission.setResult(finalCode);
                submission.setPassRate((int)((judge.size() - wrongNum) * 1.0 /judge.size() * 100));
            }
            submissionService.updateById(submission);
        } else {
            String path = "/judger/run/" + submitInfoDTO.getJudge_id() +"/";
            Submission submission = new Submission();
            submission.setId(submitId);
            submission.setResult(7);
            submission.setStatisticInfo(result.getString("msg").replace(path, ""));
            LocalDateTime localDateTime = LocalDateTime
                    .parse(LocalDateTime.now().format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER);
            submission.setJudgeTime(localDateTime);
            submission.setPassRate(0);
            submissionService.updateById(submission);
        }
        redisTemplate.delete(key);
        return Resp.ok("任务处理成功");
    }

    /**
     * 获取题目详情
     */
    @GetMapping("/getProblem")
    public Resp getProblem(String problem_id) {
        if(problem_id == null && "".equals(problem_id))
            return Resp.fail(ErrorCodeEnum.BIZ_PARAM_ERR);
        Problem problem = new Problem();
        ProblemFunction problemFunction = new ProblemFunction();
        List<ProblemToTagDTO> tags = new ArrayList<>();
        try {
            Integer id = Integer.valueOf(problem_id);
            Problem tempProblem = problemService.getProblemById(id);
            if(tempProblem == null)
                return Resp.fail(ErrorCodeEnum.PROBLEM_NOT_EXIST);
            problem = tempProblem;
            List<ProblemToTagDTO> tempTags = problemToTagService.getProblemToTag(id, id);
            if(tempTags != null && tempTags.size() > 0)
                tags = tempTags;
            String problemType = Integer.toBinaryString(problem.getProblemType());
            int length = problemType.length();
            char functionFlag = '0';
            if(length > 2)
                functionFlag = problemType.charAt(length - 3);
            if(functionFlag == '1') {
                ProblemFunction tempProblemFunction = problemFunctionService.getProblemFunctionByProblem(id);
                if(tempProblemFunction != null)
                    problemFunction = tempProblemFunction;
            }
        } catch (Exception e) {
            return Resp.fail(ErrorCodeEnum.BIZ_PARAM_ERR);
        }
        return Resp.ok(ProblemDTO.builder().problem(problem).tags(tags).problemFunction(problemFunction).build());
    }

    /**
     * 获取所有题目标签
     */
    @GetMapping("/listTags")
    public Resp listTags() {
        return Resp.ok(problemTagService.listTags());
    }

    /**
     * 获取所有提交信息
     */
    @GetMapping("/listSubmissions")
    public Resp listSubmissions(Integer pageIndex, Integer pageSize) {
        if (Objects.isNull(pageIndex) || Objects.isNull(pageSize)) {
            pageIndex = 1;
            pageSize = 100;
        }
        Page<Submission> page = submissionService.getList(pageIndex, pageSize);
        List<Submission> submissionList = page.getRecords();
        Integer submissionNumber = submissionService.getListCount();
        if (CollectionUtils.isEmpty(submissionList)) {
            return Resp.ok(new Page<>(pageIndex, 0, submissionNumber));
        }
        // 实体类转换成dto
        List<SubmissionDTO> submissionDTOList = submissionList.stream()
                .map(submission -> SubmissionDTO
                        .builder()
                        .id(submission.getId())
                        .problemId(submission.getProblemId())
                        .contestId(submission.getContestId())
                        .userId(submission.getUserId())
                        .createTime(submission.getCreateTime())
                        .result(submission.getResult())
                        .language(submission.getLanguage())
                        .codeShare(submission.getCodeShare())
                        .ip(submission.getIp())
                        .judgeServer(submission.getJudgeServer())
                        .judgeTime(submission.getJudgeTime())
                        .passRate(submission.getPassRate())
                        .build())
                .collect(Collectors.toList());
        // 将dto集合封装到分页对象
        Page<SubmissionDTO> result = new Page<>(page.getCurrent(), submissionDTOList.size(), page.getTotal());
        result.setRecords(submissionDTOList);
        return Resp.ok(result);
    }

    /**
     * 获取解题排名
     */
    @GetMapping("/listRank")
    public Resp listRank(Integer pageIndex, Integer pageSize) {
        Page<RankListDTO> users = userClient.getRankList(pageIndex, pageSize);
        return Resp.ok(users);
    }
}
