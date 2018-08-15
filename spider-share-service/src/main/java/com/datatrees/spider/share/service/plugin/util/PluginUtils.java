package com.datatrees.spider.share.service.plugin.util;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.plugin.PluginContext;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderRequestFactory;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.function.SpiderResponseFactory;
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
        SpiderRequest newRequest = SpiderRequestFactory.make();
        AbstractProcessorContext processorContext = PluginContext.getProcessorContext();
        RequestUtil.setProcessorContext(newRequest, processorContext);
        RequestUtil.setConf(newRequest, PropertiesConfiguration.getInstance());
        RequestUtil.setContext(newRequest, processorContext.getContext());
        SpiderResponse newResponse = SpiderResponseFactory.make();
        try {
            RequestUtil.setCurrentUrl(newRequest, linkNode);
            ServiceBase serviceProcessor = ProcessorFactory.getService(null);
            serviceProcessor.invoke(newRequest, newResponse);
        } catch (Exception e) {
            logger.error("getResponseByWebRequest error! url={}", linkNode.getUrl(), e);
        }
        return StringUtils.defaultString(RequestUtil.getContent(newRequest));
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
