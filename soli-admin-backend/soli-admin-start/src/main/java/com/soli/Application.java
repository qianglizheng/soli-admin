package com.soli;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.soli.auth.api.service.AuthService;

/**
* @author lizhengqiang
* @since 2026-03-04 22:08
*/
@SpringBootApplication(scanBasePackages = "com.soli")
@MapperScan(basePackages = "com.soli.**.mapper")
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        AuthService bean = run.getBean(AuthService.class);
    }
}