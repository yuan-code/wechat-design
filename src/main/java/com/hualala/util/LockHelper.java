package com.hualala.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * @author YuanChong
 * @create 2019-08-26 17:09
 * @desc
 */
@Component
public class LockHelper {

    @Autowired
    private CuratorFramework client;

    private final String rootPath = "/distribution/lock/";

    /**
     * 同步代码块
     *
     * @param path 锁定的资源
     * @param supplier 回调消费者
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T doSync(String path, Supplier<T> supplier) throws Exception {
        InterProcessMutex lock = null;
        try {
            lock = new InterProcessMutex(client, rootPath + path);
            lock.acquire();
            return supplier.get();
        } finally {
            if (lock != null && lock.isAcquiredInThisProcess()) {
                lock.release();
            }
        }
    }

    public InterProcessLock getLock(String path) {
        return new InterProcessMutex(client, rootPath + path);
    }

}
