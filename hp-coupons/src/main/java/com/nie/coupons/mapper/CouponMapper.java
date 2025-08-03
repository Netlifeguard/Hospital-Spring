package com.nie.coupons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.feign.dto.Coupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author nie
 * @since 2025-01-24
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

}
