package com.nie.feign.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyCoupon {

    private Coupon coupon;
    private Integer userId;

    @JsonCreator
    public MyCoupon(@JsonProperty("coupon") Coupon coupon, @JsonProperty("userId") Integer userId) {
        this.coupon = coupon;
        this.userId = userId;
    }
}
