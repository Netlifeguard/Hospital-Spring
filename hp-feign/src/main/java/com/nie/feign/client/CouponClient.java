package com.nie.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@FeignClient("hp-coupons")
public interface CouponClient {
    @GetMapping("/userCoupon/realGet")
    boolean realGet(@RequestParam String couponJson);

    @GetMapping("/userCoupon/useCoupon")
    ArrayList<Integer> useCoupon(@RequestParam("couponIds") String couponIds);
}
