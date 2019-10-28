package cn.imustacm.problem.service;


import cn.imustacm.problem.dto.ProblemListDTO;
import cn.imustacm.problem.mapper.ProblemMapper;
import cn.imustacm.problem.model.Problem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 服务类
 *
 * @author liandong
 * @since 2019-09-10
 */
public interface ProblemService extends IService<Problem> {

    /**
     * 分页查询题目列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<ProblemListDTO> getProblemsByPage(Integer pageIndex, Integer pageSize);

    /**
     * 获取题目总数
     *
     * @return
     */
    Integer getProblemTotalNumber();

    /**
     * 根据id获取题目信息
     *
     * @param id
     * @return
     */
    Problem getProblemById(Integer id);

}
