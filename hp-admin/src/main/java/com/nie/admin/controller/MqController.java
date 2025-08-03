package com.nie.admin.controller;

import com.nie.common.tools.*;
import com.nie.feign.dto.Sysmessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MqController {
    private final MqProducer mqProducer;

    private final RedisOPS redisOPS;


    @PostMapping("/addNotice")
    public Result addNotice(@RequestBody Sysmessage sysmessage) {
        log.info("{}  add Notice", UserContest.getUserId());

        final Sysmessage msg = new Sysmessage();
        BeanUtils.copyProperties(sysmessage, msg);
        msg.setSenderId(UserContest.getUserId());
        msg.setMsgStatus(0);
        msg.setSendTime(LocalDateTime.now().toString());

        mqProducer.sendWithRetry(MQConstant.EXCHANGE_SYS_MESSAGE, MQConstant.ROUTING_KEY_SYS_MESSAGE, msg);


        return Result.success();
    }

}
