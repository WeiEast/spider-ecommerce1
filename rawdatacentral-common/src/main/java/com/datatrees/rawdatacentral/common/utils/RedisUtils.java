package com.datatrees.rawdatacentral.common.utils;

import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

public class RedisUtils {

    private static final Jedis jedis;

    static {
        jedis = new Jedis("192.168.5.24", 6379);
    }

    public static Jedis getJedis() {
        return jedis;
    }

    /**
     * 是否存在
     * @param key 不能为null
     * @return
     */
    public Boolean exists(String key) {
        return jedis.exists(key);
    }

    /**
     * 设置值
     * @param key   不能为null
     * @param value 不能为null
     */
    public void set(String key, String value) {
        jedis.set(key, value);
    }

    /**
     * 设置值
     * @param key    不能为null
     * @param value  不能为null
     * @param second 失效时间(单位:秒)
     */
    public void set(String key, String value, int second) {
        jedis.setex(key, second, value);
    }

    /**
     * 设置值
     * @param key      不能为null
     * @param value    不能为null
     * @param timeout  失效时间
     * @param timeUnit 失效时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        set(key, value, (int) timeUnit.toSeconds(timeout));
    }

    /**
     * 如果不存在,设置value
     * @param key
     * @param value
     * @return
     */
    public Boolean setIfAbsent(String key, String value) {
        Long r = jedis.setnx(key, value);
        return r == 1;
    }

    /**
     * 如果不存在,设置value
     * @param key
     * @param value
     * @param second 失效时间(单位:秒)
     * @return
     */
    public Boolean setIfAbsent(String key, String value, int second) {
        boolean b = setIfAbsent(key, value);
        if (b) {
            jedis.expire(key, second);
        }
        return b;
    }
}
