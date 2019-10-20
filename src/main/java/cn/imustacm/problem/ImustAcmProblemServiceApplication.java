package cn.imustacm.problem;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableSwagger2Doc
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ImustAcmProblemServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImustAcmProblemServiceApplication.class, args);
    }

}
