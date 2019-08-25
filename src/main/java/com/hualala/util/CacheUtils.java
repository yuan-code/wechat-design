package com.hualala.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author YuanChong
 * @create 2019-06-26 23:10
 * @desc
 */
@Component
public class CacheUtils {

    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        CacheUtils.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public static void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0
     * @return true成功 false 失败
     */
    public static void set(String key, String value, long time) {
        stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true成功 false 失败
     */
    public static boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param time
     */
    public static void expire(String key, long time) {
        stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        return key == null ? null : stringRedisTemplate.opsForValue().get(key);
    }


    public static void zAdd(String key, String value, Double score) {
        stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 从小到大
     *
     * @param key
     * @return
     */
    public static Set<String> zRangeAll(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }


    /**
     * 从大到小
     *
     * @param key
     * @return
     */
    public static Set<String> zRangeRevertAll(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 从大到小
     *
     * @param key
     * @return
     */
    public static Long zSize(String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }


    /**
     * 从大到小
     *
     * @param key
     * @return
     */
    public static Long zDel(String key) {
        return stringRedisTemplate.opsForZSet().removeRange(key, 0, -1);
    }
}
