package cn.imustacm.problem.service;


import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.model.JudgeServer;
import cn.imustacm.problem.model.Problem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 服务类
 *
 * @author wangjianli
 * @since 2019-10-09
 *
 */
public interface JudgeServerService extends IService<JudgeServer> {

    /**
     * 查询判题机列表
     *
     * @param
     * @return
     */
    public JudgeServer getJudgeServer();
}
