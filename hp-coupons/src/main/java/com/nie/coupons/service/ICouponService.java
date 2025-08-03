package com.nie.coupons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nie.feign.dto.Coupon;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nie
 * @since 2025-01-24
 */
public interface ICouponService extends IService<Coupon> {

    List<Coupon> getAllCoupons();

    long getCouponNum();

}
