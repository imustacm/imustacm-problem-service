package cn.imustacm.problem.controller;

import cn.imustacm.common.domain.Resp;
import cn.imustacm.common.enums.ErrorCodeEnum;
import cn.imustacm.common.utils.OkHttpUtil;
import cn.imustacm.common.utils.SecretUtils;
import cn.imustacm.problem.client.UserClient;
import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.dto.ProblemToTagDTO;
import cn.imustacm.problem.dto.SubmitCodeDTO;
import cn.imustacm.problem.model.JudgeServer;
import cn.imustacm.problem.service.JudgeServerService;
import cn.imustacm.problem.service.ProblemService;
import cn.imustacm.problem.service.ProblemToTagService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.*;

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
    @GetMapping("ceshi")
    public Resp ceshiFeign(@RequestParam("userId") Long userId) {
        return Resp.ok(userClient.getUser(userId));
    }

    /**
     * 提交代码
     */
    @PostMapping("/submitCode")
    public Resp submitCode(@RequestBody SubmitCodeDTO submitCodeDTO) {
        if(submitCodeDTO.getLanguage() == null || "".equals(submitCodeDTO.getLanguage()))
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_LANGUAGE_NULL);
        if(submitCodeDTO.getCode() == null || "".equals(submitCodeDTO.getCode()))
            return Resp.fail(ErrorCodeEnum.PROBLEM_SUBMIT_CODE_NULL);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("language", submitCodeDTO.getLanguage());
        jsonObject.put("src", submitCodeDTO.getCode());
        jsonObject.put("max_cpu_time", 1000);  //先写死
        jsonObject.put("max_memory", 6020);  //先写死
        jsonObject.put("problem_id", String.valueOf(submitCodeDTO.getProblem_id()));
        jsonObject.put("output", false);

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
        return Resp.ok(result);
    }
}
