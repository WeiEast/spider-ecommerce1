package com.datatrees.rawdatacentral.service.lock;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 分布式锁工厂
 * @author Jerry
 * @version 1.0.1
 * @since 1.0.1 [14:39, 11/3/15]
 */
@Component
public class DistributedLocks {

    private final RedisTemplate<String, Long> redisTemplate;

    @Autowired
    public DistributedLocks(RedisTemplate<String, Long> redisTemplate) {this.redisTemplate = redisTemplate;}

    public DistributedLock newLock(String key) {
        return new DistributedLock(redisTemplate, key);
    }

    /**
     * running <code>runnable</code> in lock state.
     * @param lockName the lock name
     * @param timeout  the maximum time to wait for the lock
     * @param unit     the unit used for timeout
     * @param runnable the function running in lock.
     * @exception InterruptedException
     * @exception LockingFailureException
     */
    public void doInLock(String lockName, long timeout, TimeUnit unit, Runnable runnable) throws InterruptedException, LockingFailureException {
        DistributedLock lock = newLock(lockName);
        if (!lock.tryLock(timeout, unit)) {
            throw new LockingFailureException("Can not acquire the lock. lockName: " + lockName);
        }

        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}
