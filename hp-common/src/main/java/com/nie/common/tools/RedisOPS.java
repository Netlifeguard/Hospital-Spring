package com.nie.common.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisOPS {
    private final RedisTemplate<String, Object> redisTemplate;

    // 设置缓存
    public <T> void setCacheObject(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 设置缓存并指定超时时间
    public <T> void setCacheObject(String key, T value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES);
    }

    // 获取缓存
    public <T> T getCacheObject(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }


    public <T> void setCacheListAll(String key, T value) {
        redisTemplate.opsForList().rightPushAll(key, value);
    }

    public <T> void setCacheList(String key, T value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public <T> List<T> getCacheList(String key, Class<T> clazz) {
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        ArrayList<T> list = new ArrayList<>();
        for (Object object : range) {
            list.add(clazz.cast(object));
        }
        return list;
    }


    public <T> boolean setCacheSortedSet(String key, T value, long score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public <T> Set<T> getCacheSortedSet(String key, Class<T> clazz) {
        Set<Object> objects = redisTemplate.opsForZSet().reverseRange(key, 0, -1); // 获取集合中所有元素
        Set<T> result = new HashSet<>();

        for (Object obj : objects) {
            result.add(clazz.cast(obj));  // 将对象转换为目标类型
        }

        return result;
    }


    // 删除缓存
    public boolean deleteObject(String key) {
        final boolean delete = redisTemplate.delete(key);
        return delete;
    }

    // 删除 ZSet 中的某个成员
    public <T> long deleteSortedSet(String key, T value) {

        return redisTemplate.opsForZSet().remove(key, value);  // 根据 value 删除 ZSet 中的元素
    }


    public <T> void setObjectForHash(String key, String hashKey, T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public <T> T getObjectForHash(String key, String hashKey) {
        return (T) redisTemplate.opsForHash().get(key, hashKey);
    }

    public void removeObjectForHash(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
    }

    public Long executeScript(String script, String key, Object... args) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script); // 设置 Lua 脚本
        redisScript.setResultType(Long.class); // 设置返回类型

        return redisTemplate.execute(redisScript, Collections.singletonList(key), args);
    }

    public Long executeScriptForHash(String script, String key, String field, Object... args) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script); // 设置 Lua 脚本
        redisScript.setResultType(Long.class); // 设置返回类型

        // 将 key 和 args（字段名，增量）传递给脚本
        return redisTemplate.execute(redisScript,
                Collections.singletonList(key),
                field, args);
    }


}
