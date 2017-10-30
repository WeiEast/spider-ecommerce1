package com.datatrees.rawdatacentral.web.controller;

import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
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
            RedisUtils.set(redisKey, proxy);
            RedisUtils.expire(redisKey, RedisKeyPrefixEnum.WEBSITE_PROXY.toSeconds());
            logger.info("setProxy success websiteName={},proxy={}", websiteName, proxy);
            return result.success();
        } catch (Exception e) {
            logger.error("setProxy error websiteName={},proxy={}", websiteName, proxy, e);
            return result.failure();
        }
    }

}
