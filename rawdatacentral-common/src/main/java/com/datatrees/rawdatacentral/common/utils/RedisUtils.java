package com.datatrees.rawdatacentral.common.utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.common.config.RedisConfig;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils extends RedisConfig {

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

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 是否存在
     * @param key 不能为null
     * @return
     */
    public static Boolean exists(String key) {
        return getJedis().exists(key);
    }

    /**
     * 返回数据类型
     * @param key
     * @return none  string hash list set zset
     */
    public static String type(String key) {
        return getJedis().type(key);
    }

    /**
     * 删除key
     * @param key 不能为null
     * @return
     */
    public static void del(String key) {
        getJedis().del(key);
    }

    /**
     * 设置值
     * @param key   不能为null
     * @param value 不能为null
     */
    public static void set(String key, String value) {
        getJedis().set(key, value);
    }

    /**
     * 设置值
     * @param key    不能为null
     * @param value  不能为null
     * @param second 失效时间(单位:秒)
     */
    public static void set(String key, String value, int second) {
        getJedis().setex(key, second, value);
    }

    /**
     * 设置值
     * @param key      不能为null
     * @param value    不能为null
     * @param timeout  失效时间
     * @param timeUnit 失效时间单位
     */
    public static void set(String key, String value, long timeout, TimeUnit timeUnit) {
        set(key, value, (int) timeUnit.toSeconds(timeout));
    }

    /**
     * 如果不存在,设置value
     * @param key
     * @param value
     * @return
     */
    public static Boolean setnx(String key, String value) {
        Long r = getJedis().setnx(key, value);
        return r == 1;
    }

    /**
     * 如果不存在,设置value
     * @param key
     * @param value
     * @param second 失效时间(单位:秒)
     * @return
     */
    public static Boolean setnx(String key, String value, int second) {
        boolean b = setnx(key, value);
        if (b) {
            getJedis().expire(key, second);
        }
        return b;
    }

    /**
     * 设置值
     * @param key   不能为null
     * @param value 不能为null
     */
    public static void rpush(String key, String value) {
        getJedis().rpush(key, value);
    }

    public static String get(String key) {
        return getJedis().get(key);
    }

    public static void hset(String key, String name, String value) {
        getJedis().hset(key, name, value);
    }

    public static void hset(String key, String name, String value, int second) {
        hset(key, name, value);
        getJedis().expire(key, second);
    }

    public static void hset(String key, String name, String value, long timeout, TimeUnit unit) {
        hset(key, name, value);
        getJedis().expire(key, (int) unit.toSeconds(timeout));
    }

    public static void hdel(String key, String... names) {
        getJedis().hdel(key, names);
    }

    public static Map<String, String> hgetAll(String key) {
        return getJedis().hgetAll(key);
    }

}
