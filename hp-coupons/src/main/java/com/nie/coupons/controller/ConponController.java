package com.nie.coupons.controller;

import com.nie.common.tools.MQConstant;
import com.nie.common.tools.RedisKeyPrefix;
import com.nie.common.tools.RedisOPS;
import com.nie.common.tools.Result;
import com.nie.coupons.service.ICouponService;
import com.nie.coupons.service.IUserCouponService;
import com.nie.feign.dto.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class ConponController {
    private final IUserCouponService userCouponService;

    private final ICouponService couponService;

    private final RedisOPS redisOPS;

    private final RedissonClient redissonClient;

    @GetMapping("/total")
    //@Cacheable(value = RedisKeyPrefix.User_coupon_nums,key = "'user:' + #userId",sync = true)
    public Result getTotal(String userId) {
        final RMap<String, String> map = redissonClient.getMap(RedisKeyPrefix.User_coupon_nums);
        if (map.containsKey(RedisKeyPrefix.User + userId)) {
            return Result.success(map.get(RedisKeyPrefix.User + userId));
        }
        return Result.success(userCouponService.getTotal(userId));
    }

    @GetMapping("/point")
    public Result getPoint(String userId) {
        final RMap<String, Integer> map = redissonClient.getMap(RedisKeyPrefix.User_point_all);
        if (map.containsKey(RedisKeyPrefix.User + userId)) {
            return Result.success(map.get(RedisKeyPrefix.User + userId));
        }
        final Integer point = userCouponService.getPoint(userId);
        return Result.success(point);
    }

    @GetMapping("/all")
    public Result getAllCoupons() {
        return Result.success(couponService.getAllCoupons());
    }

    @GetMapping("/couponNum")
    public Result getCouponNum() {
        final Object hash = redisOPS.getCacheObject(RedisKeyPrefix.Coupon_nums);
        if (hash != null) {
            return Result.success(hash);
        }
        final long num = couponService.getCouponNum();
        redisOPS.setCacheObject(RedisKeyPrefix.Coupon_nums, num);
        return Result.success(num);
    }

    @PostMapping("/publish")
    public Result publish(@RequestBody Coupon coupon) {
        final boolean save = couponService.save(coupon);
        if (save) {
            log.info("publish id {}", coupon.getCouponId());
            redisOPS.executeScript(MQConstant.scriptLuaIncrement, RedisKeyPrefix.Coupon_nums, 1);
            final RMap<String, Integer> map = redissonClient.getMap(RedisKeyPrefix.Coupon_limit_num);
            map.put(RedisKeyPrefix.Coupon + coupon.getCouponId(), coupon.getLimitNumber());
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }

    @DeleteMapping("/deleteCoupon")
    public Result deleteCoupon(Integer couponId) {
        final boolean removeById = couponService.removeById(couponId);
        if (removeById) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

}
