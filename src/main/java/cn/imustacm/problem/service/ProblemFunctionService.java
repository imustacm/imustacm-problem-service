package cn.imustacm.problem.service;

import cn.imustacm.problem.model.ProblemFunction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 服务类
 *
 * @author liandong
 * @since 2019-10-11
 */
public interface ProblemFunctionService extends IService<ProblemFunction> {

    /**
     * 根据problem_id查询函数实现类题目信息
     *
     * @param problem_id
     * @return
     */
    ProblemFunction getProblemFunctionByProblem(Integer problem_id);

}
