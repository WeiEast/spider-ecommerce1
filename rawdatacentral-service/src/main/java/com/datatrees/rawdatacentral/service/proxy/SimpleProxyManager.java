package com.datatrees.rawdatacentral.service.proxy;


import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.service.constants.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.protocol.Protocol;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.common.protocol.WebClientUtil;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.operation.impl.proxyset.Option;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.proxy.ProxyConfigException;
import com.datatrees.crawler.core.processor.proxy.ProxyServiceException;
import com.datatrees.crawler.core.processor.proxy.ProxyStatus;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 15, 2014 10:52:51 AM
 */
public class SimpleProxyManager extends ProxyManager {
    private static final Logger log = LoggerFactory.getLogger(SimpleProxyManager.class);
    private static ThreadPoolExecutor proxyCallbackPool = null;
    Protocol protocol;

    private Option option;

    public SimpleProxyManager() {
        try {
            protocol = WebClientUtil.getWebClient();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ProxyConfigException("init http client error!");
        }
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    class ProxyResult {
        private String proxy;
        private long timestamp;
    }


    private Proxy doProxyGet(String url) {
        Proxy rs = null;
        ProtocolOutput output = protocol.getProtocolOutput(new ProtocolInput().setUrl(url).setFollowRedirect(true));
        if (output.isSuccess()) {
            String proxy = output.getContent().getContentAsString();
            log.info("get proxy " + proxy);
            if (proxy != null) {
                ProxyResult proxyResult = (ProxyResult) GsonUtils.fromJson(proxy, ProxyResult.class);
                if (proxyResult != null && proxyResult.proxy != null) {
                    rs = Proxy.parse(proxyResult.proxy, proxyResult.timestamp);
                }
            }
        }
        return rs;
    }


    @Override
    public Proxy getProxy(String url) throws Exception {
        Proxy rs = null;
        if (StringUtils.isEmpty(url)) {
            throw new ProxyServiceException("empty  proxy service url");
        }
        if (option == null) {
            option = Option.AUTO;
        }

        switch (option) {
            case NORMAL:
                rs = doProxyGet(url);
                break;
            case AUTO:
                rs = doProxyGet(url);
                if (rs == null) {
                    rs = Proxy.LOCALNET;
                    log.warn("no active proxy use local network");
                }
                break;
            case ON:
                rs = doProxyGet(url);
                break;
            case OFF:
                rs = Proxy.LOCALNET;
                break;
            default:
                break;
        }

        return rs;
    }


    @Override
    public void callBackProxy(Proxy proxy, ProxyStatus status) throws Exception {
        this.callBackProxy(proxy, status, false);
    }


    @Override
    public void callBackProxy(Proxy proxy, ProxyStatus status, boolean aync) throws Exception {
        if (proxyCallbackPool == null) {
            synchronized (SimpleProxyManager.class) {
                if (proxyCallbackPool == null) {
                    proxyCallbackPool =
                            new ThreadPoolExecutor(Constants.PROXY_CALLBACK_CORE_POOL_SIZE, Constants.PROXY_CALLBACK_MAX_POOL_SIZE, 0L,
                                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(Constants.PROXY_CALLBACK_MAX_TASK_SIZE),
                                    new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }
        String url =
                callBackTemplate.replace("${status}", status.toString()).replace("${proxy}", proxy.getHost() + ":" + proxy.getPort())
                        .replace("${timestamp}", proxy.getTimestamp() + "");
        if (log.isDebugEnabled()) {
            log.debug("init call back proxy url:" + url + ",status:" + status + ",proxy:" + proxy);
        }
        if (aync) {
            try {
                proxyCallbackPool.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        protocol.getProtocolOutput(new ProtocolInput().setUrl(url).setFollowRedirect(true));
                        return null;
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            protocol.getProtocolOutput(new ProtocolInput().setUrl(url).setFollowRedirect(true));
        }
    }


    @Override
    public void release(Proxy proxy, boolean aync) throws Exception {
        String url =
                callBackTemplate.replace("${status}", ProxyStatus.RELEASE.toString()).replace("${proxy}", proxy.getHost() + ":" + proxy.getPort())
                        .replace("${timestamp}", proxy.getTimestamp() + "");
        log.info("release proxy url:" + url + ",proxy:" + proxy + ",aync:" + aync);
        if (aync) {
            if (proxyCallbackPool != null) {
                try {
                    proxyCallbackPool.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            protocol.getProtocolOutput(new ProtocolInput().setUrl(url).setFollowRedirect(true));
                            return null;
                        }
                    });
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else {
            protocol.getProtocolOutput(new ProtocolInput().setUrl(url).setFollowRedirect(true));
        }
    }



    @Override
    public void setModeOption(Option option) {
        this.option = option;
        log.info("set proxy mode option:" + option);
    }
}