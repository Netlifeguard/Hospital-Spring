package com.nie.common.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class MqProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendWithSimple(String mqexchange, String mqroutingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(mqexchange, mqroutingKey, message, message1 -> {
                message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message1;
            }, new CorrelationData(UUID.randomUUID().toString()));
        } catch (Exception e) {
            log.error("发送消息失败：{}", e.getMessage());
        }
    }


    public void sendWithRetry(String mqexchange, String mqroutingKey, Object message) {
        final CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(mqexchange, mqroutingKey, message, message1 -> {
            message1.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message1;
        }, correlationData);

        rabbitTemplate.setConfirmCallback((correlationData1, ack, cause) -> {
            if (ack) {
                log.info("投递到交换机成功,确认情况：{}", ack);
            } else {
                log.info("投递到交换机失败,确认情况：{}，原因：{}", ack, cause);
            }
        });

        rabbitTemplate.setReturnCallback((msg, replyCode, replyText, exchange, routingKey) -> {
            log.info(" 投递到队列 消息：{},回应码：{},回应信息：{},交换机：{},路由键：{}"
                    , msg, replyCode
                    , replyText, exchange
                    , routingKey);
        });
    }
}
