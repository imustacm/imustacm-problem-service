package cn.imustacm.problem.service;

import cn.imustacm.problem.model.ProblemTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 服务类
 *
 * @author liandong
 * @since 2019-09-10
 */
public interface ProblemTagService extends IService<ProblemTag> {

    /**
     * 获取所有题目标签
     *
     * @return
     */
    List<ProblemTag> listTags();

}
