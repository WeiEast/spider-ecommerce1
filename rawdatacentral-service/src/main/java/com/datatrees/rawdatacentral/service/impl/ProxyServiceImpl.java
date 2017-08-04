package com.datatrees.rawdatacentral.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.service.constants.Constants;
import com.datatrees.rawdatacentral.service.proxy.SimpleProxyManager;
import com.datatrees.rawdatacentral.share.ProxyService;
import com.datatrees.rawdatacentral.share.RedisService;
import com.treefinance.proxy.api.ProxyProvider;
import com.treefinance.proxy.domain.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ProxyServiceImpl implements ProxyService, InitializingBean {

    private static final Logger       logger            = LoggerFactory.getLogger(ProxyServiceImpl.class);

    private static ThreadPoolExecutor proxyCallbackPool = null;

    @Resource
    private ProxyProvider             proxyProvider;

    @Override
    public Proxy getProxy(Long taskId, String websiteName) {
        Proxy proxy = null;
        try {
            RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
            proxy = redisService.getCache(RedisKeyPrefixEnum.TASK_PROXY.getRedisKey(taskId),
                new TypeReference<Proxy>() {
                });
            if (null != proxy) {
                logger.info("getProxy success from redis taskId={},websiteName={},proxy={}", taskId, websiteName,
                    proxy.getIp());
                return proxy;
            }
            ProxyProvider proxyProvider = BeanFactoryUtils.getBean(ProxyProvider.class);
            proxy = proxyProvider.getProxy(taskId, websiteName);
            if (null != proxy) {
                logger.info("getProxy success from dubbo taskId={},websiteName={},proxy={}", taskId, websiteName,
                    proxy.getIp());
                return proxy;
            }
        } catch (Exception e) {
            logger.error("getProxy error taskId={},websiteName={}", taskId, websiteName, e);
        }
        logger.warn("getProxy error,user local network taskId={},websiteName={}", taskId, websiteName);
        return proxy;
    }

    @Override
    public void release(Long taskId) {
        try {
            proxyCallbackPool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    proxyProvider.release(taskId);
                    logger.info("release proxy success taskId={}", taskId);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("release proxy error taskId={}", taskId, e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        proxyCallbackPool = new ThreadPoolExecutor(Constants.PROXY_CALLBACK_CORE_POOL_SIZE,
            Constants.PROXY_CALLBACK_MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(Constants.PROXY_CALLBACK_MAX_TASK_SIZE),
            new ThreadPoolExecutor.AbortPolicy());
    }
}
