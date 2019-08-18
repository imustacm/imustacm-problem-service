package cn.imustacm.problem.controller;

import cn.imustacm.common.domain.Resp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author liandong
 * @date 2019/08/18
 */
@RestController
@RequestMapping("/demp")
public class DemoController {
    @GetMapping("hello")
    public Resp helloWorld() {
        return Resp.ok("Hello World");
    }
}
