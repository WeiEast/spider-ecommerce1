package com.datatrees.crawler.plugin.util;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.plugin.PluginContext;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.service.ServiceBase;
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
        Request newRequest = new Request();
        AbstractProcessorContext processorContext = PluginContext.getProcessorContext();
        RequestUtil.setProcessorContext(newRequest, processorContext);
        RequestUtil.setConf(newRequest, PropertiesConfiguration.getInstance());
        RequestUtil.setContext(newRequest, processorContext.getContext());
        Response newResponse = new Response();
        try {
            RequestUtil.setCurrentUrl(newRequest, linkNode);
            ServiceBase serviceProcessor = ProcessorFactory.getService((AbstractService) null);
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
     * @throws Exception
     */
    public static String getPorxy(String url) throws Exception {
        String proxyURL = null;
        AbstractProcessorContext context = PluginContext.getProcessorContext();
        if (context instanceof SearchProcessorContext && ((SearchProcessorContext) context).needProxyByUrl(url)) {
            Proxy proxy = ((SearchProcessorContext) context).getProxyManager().getProxy(url);
            if (proxy == null) {
                logger.error("no active proxy use for cacertUrl={},use default ip");
            } else {
                proxyURL = proxy.format();
            }
        }
        return proxyURL;
    }



}
