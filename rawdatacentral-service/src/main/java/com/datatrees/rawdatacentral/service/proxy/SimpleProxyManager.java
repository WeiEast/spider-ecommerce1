package com.datatrees.rawdatacentral.service.proxy;

import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.share.ProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.proxy.ProxyStatus;

/**
 * 代理,重新定义,以taskId为唯一标识
 */
public class SimpleProxyManager extends ProxyManager {

    private static final Logger logger = LoggerFactory.getLogger(SimpleProxyManager.class);

    /**
     * proxy dubbo service
     */
    private ProxyService        proxyService;

    /**
     * 当前代理
     */
    private Proxy               last;

    /**
     * 根据taskId获取,全部session模式,
     */
    private Long                taskId;

    private String              websiteName;

    public SimpleProxyManager(Long taskId, String websiteName, ProxyService proxyService) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        CheckUtils.checkNotNull(proxyService, "proxyService is null");
        CheckUtils.checkNotBlank(websiteName, "websiteName is null");
        this.taskId = taskId;
        this.proxyService = proxyService;
        this.websiteName = websiteName;
    }

    @Override
    public Proxy getProxy() throws Exception {
        if (null == last) {
            try {
                com.treefinance.proxy.domain.Proxy proxy = proxyService.getProxy(taskId, websiteName);
                if (null != proxy) {
                    last = new Proxy(proxy.getIp(), Integer.valueOf(proxy.getPort()));
                }
            } catch (Exception e) {
                logger.error("getProxy error taskId={},websiteName={}", taskId, websiteName, e);
            }
        }
        if (null == last) {
            logger.warn("getProxy error,user local network taskId={},websiteName={}", taskId, websiteName);
        }
        return last;
    }

    @Override
    public void callBackProxy(ProxyStatus status) throws Exception {
        // TODO: 2017/8/4  
    }

    @Override
    public void release() throws Exception {
        try {
            proxyService.release(taskId);
        } catch (Exception e) {
            logger.error("release proxy error taskId={},websiteName={}", taskId, websiteName, e);
        }
    }

}