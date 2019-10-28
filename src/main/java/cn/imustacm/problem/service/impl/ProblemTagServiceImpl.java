package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.mapper.ProblemTagMapper;
import cn.imustacm.problem.model.ProblemTag;
import cn.imustacm.problem.service.ProblemTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 *
 * @author liandong
 * @since 2019-09-10
 */
@Service
public class ProblemTagServiceImpl extends ServiceImpl<ProblemTagMapper, ProblemTag> implements ProblemTagService {

    @Override
    public List<ProblemTag> listTags() {
        LambdaQueryWrapper<ProblemTag> wrapper = new QueryWrapper<ProblemTag>().lambda().eq(ProblemTag::getVisible, true);
        List<ProblemTag> problemTags = list(wrapper);
        return problemTags;
    }

}
