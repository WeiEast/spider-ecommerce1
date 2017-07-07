package com.datatrees.rawdatacentral.service.proxy;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.protocol.Protocol;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.common.protocol.WebClientUtil;
import com.datatrees.crawler.core.domain.config.operation.impl.proxyset.Option;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.proxy.ProxyConfigException;
import com.datatrees.crawler.core.processor.proxy.ProxyServiceException;
import com.datatrees.crawler.core.processor.proxy.ProxyStatus;

/**
 *
 * @author <A HREF="mailto:wang_cheng@treefinance.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jun 9, 2014 12:42:47 AM
 */
public class SimpleProxyManager extends ProxyManager {

    private static final Logger log = LoggerFactory.getLogger(SimpleProxyManager.class);

    Protocol protocol;

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

    @Override
    public Proxy getProxy(String url) throws Exception {
        Proxy rs = null;
        if (StringUtils.isEmpty(url)) {
            throw new ProxyServiceException("empty service url");
        }
        log.debug("service url " + url);
        ProtocolOutput output = protocol.getProtocolOutput(new ProtocolInput().setUrl(url).setFollowRedirect(true));
        if (output.isSuccess()) {
            String proxy = output.getContent().getContentAsString().trim();
            log.info("get proxy url:" + proxy);
            if (StringUtils.isNotEmpty(proxy)) {
                rs = Proxy.parse(proxy);
            }
        }
        return rs;
    }

    @Override
    public void callBackProxy(Proxy proxy, ProxyStatus status) throws Exception {}

    @Override
    public void callBackProxy(Proxy proxy, ProxyStatus status, boolean aync) throws Exception {}

    @Override
    public void release(Proxy proxy, boolean aync) throws Exception {}

    @Override
    public void setModeOption(Option option) {}

}