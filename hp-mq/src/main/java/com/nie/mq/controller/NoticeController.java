package com.nie.mq.controller;

import com.nie.common.tools.*;
import com.nie.feign.dto.Sysmessage;
import com.nie.mq.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    private final RedisOPS redisOPS;

    @GetMapping("/findAllNotice")
    public Result findAllNotice() {
//       final long maxT = System.currentTimeMillis();
//       final long minT = maxT - 1000L * 60 * 60 * 24;
        final Set<Sysmessage> set = redisOPS.getCacheSortedSet(RedisKeyPrefix.System_msg_target_public, Sysmessage.class);
        if (set.size() > 0) {
            log.info("from redis");
            return Result.success(set);
        }
        log.info("from db");
        final List<Sysmessage> allNotice = noticeService.findAllNotice();
        return Result.success(allNotice);
    }

    @DeleteMapping("/deleteNotice/{msgId}")
    public Result deleteNotice(@PathVariable("msgId") int msgId) {
        final int i = noticeService.removeById(msgId);
        String key = RedisKeyPrefix.System_msg_id + msgId;
        redisOPS.deleteObject(key);

        final Set<Sysmessage> set = redisOPS.getCacheSortedSet(RedisKeyPrefix.System_msg_target_public, Sysmessage.class);
        for (Sysmessage msg : set) {
            if (msg.getMsgId() == msgId) {
                redisOPS.deleteSortedSet(RedisKeyPrefix.System_msg_target_public, msg);
                log.info("delete from redis");
                break;
            }
        }
        if (i == 0) {
            return Result.error("删除失败");
        }
        return Result.success("删除成功");
    }
}
