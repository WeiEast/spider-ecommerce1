package com.datatrees.spider.share.service.plugin.util;

import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.plugin.PluginContext;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.treefinance.crawler.framework.util.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 插件中使用
 * Created by zhouxinghai on 2017/7/10.
 */
public class PluginUtils {

    private static final Logger logger = LoggerFactory.getLogger(PluginUtils.class);

    /**
     * http请求
     * @param url
     * @return
     */
    public static String getResponseByWebRequest(String url) {
        return getResponseByWebRequest(new LinkNode(url));
    }

    /**
     * http请求
     * @param linkNode
     * @return
     */
    public static String getResponseByWebRequest(LinkNode linkNode) {
        AbstractProcessorContext processorContext = PluginContext.getProcessorContext();

        try {
            return ServiceUtils.invokeAsString(null, linkNode, processorContext, null, processorContext.getVisibleScope());
        } catch (Exception e) {
            logger.error("getResponseByWebRequest error! url={}", linkNode.getUrl(), e);
        }

        return StringUtils.EMPTY;
    }

    /**
     * 获取代理
     * @param url
     * @return
     * @exception Exception
     */
    public static String getPorxy(String url) throws Exception {
        String proxyURL = null;
        AbstractProcessorContext context = PluginContext.getProcessorContext();
        if (context instanceof SearchProcessorContext && ((SearchProcessorContext) context).needProxyByUrl(url)) {
            Proxy proxy = ((SearchProcessorContext) context).getProxy();
            if (proxy == null) {
                logger.error("no active proxy use for cacertUrl={},use default ip");
            } else {
                proxyURL = proxy.format();
            }
        }
        return proxyURL;
    }

}
