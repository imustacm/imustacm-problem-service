package cn.imustacm.problem.controller;

import cn.imustacm.common.domain.Resp;
import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.model.Problem;
import cn.imustacm.problem.service.ProblemService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

    /**
     * 分页获取题目列表
     */
    @GetMapping("/listProblem")
    public Resp getUserList(Integer pageIndex, Integer pageSize) {
        if (Objects.isNull(pageIndex) || Objects.isNull(pageSize)) {
            pageIndex = 1;
            pageSize = 100;
        }
        List<Problem> problemList = problemService.getProblemByPage(pageIndex, pageSize);
        Integer problemNumber = problemService.getProblemTotalNumber();
        if (CollectionUtils.isEmpty(problemList)) {
            return Resp.ok(new Page<>(pageIndex, 0, problemNumber));
        }
        // 实体类转换成dto
        List<ProblemListDTO> problemListDTO = problemList.stream()
                .map(problems -> ProblemListDTO
                        .builder()
                        .id(problems.getId())
                        .title(problems.getTitle())
                        .difficulty(problems.getDifficulty())
                        .source(problems.getSource())
                        .spjFlag(problems.getSpjFlag())
                        .submitNumber(problems.getSubmitNumber())
                        .acceptedNumber(problems.getAcceptedNumber())
                        .build())
                .collect(Collectors.toList());
        DecimalFormat df = new DecimalFormat("###0.00");
        for(int i = 0; i < problemListDTO.size(); i++) {
            if(problemListDTO.get(i).getSubmitNumber().equals(0)){
                problemListDTO.get(i).setAcceptedPercent(df.format(0) + "%");
            } else {
                problemListDTO.get(i).setAcceptedPercent(
                        df.format(Double.valueOf(problemListDTO.get(i).getAcceptedNumber())
                                / Double.valueOf(problemListDTO.get(i).getSubmitNumber()) * 100) + "%");
            }
        }
        // 将dto集合封装到分页对象
        Page<ProblemListDTO> result = new Page<>(pageIndex, problemListDTO.size(), problemNumber);
        result.setRecords(problemListDTO);
        return Resp.ok(result);
    }
}