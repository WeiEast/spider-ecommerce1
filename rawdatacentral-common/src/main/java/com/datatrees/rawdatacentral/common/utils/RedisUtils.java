package com.datatrees.rawdatacentral.common.utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.common.config.RedisConfig;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils extends RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    private static JedisPool jedisPool;

    public static void init() {
        try {
            if (null == jedisPool) {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(maxTotal);
                config.setMaxIdle(maxIdle);
                config.setMinIdle(minIdle);
                config.setMaxWaitMillis(maxWait);
                config.setTestOnBorrow(true);
                if (StringUtils.equals("", password)) {
                    password = null;
                }
                jedisPool = new JedisPool(config, host, port, timeout, password);
            }

        } catch (Exception e) {
            throw new RuntimeException("redis init error ");
        }
    }

    public static void init(String host, int port, String password) {
        try {
            RedisConfig.host = host;
            RedisConfig.port = port;
            RedisConfig.password = password;
            init();
        } catch (Exception e) {
            throw new RuntimeException("redis init error ");
        }
    }

    public static Boolean exists(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    public static Long expire(final String key, final int second) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, second);
        }
    }

    public static Boolean hexists(final String key, final String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hexists(key, field);
        }
    }

    public static String type(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.type(key);
        }
    }

    public static String get(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public static Map<String, String> hgetAll(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    public static String hget(final String key, final String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        }
    }

    public static long hdel(final String key, final String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, field);
        }
    }

    public static String set(final String key, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    public static Long hset(final String key, final String field, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, field, value);
        }
    }

    public static String setex(RedisKeyPrefixEnum keyEnum, Object prefix, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.psetex(keyEnum.getRedisKey(prefix), keyEnum.getTimeUnit().toMillis(keyEnum.getTimeout()), value);
        }
    }

    /**
     * 如果不存在,设置value
     * @param key
     * @param value
     * @return
     */
    public static Boolean setnx(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long r = jedis.setnx(key, value);
            return r == 1;
        }
    }

    /**
     * 如果不存在,设置value
     * @param key
     * @param value
     * @param second 失效时间(单位:秒)
     * @return
     */
    public static Boolean setnx(String key, String value, int second) {
        try (Jedis jedis = jedisPool.getResource()) {
            boolean b = setnx(key, value);
            if (b) {
                jedis.expire(key, second);
            }
            return b;
        }
    }

    public static String get(String key, int waitSecond) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        long startTime = System.currentTimeMillis();
        long wait = TimeUnit.SECONDS.toMillis(waitSecond);
        long endTime = startTime + wait;
        long sleeptime = wait >= 300 ? 300 : wait;
        try (Jedis jedis = jedisPool.getResource()) {
            logger.info("get from redis wait {}s, key={}", waitSecond, key);
            do {
                TimeUnit.MILLISECONDS.sleep(sleeptime);
                if (jedis.exists(key)) {
                    String value = jedis.get(key);
                    logger.info("getString success,useTime={}, key={}", DateUtils.getUsedTime(startTime, System.currentTimeMillis()), key);
                    return cleanJson(value);
                }
            } while (System.currentTimeMillis() <= endTime);
            logger.warn("getString fail,useTime={}, key={}", DateUtils.getUsedTime(startTime, System.currentTimeMillis()), key);
        } catch (Throwable e) {
            logger.error("getString error,useTime={}, key={}", DateUtils.getUsedTime(startTime, System.currentTimeMillis()), key, e);
        }
        return null;
    }

    public static void hset(String key, String name, String value, int second) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, name, value);
            jedis.expire(key, second);
        }
    }

    public static void hset(String key, String name, String value, long timeout, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, name, value);
            jedis.expire(key, toSeconds(timeout, unit));
        }
    }

    public static Boolean lock(Object redisKey, int second) {
        long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(second);
        String lockKey = "lock." + redisKey.toString();
        boolean result = false;
        while (!result && System.currentTimeMillis() < endTime) {
            result = setnx(lockKey, "true", second);
            if (result) {
                logger.info("lock success redisKey={}", redisKey);
                return true;
            }
        }
        logger.error("lock fail redisKey={}", redisKey);
        return false;
    }

    public static void lockFailThrowException(Object redisKey, int second) {
        if (!lock(redisKey, second)) {
            throw new RuntimeException("lock fail redisKey=" + redisKey);
        }
    }

    public static Boolean unLock(Object redisKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            String lockKey = "lock." + redisKey.toString();
            jedis.del(lockKey);
            logger.info("unlock success redisKey={}", redisKey);
            return true;
        }
    }

    public static String cleanJson(String json) {
        if (StringUtils.isBlank(json)) {
            return json;
        }
        if (json.startsWith("\"") && json.endsWith("\"")) {
            return json.substring(1, json.length() - 1);
        }
        return json;
    }

    public static int toSeconds(long timeout, TimeUnit unit) {
        long l = unit.toSeconds(timeout);
        return l > 0 ? (int) l : 1;
    }

}
