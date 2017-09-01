package com.datatrees.rawdatacentral.common.http;

import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.treefinance.proxy.domain.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取代理
 */
public class ProxyUtils {

    private static final Logger       logger       = LoggerFactory.getLogger(ProxyUtils.class);
    private static       ProxyService proxyService = BeanFactoryUtils.getBean(ProxyService.class);

    public static Proxy getProxy(Long taskId, String websiteName) {
        return proxyService.getProxy(taskId, websiteName);
    }
}
