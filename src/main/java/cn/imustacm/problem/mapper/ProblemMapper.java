package cn.imustacm.problem.mapper;


import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.model.Problem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author liandong
 * @since 2019-09-10
 */
@Mapper
@Component("problemMapper")
public interface ProblemMapper extends BaseMapper<Problem> {

    @Select(
        "<script>"
            + "SELECT id, title, difficulty, source, problem_type, submit_number, accepted_number FROM problem "
            + "<where> "
                + "( id BETWEEN ( #{pageIndex} - 1 ) * #{pageSize} + 1 AND #{pageIndex} * #{pageSize} ) AND visible = TRUE AND id NOT IN ( "
                    + "SELECT problem_id FROM contest_problem "
                    + "<where> "
                        + "contest_id IN ( "
                            + "SELECT id FROM contest "
                            + "<where> "
                                + " ( ( ( end_time &gt; now() ) OR ( permission_type != 0 ) ) AND visible = TRUE )"
                            + "</where> "
                        + ") "
                    + "</where> "
                + ") "
            + "</where>"
      + "</script>"
    )
    List<ProblemListDTO> getProblemsByPage(@Param("pageIndex")Integer pageIndex, @Param("pageSize")Integer pageSize);

    @Select(
        "<script>"
            + "SELECT COUNT(id) FROM problem "
            + "<where> "
                +"visible = TRUE "
            + "</where>"
        + "</script>"
    )
    Integer getProblemTotalNumber();

    @Select(
            "<script>"
                + "SELECT * FROM problem "
                + "<where> "
                    + "visible = TRUE AND id = #{id} AND id NOT IN ( "
                        + "SELECT problem_id FROM contest_problem "
                        + "<where> "
                            + "contest_id IN ( "
                                + "SELECT id FROM contest "
                                + "<where> "
                                    + " ( ( ( end_time &gt; now() ) OR ( permission_type != 0 ) ) AND visible = TRUE )"
                                + "</where> "
                            + ") "
                        + "</where> "
                    + ") "
                + "</where>"
            + "</script>"
    )
    Problem getProblemById(@Param("id")Integer id);

}
