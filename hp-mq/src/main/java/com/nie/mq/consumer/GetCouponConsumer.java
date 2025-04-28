package com.nie.mq.consumer;

import com.google.gson.Gson;
import com.nie.common.tools.MQConstant;
import com.nie.common.tools.MqProducer;
import com.nie.common.tools.RedisKeyPrefix;
import com.nie.common.tools.RedisOPS;
import com.nie.feign.client.CouponClient;
import com.nie.feign.dto.Coupon;
import com.nie.feign.dto.Sysmessage;
import com.nie.feign.dto.UserCoupon;
import com.nie.mq.service.NoticeService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetCouponConsumer {
    private final RedisOPS redisOPS;
    private final NoticeService noticeService;
    private final MqProducer mqProducer;
    private final CouponClient couponClient;
    private final Gson gson;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.QUEUE_USER_COUPON, durable = "true"),
            exchange = @Exchange(name = MQConstant.EXCHANGE_USER_COUPON, type = ExchangeTypes.DIRECT),
            key = MQConstant.ROUTING_KEY_USER_COUPON
    ))
        //@CacheEvict(value = RedisKeyPrefix.User_coupon_nums, key = "'user:' + #coupon.userId")
    void getCoupon(Coupon coupon, Channel channel, Message message) throws Exception {
        final Integer userId = coupon.getUserId();
        log.info("receive msg：{}", coupon);
        String expiryTime = coupon.getExpiryTime();
        String datePart = expiryTime.substring(0, 10); // 取 "2025-02-02"
        LocalDate parse = LocalDate.parse(datePart);

        final Sysmessage sysmessage = new Sysmessage();
        sysmessage.setTarget("公开");
        sysmessage.setSendTime(LocalDateTime.now().toString());
        sysmessage.setSenderId(55555);

        // 判断优惠券是否过期
        if (parse.isBefore(LocalDate.now())) {
            log.info("优惠券已过期");
            sysmessage.setMsgtitle("优惠券已过期");
            sysmessage.setMsgContent("优惠券已过期,无法领取");
        }
        // 判断优惠券是否不可用
        else if (coupon.getCouponStatus() == 0) {
            log.info("优惠券不可用");
            sysmessage.setMsgtitle("优惠券不可用");
            sysmessage.setMsgContent("优惠券不可用,无法领取");
        }
        // 判断优惠券是否已领完
        else if (coupon.getIsLimited() == 1) {
            final List<Integer> userGeted = redisOPS.getCacheList(RedisKeyPrefix.Coupon_already_get + coupon.getCouponId(), Integer.class);
            final long forHash = (long) redisOPS.getObjectForHash(RedisKeyPrefix.Coupon_limit_num, RedisKeyPrefix.Coupon + coupon.getCouponId());
            final long getNum = userGeted.stream().filter(id -> id.equals(userId)).count();
            if (getNum == forHash) {
                log.info("优惠券已领完");
                sysmessage.setMsgtitle("优惠券已领完");
                sysmessage.setMsgContent("优惠券已领完,无法领取");
            }
        }
        // 如果上述条件都不满足，执行领取优惠券操作
        else {
            final UserCoupon userCoupon = new UserCoupon();
            userCoupon.setCouponId(coupon.getCouponId());
            userCoupon.setUserId(userId);
            userCoupon.setGetTime(LocalDate.now().toString());
            final String couponJson = gson.toJson(userCoupon);
            final boolean b = couponClient.realGet(couponJson);

            if (b) {
                log.info("realGet success");
                redisOPS.setCacheList(RedisKeyPrefix.Coupon_already_get + coupon.getCouponId(), userId);
                // 原子化扣减总量
                String luaKey = RedisKeyPrefix.Coupon_nums;
                redisOPS.executeScript(MQConstant.scriptLuaDecrement, luaKey, 1);
                sysmessage.setMsgtitle("优惠券领取成功");
                sysmessage.setMsgContent("优惠券领取成功");

                redisOPS.setCacheList(RedisKeyPrefix.User_coupon + userId, coupon);
            } else {
                log.info("realGet fail");
            }
        }

        mqProducer.sendWithRetry(MQConstant.EXCHANGE_SYS_MESSAGE, MQConstant.ROUTING_KEY_SYS_MESSAGE, sysmessage);
    }
}
