package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.mapper.ProblemMapper;
import cn.imustacm.problem.model.Problem;
import cn.imustacm.problem.service.ProblemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liandong
 * @since 2019-09-10
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    public List<ProblemListDTO> getProblemByPage(Integer pageIndex, Integer pageSize) {
        return problemMapper.getProblemByPage(pageIndex, pageSize);
    }

    public Integer getProblemTotalNumber() {
        return problemMapper.getProblemTotalNumber();
    }
}
