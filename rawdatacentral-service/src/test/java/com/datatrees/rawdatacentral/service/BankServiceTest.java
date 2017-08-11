package com.datatrees.rawdatacentral.service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;

import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.share.RedisService;

/**
 * Created by zhouxinghai on 2017/6/27.
 */
public class BankServiceTest extends BaseTest {

    @Resource
    private BankService bankService;

    @Test
    public void testRedisStart(){
//        String key = "zhouxinghai_01";
//        redisService.saveString(key, DateUtils.formatYmd(new Date()));
//
//        Bank bank = new Bank();
//        bank.setBankName("哈哈");
//        bank.setBankId(111);
//        redisService.cache(key,bank,60, TimeUnit.SECONDS);
//        bank = redisService.getCache(key,Bank.class);
//        System.out.println(bank);

    }


    @Test
    public void testCache(){
        Map<String, Integer> map = bankService.getMailBankMap();
        map = bankService.getMailBankMap();
        map = bankService.getMailBankMap();

    }
}
