package cn.imustacm.problem.feign;

import cn.imustacm.common.domain.Resp;
import cn.imustacm.problem.model.Problem;
import cn.imustacm.problem.service.IProblemService;
import cn.imustacm.problem.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ProblemServiceFeign implements IProblemService {

    @Autowired
    private ProblemService problemService;

    @Override
    public Resp addProblem(Problem problem) {
        log.info("addProblem begin! problem:{}", problem);
        return Resp.ok(problemService.saveOrUpdate(problem));
    }

    @Override
    public Problem getProblemById(Long id) {
        return problemService.getById(id);
    }
}
