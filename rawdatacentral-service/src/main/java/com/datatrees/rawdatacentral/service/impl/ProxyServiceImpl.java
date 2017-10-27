package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.service.constants.Constants;
import com.treefinance.proxy.api.ProxyProvider;
import com.treefinance.proxy.domain.Proxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class ProxyServiceImpl implements ProxyService, InitializingBean {

    private static final Logger             logger            = LoggerFactory.getLogger(ProxyServiceImpl.class);
    private static       ThreadPoolExecutor proxyCallbackPool = null;
    @Resource
    private ProxyProvider proxyProvider;
    @Resource
    private RedisService  redisService;

    @Override
    public Proxy getProxy(Long taskId, String websiteName) {
        CheckUtils.checkNotPositiveNumber(taskId, ErrorCode.EMPTY_TASK_ID.getErrorMsg());
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        Proxy proxy = null;
        try {
            String proxyString = PropertiesConfiguration.getInstance().get("website.proxy." + websiteName);
            if (StringUtils.isNotBlank(proxyString)) {
                proxy = new Proxy();
                proxy.setIp(proxyString.split(":")[0]);
                proxy.setPort(proxyString.split(":")[1]);
                return proxy;
            }
            proxy = redisService.getCache(RedisKeyPrefixEnum.TASK_PROXY.getRedisKey(taskId), new TypeReference<Proxy>() {});
            if (null != proxy) {
                //ip为空不再访问dubbo接口,第一次调用没有取到proxy,中途不更换
                return StringUtils.isBlank(proxy.getIp()) ? null : proxy;
            }
            proxy = getProxyFromDubbo(taskId, websiteName);
            if (null != proxy) {
                redisService.cache(RedisKeyPrefixEnum.TASK_PROXY, taskId, proxy);
                return proxy;
            }
            redisService.cache(RedisKeyPrefixEnum.TASK_PROXY, taskId, new Proxy());
            return null;
        } catch (Exception e) {
            logger.error("getProxy error taskId={},websiteName={}", taskId, websiteName, e);
            return null;
        }

    }

    @Override
    public void release(Long taskId) {
        try {
            redisService.deleteKey(RedisKeyPrefixEnum.TASK_PROXY.getRedisKey(taskId));
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

    private Proxy getProxyFromDubbo(Long taskId, String websiteName) {
        try {
            Proxy proxy = proxyProvider.getProxy(taskId, websiteName);
            if (null != proxy && StringUtils.isNoneBlank(proxy.getIp())) {
                logger.info("getProxyFromDubbo success,taskId={},websiteName={},proxy={}", taskId, websiteName, JSON.toJSONString(proxy));
                return proxy;
            }
        } catch (Exception e) {
            logger.debug("getProxyFromDubbo error,taskId={},websiteName={}", taskId, websiteName);
        }
        logger.warn("从dubbo获取代理失败,将使用本地网络,taskId={},websiteName={}", taskId, websiteName);
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        proxyCallbackPool = new ThreadPoolExecutor(Constants.PROXY_CALLBACK_CORE_POOL_SIZE, Constants.PROXY_CALLBACK_MAX_POOL_SIZE, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(Constants.PROXY_CALLBACK_MAX_TASK_SIZE),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
