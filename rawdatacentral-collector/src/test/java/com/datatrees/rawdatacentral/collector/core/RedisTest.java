package com.datatrees.rawdatacentral.collector.core;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.domain.directive.DirectiveRedisCode;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhouxinghai on 2017/5/22.
 */
public class RedisTest {

    @Resource
    private RedisService redisService;

    @Test
    public void testSaveAndGet() {
        final String key = "test_" + RandomUtils.nextLong(10000, 20000);
        final String value = "hell word";
        redisService.saveString(key, value, 1, TimeUnit.MINUTES);
        Assert.assertEquals(value, redisService.getString(key));
    }

    @Test
    public void testSaveAndGet2() {
        final String key = "test_" + RandomUtils.nextLong(10000, 20000);
        final String value = "hell word";
        redisService.saveToList(key, Arrays.asList(value), 1, TimeUnit.MINUTES);
        Assert.assertEquals(value, redisService.rightPop(key));
    }

    @Test
    public void testSaveAndGet3() {
        final long taskId = RandomUtils.nextLong(10000, 20000);
        DirectiveResult<Boolean> result = new DirectiveResult("sms", taskId);
        result.fill(DirectiveRedisCode.CANCEL, false);

        final String key = result.getGroupKey();

        redisService.saveDirectiveResult(result);

        DirectiveResult<Boolean> x = redisService.getNextDirectiveResult(key, 1, TimeUnit.MINUTES);

        Assert.assertNotNull(x);
    }
}
