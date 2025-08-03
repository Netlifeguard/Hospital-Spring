package com.nie.coupons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.coupons.pojo.UseCouponDTO;
import com.nie.feign.dto.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author nie
 * @since 2025-01-24
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    @Select("select Point from user_point where User_Id = #{userId}")
    Integer getPoint(String userId);

}
