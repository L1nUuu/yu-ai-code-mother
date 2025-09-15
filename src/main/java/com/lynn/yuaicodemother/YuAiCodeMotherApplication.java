package com.lynn.yuaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lynn.yuaicodemother.mapper")
public class YuAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuAiCodeMotherApplication.class, args);
    }

}
