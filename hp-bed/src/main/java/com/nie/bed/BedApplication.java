package com.nie.bed;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nie.bed.mapper")
public class BedApplication {
    public static void main(String[] args) {
        SpringApplication.run(BedApplication.class, args);
    }
}
