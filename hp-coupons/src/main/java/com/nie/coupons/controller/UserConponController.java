package com.nie.coupons.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.nie.common.tools.*;
import com.nie.coupons.pojo.UseCouponDTO;
import com.nie.coupons.service.ICouponService;
import com.nie.coupons.service.IUserCouponService;
import com.nie.feign.dto.Coupon;
import com.nie.feign.dto.UserCoupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/userCoupon")
@RequiredArgsConstructor
public class UserConponController {
    private final IUserCouponService userCouponService;
    private final MqProducer mqProducer;
    private final Gson gson;
    private final ICouponService couponService;
    private final RedissonClient redissonClient;
    public static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    /**
     * 用户领取优惠券
     */
    @PostMapping("/getCoupon")
    public Result getCoupon(@RequestBody Coupon coupon) throws Exception {
        System.out.println(coupon.toString());
        mqProducer.sendWithRetry(MQConstant.EXCHANGE_USER_COUPON, MQConstant.ROUTING_KEY_USER_COUPON, coupon);
        return Result.success();
    }

    @GetMapping("/realGet")
    public boolean realGet(String couponJson) throws Exception {
        final UserCoupon userCoupon = gson.fromJson(couponJson, UserCoupon.class);
        final boolean b = userCouponService.save(userCoupon);
        return b;
    }

    @GetMapping("/getUserCoupon")
    public Result getUserCoupon(String userId) throws Exception {
        LambdaQueryWrapper<UserCoupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(UserCoupon::getCouponId)
                .eq(UserCoupon::getUserId, userId);

        final List<UserCoupon> list = userCouponService.list(lambdaQueryWrapper);

        final List<Integer> cip = list.stream()
                .map(UserCoupon::getCouponId)
                .collect(Collectors.toList());

        final List<Coupon> coupons = couponService.listByIds(cip);
        log.info("用户优惠券：{}", cip);
        return Result.success(coupons);
    }

    @GetMapping("/useCoupon")
    public ArrayList<Integer> useCoupon(@RequestParam("couponIds") String couponIds) throws Exception {
        final String[] split = couponIds.split(",");
        final ArrayList<Integer> collect = new ArrayList<>();
        Arrays.stream(split).forEach(x -> collect.add(Integer.parseInt(x)));
        LambdaQueryWrapper<Coupon> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Coupon::getCouponId, collect);
        final List<Coupon> cs = couponService.list(lambdaQueryWrapper);

        // 使用 CopyOnWriteArrayList 替代 ArrayList 以保证线程安全
        final ArrayList<Integer> faces = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(collect.size());

        // 提交任务
        for (Coupon coupon : cs) {
            threadPool.execute(() -> {
                try {
                    checkCoupon(coupon, faces, latch);
                    userCouponService.removeById(coupon.getCouponId());
                } catch (Exception e) {
                    log.error("优惠券核销失败", e);  // 输出整个异常堆栈
                }
            });
        }
        // 等待所有线程执行完
        try {
            latch.await();
            log.info("优惠券核销结果：{}", faces);
            return faces;
        } catch (InterruptedException e) {
            log.error("等待线程执行异常", e);  // 记录线程等待异常
            Thread.currentThread().interrupt();  // 恢复中断状态
        } finally {
            // 关闭线程池
            threadPool.shutdown();
        }
        return new ArrayList<>();
    }

    void checkCoupon(Coupon coupon, List<Integer> faces, CountDownLatch latch) throws Exception {
        final RLock lock = redissonClient.getLock("lock_" + coupon.getCouponId());
        try {
            // 尝试获取锁，默认超时 10 秒
            lock.lock(30, TimeUnit.SECONDS);
            String expiryTime = coupon.getExpiryTime();
            String datePart = expiryTime.substring(0, 10); // 取 "2025-02-02"
            LocalDate parse = LocalDate.parse(datePart);

            if (parse.isBefore(LocalDate.now())) {
                log.info("优惠券已过期");
            } else if (coupon.getCouponStatus() == 0) {
                log.info("优惠券不可用");
            } else {
                faces.add(coupon.getDiscountAmount());  // 记录核销的优惠券
            }
        } catch (Exception e) {
            log.error("核销优惠券失败", e);
        } finally {
            latch.countDown();  // 减少 CountDownLatch 的计数
            lock.unlock();  // 确保锁被释放
        }
    }


}
