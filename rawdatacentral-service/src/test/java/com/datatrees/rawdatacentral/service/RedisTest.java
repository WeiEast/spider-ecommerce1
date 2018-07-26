package com.datatrees.rawdatacentral.service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.spider.share.service.RedisService;
import com.datatrees.rawdatacentral.domain.model.Bank;
import org.junit.Test;

/**
 * Created by zhouxinghai on 2017/6/27.
 */
public class RedisTest extends BaseTest {

    @Resource
    private RedisService redisService;

    @Test
    public void testRedisStart() {
        String key = "zhouxinghai_01.redis.cath";
        //        redisService.saveString(key, DateUtils.formatYmd(new Date()));

        Bank bank = new Bank();
        bank.setBankName("哈哈");
        bank.setBankId(111);
        redisService.cache(key, bank, 60, TimeUnit.SECONDS);
        bank = redisService.getCache(key, new TypeReference<Bank>() {});
        System.out.println(bank);

    }

}
