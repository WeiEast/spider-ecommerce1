package com.datatrees.crawler.core.processor.common.resource;

import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.proxy.ProxyStatus;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月5日 下午11:29:29
 */
public abstract class ProxyManager implements Resource {

    public abstract Proxy getProxy() throws Exception;

    public abstract void callBackProxy(ProxyStatus status) throws Exception;

    public abstract void release() throws Exception;
}
