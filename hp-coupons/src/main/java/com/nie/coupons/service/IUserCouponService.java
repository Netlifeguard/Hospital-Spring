package com.nie.coupons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nie.coupons.pojo.UseCouponDTO;
import com.nie.feign.dto.UserCoupon;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nie
 * @since 2025-01-24
 */
public interface IUserCouponService extends IService<UserCoupon> {

    int getTotal(String userId);

    Integer getPoint(String userId);


}
