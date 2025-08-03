package com.nie.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.nie.feign.client")
@SpringBootApplication
public class CheckApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckApplication.class, args);
    }
}
