package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.mapper.SpecialJudgeMapper;
import cn.imustacm.problem.model.SpecialJudge;
import cn.imustacm.problem.service.SpecialJudgeService;
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
public class SpecialJudgeServiceImpl extends ServiceImpl<SpecialJudgeMapper, SpecialJudge> implements SpecialJudgeService {

    @Override
    public SpecialJudge getByProblemId(Integer problem_id) {
        LambdaQueryWrapper<SpecialJudge> wrapper = new QueryWrapper<SpecialJudge>().lambda().eq(SpecialJudge::getProblemId, problem_id);
        SpecialJudge specialJudge = getOne(wrapper);
        return specialJudge;
    }

}
