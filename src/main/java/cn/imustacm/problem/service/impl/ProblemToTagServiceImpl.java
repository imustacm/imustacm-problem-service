package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.dto.ProblemToTagDTO;
import cn.imustacm.problem.mapper.ProblemMapper;
import cn.imustacm.problem.mapper.ProblemToTagMapper;
import cn.imustacm.problem.model.ProblemToTag;
import cn.imustacm.problem.service.ProblemToTagService;
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
public class ProblemToTagServiceImpl extends ServiceImpl<ProblemToTagMapper, ProblemToTag> implements ProblemToTagService {

    @Autowired
    private ProblemToTagMapper problemToTagMapper;

    public List<ProblemToTagDTO> getProblemToTag(Integer startProblemId, Integer endProblemId) {
        return problemToTagMapper.getProblemToTag(startProblemId, endProblemId);
    }

}
