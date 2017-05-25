package com.datatrees.rawdatacentral.collector.core;

import com.datatrees.rawdatacentral.collector.AbstractTest;
import com.datatrees.rawdatacentral.domain.constant.DirectiveRedisCode;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Created by zhouxinghai on 2017/5/22.
 */
public class RedisTest extends AbstractTest {

    @Resource
    private RedisService redisService;

    @Test
    public void testSaveAndGet() {
        final String key = "test_" + RandomUtils.nextLong(10000, 20000);
        final String value = "hell word";
        redisService.saveString(key, value);
        Assert.assertEquals(value, redisService.getString(key));
    }

    @Test
    public void testSaveAndGet2() {
        final String key = "test_" + RandomUtils.nextLong(10000, 20000);
        final String value = "hell word";
        redisService.saveListString(key, Arrays.asList(value));
        Assert.assertEquals(value, redisService.rightPop(key));
    }

    @Test
    public void testSaveAndGet3() {
        final long taskId = RandomUtils.nextLong(10000, 20000);
        DirectiveResult<Boolean> result = new DirectiveResult("sms", taskId);
        result.fill(DirectiveRedisCode.CANCEL, false);

        final String key = result.getGroupKey();

        redisService.saveDirectiveResult(result);

        DirectiveResult<Boolean> x = redisService.getNextDirectiveResult(key);

        Assert.assertNotNull(x);
    }
}
