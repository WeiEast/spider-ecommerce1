package com.datatrees.rawdatacentral.common.http;

import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.treefinance.proxy.domain.Proxy;
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
        return proxyService.getProxy(taskId, websiteName);
    }

    public static void setProxyEnable(Long taskId, boolean enable) {
        redisService.saveString(RedisKeyPrefixEnum.TASK_PROXY_ENABLE, taskId, Boolean.toString(enable));
    }

    public static boolean getProxyEnable(long taskId) {
        String key = RedisKeyPrefixEnum.TASK_PROXY_ENABLE.getRedisKey(taskId);
        if (redisService.hasKey(key)) {
           return Boolean.valueOf(redisService.getString(key));
        }
        return false;
    }

}
