package com.nie.admin;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nie.common.tools.JwtUtil;

import com.nie.common.tools.RedisKeyPrefix;
import com.nie.common.tools.RedisOPS;
import com.nie.feign.dto.Sysmessage;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RSet;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;


import java.util.*;

@SpringBootTest
public class RedisTestApplication {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisOPS redisOPS;

    @Test
    public void redissonTest() {
        RSet<String> set = redissonClient.getSet("mySet");

        // 添加元素
        set.add("value1");
        set.add("value2");
        set.add("value3");

        // 检查元素是否存在
        boolean contains = set.contains("value1");
        System.out.println("Contains value1: " + contains);

        // 删除元素
        set.remove("value2");

        // 输出集合的所有元素
        System.out.println("Set contents: " + set.readAll());
    }

    @Test
    public void testHash() {
        redisOPS.setObjectForHash(RedisKeyPrefix.Channel_group, "ok", 777);
        final Object ok = redisOPS.getObjectForHash(RedisKeyPrefix.Channel_group, "ok");
        System.out.println(ok);
    }

    @Test
    public void test0() {
        final Set<Sysmessage> set = redisOPS.getCacheSortedSet(RedisKeyPrefix.System_msg_target_public, Sysmessage.class);
        System.out.println(set);

    }

    @Test
    public void test1() {

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkTmFtZSI6IuWImOeLlyIsImV4cCI6MTczNDI0NzI1MiwiZElkIjoiMjAxOTAwIn0.UIwzo2kXslDxJYge1uqbsIYMBoAxVssRDjobdOJlGmI";
        DecodedJWT decodedJWT = JwtUtil.verify(token);
        String s = decodedJWT.getClaim("dId").asString();
        System.out.println(s);

    }

    @Test
    public void test2() {
//        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("2023012024-11-17");
//        Set<Object> objects = entries.keySet();
//        for (Object k: objects) {
//            System.out.println(entries.get(k));
//        }
        String s = "2024-11-17 16:30-17:30";
        String[] s1 = s.split(" ");
        System.out.println(s1[1]);
    }

    @Test
    public void test3() {
        String hKey = "hp:workTime";

        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(hKey);
        if (map.isEmpty()) {
            map = new HashMap<>();
            map.put("mT1", "20");
            map.put("mT2", "20");
            map.put("mT3", "20");
            map.put("aT4", "20");
            map.put("aT5", "20");
            map.put("aT6", "20");

            stringRedisTemplate.opsForHash().putAll(hKey, map);
            System.out.println("success");
        } else {
            System.out.println("not null");
        }
    }

    @Test
    public void test4() {
        List<Integer> list = Arrays.asList(1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008);
        redisTemplate.opsForList().rightPushAll("LuaTest", list);
        Object leftPop = redisTemplate.opsForList().leftPop("LuaTest");
        List<Integer> res = null;
        if (leftPop instanceof List) {
            res = (List<Integer>) leftPop;
        }
        System.out.println(res);

    }


}
