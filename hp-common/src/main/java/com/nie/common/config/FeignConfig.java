package com.nie.common.config;

import com.nie.common.interceptor.UserInfoInterceptor;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {
    @Bean
    RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
                final HttpServletRequest request = attributes.getRequest();
                requestTemplate.header("Authorization", request.getHeader("Authorization"));
            }
        };
    }
}
