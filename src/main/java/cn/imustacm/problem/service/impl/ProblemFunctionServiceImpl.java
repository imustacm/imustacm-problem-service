package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.mapper.ProblemFunctionMapper;
import cn.imustacm.problem.model.ProblemFunction;
import cn.imustacm.problem.service.ProblemFunctionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author liandong
 * @since 2019-10-11
 */
@Service
public class ProblemFunctionServiceImpl extends ServiceImpl<ProblemFunctionMapper, ProblemFunction> implements ProblemFunctionService {

    @Override
    public ProblemFunction getProblemFunctionByProblem(Integer problem_id) {
        LambdaQueryWrapper<ProblemFunction> wrapper = new QueryWrapper<ProblemFunction>().lambda().eq(ProblemFunction::getProblemId, problem_id);
        ProblemFunction problemFunction = getOne(wrapper);
        return problemFunction;
    }

}
