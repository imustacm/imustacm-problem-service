package cn.imustacm.problem.mapper;


import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.model.JudgeServer;
import cn.imustacm.problem.model.Problem;
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
 * @author wangjianli
 * @since 2019-10-09
 *
 */
@Mapper
@Component("judgeServerMapper")
public interface JudgeServerMapper extends BaseMapper<JudgeServer> {

}
