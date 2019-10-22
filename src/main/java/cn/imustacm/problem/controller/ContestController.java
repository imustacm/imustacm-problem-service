package cn.imustacm.problem.controller;

import cn.imustacm.common.domain.Resp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author wangjianli
 * @date 2019/09/29
 */

@RestController
@RequestMapping("/contest")
public class ContestController {

    /**
     * 分页获取竞赛列表
     */
    @GetMapping("/listContest")
    public Resp getContestList(Integer pageIndex, Integer pageSize) {
        if (Objects.isNull(pageIndex) || Objects.isNull(pageSize)) {
            pageIndex = 1;
            pageSize = 100;
        }
        //在此写方法内逻辑

        return Resp.ok();
    }

    /**
     * 分页获取实验列表
     */
    @GetMapping("/listExperiment")
    public Resp getExperimentList(Integer pageIndex, Integer pageSize) {
        if (Objects.isNull(pageIndex) || Objects.isNull(pageSize)) {
            pageIndex = 1;
            pageSize = 100;
        }
        //在此写方法内逻辑

        return Resp.ok();
    }

}
