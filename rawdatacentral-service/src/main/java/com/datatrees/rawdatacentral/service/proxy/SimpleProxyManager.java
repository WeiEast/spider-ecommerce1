package com.datatrees.rawdatacentral.service.proxy;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.service.constants.Constants;
import com.treefinance.proxy.api.ProxyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.proxy.ProxyStatus;

/**
 * 代理,重新定义,以taskId为唯一标识
 */
public class SimpleProxyManager extends ProxyManager {

    private static final Logger       logger            = LoggerFactory.getLogger(SimpleProxyManager.class);

    private static ThreadPoolExecutor proxyCallbackPool = null;

    /**
     * proxy dubbo service
     */
    private ProxyProvider             proxyProvider;

    /**
     * 当前代理
     */
    private Proxy                     last;

    /**
     * 根据taskId获取,全部session模式,
     */
    private Long                      taskId;

    private String                    websiteName;

    public SimpleProxyManager(Long taskId, String websiteName, ProxyProvider proxyProvider) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        CheckUtils.checkNotNull(proxyProvider, "proxyProvider is null");
        CheckUtils.checkNotBlank(websiteName, "websiteName is null");
        this.taskId = taskId;
        this.proxyProvider = proxyProvider;
        this.websiteName = websiteName;
    }

    @Override
    public Proxy getProxy() throws Exception {
        if (null == last) {
            try {
                com.treefinance.proxy.domain.Proxy proxy = proxyProvider.getProxy(taskId, websiteName);
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
            if (proxyCallbackPool == null) {
                synchronized (SimpleProxyManager.class) {
                    if (proxyCallbackPool == null) {
                        proxyCallbackPool = new ThreadPoolExecutor(Constants.PROXY_CALLBACK_CORE_POOL_SIZE,
                            Constants.PROXY_CALLBACK_MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<Runnable>(Constants.PROXY_CALLBACK_MAX_TASK_SIZE),
                            new ThreadPoolExecutor.AbortPolicy());
                    }
                }
            }
            proxyCallbackPool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    proxyProvider.release(taskId);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("release proxy error taskId={},websiteName={}", taskId, websiteName, e);
        }
    }

}