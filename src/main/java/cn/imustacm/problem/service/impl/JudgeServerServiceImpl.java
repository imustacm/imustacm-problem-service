package cn.imustacm.problem.service.impl;

import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.mapper.JudgeServerMapper;
import cn.imustacm.problem.mapper.ProblemMapper;
import cn.imustacm.problem.model.JudgeServer;
import cn.imustacm.problem.service.JudgeServerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务实现类
 *
 * @author wangjianli
 * @since 2019-10-09
 *
 */
@Service
public class JudgeServerServiceImpl extends ServiceImpl<JudgeServerMapper, JudgeServer> implements JudgeServerService {

    public JudgeServer getJudgeServer() {
        LambdaQueryWrapper<JudgeServer> wrapper = new QueryWrapper<JudgeServer>().lambda().eq(JudgeServer::getVisible, true).orderByAsc(JudgeServer::getId);
        return chooseJudgeServer(list(wrapper));
    }

    public JudgeServer chooseJudgeServer(List<JudgeServer> judgeServers) {
        return judgeServers.get(0);
    }

}
