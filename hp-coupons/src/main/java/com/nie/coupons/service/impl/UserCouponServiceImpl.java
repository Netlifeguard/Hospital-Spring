package com.nie.coupons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nie.common.tools.RedisKeyPrefix;
import com.nie.common.tools.RedisOPS;
import com.nie.coupons.mapper.UserCouponMapper;
import com.nie.coupons.pojo.UseCouponDTO;
import com.nie.coupons.service.IUserCouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.feign.dto.UserCoupon;
import lombok.RequiredArgsConstructor;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nie
 * @since 2025-01-24
 */
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements IUserCouponService {
    private final RedisOPS redisOPS;
    private final RedissonClient redissonClient;

    @Override
    public int getTotal(String userId) {
        LambdaQueryWrapper<UserCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserCoupon::getUserId, userId);
        final long count = this.count(lambdaQueryWrapper);
        final RMap<String, Long> map = redissonClient.getMap(RedisKeyPrefix.User_coupon_nums);
        map.put(RedisKeyPrefix.User + userId, count);
        map.expire(1, TimeUnit.DAYS);
        return (int) count;
    }

    @Override
    public Integer getPoint(String userId) {
        Integer point = this.baseMapper.getPoint(userId);
        if (point == null) {
            point = 0;
        }
        final RMap<String, Integer> map = redissonClient.getMap(RedisKeyPrefix.User_point_all);
        map.put(RedisKeyPrefix.User + userId, point);
        map.expire(1, TimeUnit.DAYS);
        return point;
    }


}
