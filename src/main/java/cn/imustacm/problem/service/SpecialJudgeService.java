package cn.imustacm.problem.service;

import cn.imustacm.problem.model.SpecialJudge;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 服务类
 *
 * @author liandong
 * @since 2019-10-11
 */
public interface SpecialJudgeService extends IService<SpecialJudge> {

    /**
     * 根据题目编号查询spj信息
     *
     * @param problem_id
     * @return
     */
    SpecialJudge getByProblemId(Integer problem_id);

}
