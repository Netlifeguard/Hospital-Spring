package com.nie.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperConfig {
    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 Java 8 时间支持模块
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
