package cn.imustacm.problem.controller;

import cn.imustacm.common.domain.Resp;
import cn.imustacm.problem.client.UserClient;
import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.dto.ProblemToTagDTO;
import cn.imustacm.problem.model.Problem;
import cn.imustacm.problem.service.ProblemService;
import cn.imustacm.problem.service.ProblemToTagService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wangjianli
 * @date 2019/09/24
 */

@RestController
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ProblemToTagService problemToTagService;

    @Autowired
    private UserClient userClient;

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
}
