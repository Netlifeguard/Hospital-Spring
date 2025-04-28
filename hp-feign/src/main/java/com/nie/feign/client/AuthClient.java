package com.nie.feign.client;

import com.nie.feign.dto.BaseType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("hp-auth")
public interface AuthClient {
    @PostMapping("/auth/login")
    Map<String, String> login(
            @RequestParam("Id") int Id,
            @RequestParam("Password") String Password,
            @RequestParam("captcha") String captcha,
            @RequestParam("role") String role,
            @RequestParam("user") String user
    );
}
