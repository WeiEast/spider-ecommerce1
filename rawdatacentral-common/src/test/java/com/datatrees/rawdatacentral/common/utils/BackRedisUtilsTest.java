package com.datatrees.rawdatacentral.common.utils;

import org.junit.Test;

public class BackRedisUtilsTest {

    @Test
    public void test() {
        RedisUtils.init("192.168.5.24", 6379, null, 1);
        RedisUtils.set("zhouxinghai", "99887");

    }

}