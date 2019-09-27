package cn.imustacm.problem.mapper;


import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.dto.ProblemToTagDTO;
import cn.imustacm.problem.model.ProblemToTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 *  Mapper 接口
 *
 * @author liandong
 * @since 2019-09-10
 */
@Mapper
@Component("problemToTagMapper")
public interface ProblemToTagMapper extends BaseMapper<ProblemToTag> {

    @Select(
        "<script>"
            + "select ptt.problem_id, ptt.problem_tag_id, pt.name from problem_to_tag ptt, problem_tag pt "
            + "<where> "
                + "ptt.problem_id between #{startProblemId} and #{endProblemId} and ptt.problem_tag_id in ( "
                + "select id from problem_tag "
                + "<where> "
                    + "visible = true "
                + "</where> "
                + ") and ptt.problem_tag_id = pt.id "
            + "</where> "
            + "ORDER BY problem_id, problem_tag_id"
        + "</script>"
    )

    List<ProblemToTagDTO> getProblemToTag(@Param("startProblemId")Integer startProblemId, @Param("endProblemId")Integer endProblemId);

}
