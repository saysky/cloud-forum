package com.example.cloud.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author liuyanzhao
 */
@SpringBootApplication
@MapperScan("com.example.cloud.article.dao")
//@ComponentScan("com.example.cloud")
@EnableFeignClients(basePackages = "com.example.cloud.user.api.feign")
public class ArticleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleServiceApplication.class, args);
    }

}
