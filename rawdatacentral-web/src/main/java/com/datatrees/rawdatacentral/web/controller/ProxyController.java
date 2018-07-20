package com.datatrees.rawdatacentral.web.controller;

import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);

    @RequestMapping(value = "/setProxy")
    public HttpResult<Boolean> setProxy(String websiteName, String proxy) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            String redisKey = RedisKeyPrefixEnum.WEBSITE_PROXY.getRedisKey(websiteName);
            RedisUtils.set(redisKey, proxy, RedisKeyPrefixEnum.WEBSITE_PROXY.toSeconds());
            logger.info("setProxy success websiteName={},proxy={}", websiteName, proxy);
            return result.success();
        } catch (Exception e) {
            logger.error("setProxy error websiteName={},proxy={}", websiteName, proxy, e);
            return result.failure();
        }
    }

    @RequestMapping(value = "/createLog")
    public HttpResult<Boolean> createLog(String websiteName, String proxy) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            int i = 0;
            while (i++ < Integer.MAX_VALUE - 1000) {
                logger.info("测试日志........................................");
                TimeUnit.MILLISECONDS.sleep(10);
            }
            return result.success();
        } catch (Exception e) {
            logger.error("setProxy error websiteName={},proxy={}", websiteName, proxy, e);
            return result.failure();
        }
    }

}
