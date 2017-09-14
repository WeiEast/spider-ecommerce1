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

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public void set(String key, String value, long milliseconds) {
        jedis.set(key, value);
        jedis.pexpire(key, milliseconds);
    }

    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        jedis.set(key, value);
        jedis.pexpire(key, timeUnit.toMillis(timeout));
    }

}
