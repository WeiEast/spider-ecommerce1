package com.datatrees.rawdatacentral.web.controller;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.treefinance.proxy.domain.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);
    @Resource
    private RedisService redisService;

    @RequestMapping(value = "/setProxy")
    public HttpResult<Boolean> setProxy(Long taskId, String proxy) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            String[] strings = proxy.split(":");
            Proxy p = new Proxy();
            String ip = strings[0];
            String port = strings[1];
            p.setIp(ip);
            p.setPort(port);
            redisService.cache(RedisKeyPrefixEnum.TASK_PROXY, taskId, p);
            logger.info("setProxy success taskId={},proxy={}", taskId, proxy);
            return result.success();
        } catch (Exception e) {
            logger.error("setProxy error taskId={},proxy={}", taskId, proxy, e);
            return result.failure();
        }
    }

}
