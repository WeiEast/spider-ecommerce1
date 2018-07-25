package com.datatrees.rawdatacentral.common.http;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.treefinance.proxy.api.ProxyProvider;
import com.treefinance.proxy.domain.IpLocale;
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
        return proxyService.getProxy(taskId, websiteName);
    }

    public static boolean setProxyEnable(Long taskId, boolean enable) {
        return redisService.saveString(RedisKeyPrefixEnum.TASK_PROXY_ENABLE, taskId, Boolean.toString(enable));
    }

    public static boolean getProxyEnable(long taskId) {
        String key = RedisKeyPrefixEnum.TASK_PROXY_ENABLE.getRedisKey(taskId);
        if (redisService.hasKey(key)) {
            return Boolean.valueOf(redisService.getString(key));
        }
        return false;
    }

    public static IpLocale queryIpLocale(Long taskId, String userIp) {
        try {
            if (StringUtils.isBlank(userIp)) {
                logger.info("query ip locale error,userIp is blank,taskId={}", taskId);
            }
            ProxyProvider provider = BeanFactoryUtils.getBean(ProxyProvider.class);
            IpLocale locale = provider.getIpLocale(userIp);
            if (null != locale) {
                String k = RedisKeyPrefixEnum.TASK_IP_LOCALE.getRedisKey(taskId);
                RedisUtils.set(k, JSON.toJSONString(locale), RedisKeyPrefixEnum.TASK_IP_LOCALE.toSeconds());
            }
            logger.info("query ip locale,taskId={},userIp={},locale={}", taskId, userIp, JSON.toJSONString(locale));
            return locale;
        } catch (Exception e) {
            logger.info("query ip locale error,taskId={},userIp={}", taskId, userIp, e);
            return null;
        }
    }

    public static void releaseProxy(Long taskId) {
        proxyService.release(taskId);
    }

    public static void clearProxy(Long taskId) {
        proxyService.clear(taskId);
    }

    public static void setProxyLocation(Long taskId, String province, String city) {
        IpLocale locale = new IpLocale();
        locale.setProvinceName(province);
        locale.setCityName(city);
        String key = RedisKeyPrefixEnum.TASK_IP_LOCALE.getRedisKey(taskId);
        RedisUtils.setnx(key, JSON.toJSONString(locale));
    }

    public static void setProxyEnabled(Long taskId, String province, String city) {
        if (setProxyEnable(taskId, true)) {
            setProxyLocation(taskId, province, city);
        }
    }
}
