package com.nie.coupons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nie.coupons.mapper.CouponMapper;
import com.nie.coupons.service.ICouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.feign.dto.Coupon;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nie
 * @since 2025-01-24
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    @Override
    public long getCouponNum() {
        LambdaQueryWrapper<Coupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Coupon::getCouponId);
        return this.count(lambdaQueryWrapper);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        final List<Coupon> coupons = this.list();
        return coupons;
    }
}
