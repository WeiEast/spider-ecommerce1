package com.datatrees.rawdatacentral.common.utils;

import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.treefinance.proxy.domain.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取代理
 */
public class ProxyUtils {

    private static final Logger logger       = LoggerFactory.getLogger(ProxyUtils.class);

    private static ProxyService proxyService = BeanFactoryUtils.getBean(ProxyService.class);

    public static Proxy getProxy(Long taskId, String websiteName) {
        CheckUtils.checkNotPositiveNumber(taskId, ErrorCode.EMPTY_TASK_ID);
        Proxy proxy = null;
        try {
            proxy = proxyService.getProxy(taskId, websiteName);
        } catch (Exception e) {
            logger.error("getProxy error taskId={},websiteName={}", taskId, websiteName, e);
        }
        return proxy;
    }
}
