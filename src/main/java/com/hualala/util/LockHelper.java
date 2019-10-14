package com.hualala.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author YuanChong
 * @create 2019-08-26 17:09
 * @desc
 */
@Component
public class LockHelper {


    private static final String ROOT_PATH = "distributionlock:%s";

    /**
     * 锁过期时间 5分钟
     */
    private static final int LOCK_TIMEOUT = 60 * 5;

    /**
     * 锁等待时间30秒
     */
    private static final int WAIT_TIMEOUT = 30;

    private static final ThreadLocal<String> LOCK_HOLDER = new ThreadLocal<>();

    private static final String UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private static final RedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(UNLOCK_LUA, Long.class);


    /**
     * 同步代码块
     *
     * @param resource 锁定的资源
     * @param supplier 回调消费者
     * @param <T>
     * @return
     * @throws InterruptedException
     */
    public <T> T doSync(String resource, Supplier<T> supplier) throws InterruptedException {
        try {
            lock(resource);
            return supplier.get();
        } finally {
            unlock(resource);
        }
    }


    public static String getLockKey(String resource) {
        return String.format(ROOT_PATH, resource);
    }

    /**
     * 设置锁的持有人
     *
     * @return
     */
    public static String cacheLockHolder() {
        String lockHolder = LOCK_HOLDER.get();
        //持有锁人唯一标识
        if (StringUtils.isEmpty(lockHolder)) {
            lockHolder = UUID.randomUUID().toString();
            LOCK_HOLDER.set(lockHolder);
        }
        return lockHolder;
    }

    /**
     * 加锁 锁默认等待10秒
     *
     * @param resource
     * @return
     * @throws InterruptedException
     */
    public boolean lock(String resource) throws InterruptedException {
        int waitTime = WAIT_TIMEOUT * 1000;
        while (waitTime > 0) {
            if (tryLock(resource)) {
                return true;
            }
            waitTime = waitTime - 100;
            TimeUnit.MILLISECONDS.sleep(100);
        }
        throw new RuntimeException("系统繁忙, 上次请求数据正在处理中, 请稍后再试");
    }

    /**
     * trylock
     *
     * @param resource
     * @return 成功 or 失败
     */
    public boolean tryLock(String resource) {
        String lockKey = getLockKey(resource);
        String lockHold = cacheLockHolder();
        return CacheUtils.setNx(lockKey, lockHold, LOCK_TIMEOUT);
    }


    /**
     * 释放分布式锁
     *
     * @param resource 资源
     * @return 是否释放成功
     */
    public boolean unlock(String resource) {
        String lockKey = getLockKey(resource);
        String lockHolder = LOCK_HOLDER.get();
        if (StringUtils.isEmpty(lockHolder)) {
            throw new IllegalStateException("unlock must after at lock");
        }
        Long result = CacheUtils.eval(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockHolder);
        boolean success = result != null && result == 1;
        if(success) {
            removeLockHolder();
        }
        return success;
    }

    /**
     * 线程结束后释放资源
     */
    public static void removeLockHolder() {
        LOCK_HOLDER.remove();
    }

}
