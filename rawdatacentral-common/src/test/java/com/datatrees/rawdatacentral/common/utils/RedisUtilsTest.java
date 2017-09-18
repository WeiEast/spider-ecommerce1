package com.datatrees.rawdatacentral.common.utils;

import org.junit.Test;

public class RedisUtilsTest {

    @Test
    public void test1() {
        RedisUtils.init("192.168.5.24", 6379, null);
        int i = 1;
        while (i < 100000) {
            String key = "test.username";
            System.out.println(i++);
            String status = RedisUtils.set(key, "周兴海");
            System.out.println(TemplateUtils.format("i={},status={}", i++, status));
        }

    }

}