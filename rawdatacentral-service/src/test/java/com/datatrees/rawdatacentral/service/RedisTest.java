package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.share.RedisService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by zhouxinghai on 2017/6/27.
 */
public class RedisTest extends BaseTest {

    @Resource
    private RedisService redisService;

    @Test
    public void testRedisStart(){
        String key = "zhouxinghai_01";
        redisService.saveString(key, DateUtils.formatYmd(new Date()));
    }
}
