package cn.imustacm.problem.service;

import cn.imustacm.problem.model.Submission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 服务类
 *
 * @author liandong
 * @since 2019-10-11
 */
public interface SubmissionService extends IService<Submission> {

    /**
     * 分页查询提交信息
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<Submission> getList(Integer pageIndex, Integer pageSize);

    /**
     * 查询提交信息总数
     *
     * @return
     */
    Integer getListCount();

}
