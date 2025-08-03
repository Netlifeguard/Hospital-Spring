package com.nie.coupons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.nie.feign.client")
@EnableCaching
public class ConponsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConponsApplication.class, args);
    }
}
