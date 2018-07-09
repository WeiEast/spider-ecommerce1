package com.datatrees.rawdatacentral.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 换成spring boot了
 * Created by zhouxinghai on 2017/7/3
 */
@SpringBootApplication
@ImportResource("classpath:spring.xml")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
