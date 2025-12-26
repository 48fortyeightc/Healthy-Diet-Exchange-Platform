package com.healthy.diet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.healthy.diet.mapper")
public class DietPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(DietPlatformApplication.class, args);
    }
}



