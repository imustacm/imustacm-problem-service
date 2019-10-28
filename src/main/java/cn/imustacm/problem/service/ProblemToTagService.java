package cn.imustacm.problem.service;

import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.dto.ProblemToTagDTO;
import cn.imustacm.problem.model.ProblemToTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 服务类
 *
 * @author liandong
 * @since 2019-09-10
 */
public interface ProblemToTagService extends IService<ProblemToTag> {

    /**
     * 分页查询题目标签
     *
     * @param startProblemId
     * @param endProblemId
     * @return
     */
    List<ProblemToTagDTO> getProblemToTag(Integer startProblemId, Integer endProblemId);

}
