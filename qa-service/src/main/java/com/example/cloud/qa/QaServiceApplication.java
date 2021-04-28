package com.example.cloud.qa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuyanzhao
 */
@SpringBootApplication
@MapperScan("com.example.cloud.qa.dao")
@EnableFeignClients(basePackages = "com.example.cloud.user.api.feign")
public class QaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QaServiceApplication.class, args);
    }

}
