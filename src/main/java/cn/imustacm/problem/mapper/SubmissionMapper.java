package cn.imustacm.problem.mapper;

import cn.imustacm.problem.model.Submission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * Mapper 接口
 *
 * @author liandong
 * @since 2019-10-11
 */
@Mapper
@Component("submissionMapper")
public interface SubmissionMapper extends BaseMapper<Submission> {

}
