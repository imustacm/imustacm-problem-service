package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.mapper.SubmissionMapper;
import cn.imustacm.problem.model.Submission;
import cn.imustacm.problem.service.SubmissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author liandong
 * @since 2019-10-11
 */
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {

    @Override
    public Page<Submission> getList(Integer pageIndex, Integer pageSize) {
        if (pageIndex <= 0){
            pageIndex = 1;
        }
        if(pageSize <= 0 ){
            pageSize = 100;
        }
        LambdaQueryWrapper<Submission> wrapper = new QueryWrapper<Submission>().lambda()
                .isNull(Submission::getContestId).or(obj -> obj.isNotNull(Submission::getContestId).eq(Submission::getContestId, 0)).orderByDesc(Submission::getId);
        return (Page<Submission>) page(new Page<>(pageIndex, pageSize), wrapper);
    }

    @Override
    public Integer getListCount() {
        LambdaQueryWrapper<Submission> wrapper = new QueryWrapper<Submission>().lambda()
                .isNull(Submission::getContestId).or(obj -> obj.isNotNull(Submission::getContestId).eq(Submission::getContestId, 0)).orderByDesc(Submission::getId);
        return list(wrapper).size();
    }

}
