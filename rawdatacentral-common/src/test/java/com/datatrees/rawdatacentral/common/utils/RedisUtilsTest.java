package com.datatrees.rawdatacentral.common.utils;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;

public class RedisUtilsTest {


    @Test
    public void test1(){
        Jedis jedis = RedisUtils.getJedis();
        String key = "test.username";
        System.out.println(jedis.exists(key));
        jedis.set(key,"周兴海");
        jedis.set(key,null);

    }

}