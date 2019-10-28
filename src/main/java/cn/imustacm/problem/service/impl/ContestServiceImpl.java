package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.mapper.ContestMapper;
import cn.imustacm.problem.model.Contest;
import cn.imustacm.problem.service.ContestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author liandong
 * @since 2019-10-11
 */
@Service
public class ContestServiceImpl extends ServiceImpl<ContestMapper, Contest> implements ContestService {

}
