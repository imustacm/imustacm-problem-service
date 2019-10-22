package cn.imustacm.problem.mapper;

import cn.imustacm.problem.model.ContestUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liandong
 * @since 2019-10-11
 */
@Mapper
@Component("contestUserMapper")
public interface ContestUserMapper extends BaseMapper<ContestUser> {

}
