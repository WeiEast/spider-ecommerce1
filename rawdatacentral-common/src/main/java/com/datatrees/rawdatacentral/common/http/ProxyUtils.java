package com.datatrees.rawdatacentral.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.treefinance.proxy.domain.Proxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取代理
 */
public class ProxyUtils {

    private static final Logger       logger       = LoggerFactory.getLogger(ProxyUtils.class);
    private static       ProxyService proxyService = BeanFactoryUtils.getBean(ProxyService.class);
    private static       RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);

    public static Proxy getProxy(Long taskId, String websiteName) {
        CheckUtils.checkNotPositiveNumber(taskId, ErrorCode.EMPTY_TASK_ID);
        String key = RedisKeyPrefixEnum.TASK_PROXY.getRedisKey(taskId);
        if (redisService.hasKey(key)) {
            String proxy = redisService.getString(key);
            if (StringUtils.equals("local", proxy)) {
                return null;
            }
            return JSON.parseObject(proxy, new TypeReference<Proxy>() {});
        }

        try {
            boolean retry = false;
            do {
                Proxy  proxy = proxyService.getProxy(taskId, websiteName);
                if (null != proxy) {
                    redisService.cache(RedisKeyPrefixEnum.TASK_PROXY, taskId, proxy);
                    return proxy;
                }
                retry = true;
            } while (!retry);
        } catch (Exception e) {
            logger.error("getProxy error taskId={},websiteName={}", taskId, websiteName, e);
        }
        redisService.saveString(RedisKeyPrefixEnum.TASK_PROXY,taskId,"local");
        return null;

    }
}
