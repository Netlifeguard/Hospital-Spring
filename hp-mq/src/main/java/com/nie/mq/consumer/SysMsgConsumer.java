package com.nie.mq.consumer;

import com.nie.common.tools.MQConstant;
import com.nie.common.tools.RedisKeyPrefix;
import com.nie.common.tools.RedisOPS;
import com.nie.common.tools.UserContest;
import com.nie.feign.dto.Sysmessage;
import com.nie.mq.service.NoticeService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class SysMsgConsumer {
    private final NoticeService noticeService;
    private final RedisOPS redisOPS;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstant.QUEUE_SYS_MESSAGE, durable = "true"),
            exchange = @Exchange(name = MQConstant.EXCHANGE_SYS_MESSAGE, type = ExchangeTypes.DIRECT),
            key = MQConstant.ROUTING_KEY_SYS_MESSAGE
    ))
    public void receive(Sysmessage sysmessage, Channel channel, Message message) throws IOException {
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            Objects.requireNonNull(sysmessage);
            noticeService.addNotice(sysmessage);

            //插入数据库后再加入缓存
            LocalDateTime ldtime = LocalDateTime.parse(sysmessage.getSendTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            final long score = ldtime.toInstant(ZoneOffset.UTC).toEpochMilli();
            System.out.println(sysmessage.getTarget());

            if ("公开".equals(sysmessage.getTarget())) {
                final boolean b = redisOPS.setCacheSortedSet(RedisKeyPrefix.System_msg_target_public, sysmessage, score);
                redisOPS.expire(RedisKeyPrefix.System_msg_target_public, 60 * 60 * 24 * 7);
                System.out.println(b);
            }
            if ("医生".equals(sysmessage.getTarget())) {
                redisOPS.setCacheSortedSet(RedisKeyPrefix.System_msg_target_doctor, sysmessage, score);
                redisOPS.expire(RedisKeyPrefix.System_msg_target_doctor, 60 * 60 * 24 * 7);
            }
            //channel.basicAck(deliveryTag, false)
            log.info("接收到消息：{}", sysmessage);
        } catch (Exception e) {
            // channel.basicNack(deliveryTag, false, false);
            log.info("接收到消息失败：{}", e.getMessage());
        }
    }

}
