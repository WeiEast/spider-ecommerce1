package com.datatrees.crawler.core.processor.service.impl;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.ProtocolStatusCodes;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.service.impl.PluginService;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginUtil;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.google.common.base.Preconditions;
import com.treefinance.crawler.framework.extension.plugin.PluginCaller;
import org.apache.commons.lang.StringUtils;

public class PluginServiceImpl extends ServiceBase<PluginService> {

    private final int retryCount = PropertiesConfiguration.getInstance().getInt("pluginService.retry.count", 3);

    public PluginServiceImpl(@Nonnull PluginService service) {
        super(service);
    }

    @Override
    public void process(@Nonnull Request request, @Nonnull Response response) throws Exception {
        PluginService service = getService();
        LinkNode current = RequestUtil.getCurrentUrl(request);
        String url = current.getUrl();
        SearchProcessorContext context = (SearchProcessorContext) RequestUtil.getProcessorContext(request);
        logger.info("handling request using plugin : {}", url);
        AbstractPlugin plugin = service.getPlugin();
        if (plugin != null) {
            for (int i = 0; i < retryCount; i++) {
                try {
                    String serviceResult = PluginCaller.call(plugin, context, () -> {
                        Map<String, String> params = new LinkedHashMap<>();
                        params.put(PluginConstants.CURRENT_URL, url);
                        params.put(PluginConstants.REDIRECT_URL, current.getRedirectUrl());
                        current.getPropertys().forEach((key, val) -> params.put(key, val + ""));
                        if (context.needProxyByUrl(url)) {
                            Proxy proxy = context.getProxy();
                            Preconditions.checkNotNull(proxy);
                            logger.info("set proxy to: " + url + "\tproxy:\t" + proxy.format());
                            params.put(PluginConstants.PROXY, proxy.format());
                        }

                        return params;
                    }).toString();

                    logger.debug("PluginServiceImpl output content : {} url : {}", serviceResult, url);
                    // get plugin json result
                    Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult(serviceResult);
                    serviceResult = StringUtils.defaultIfEmpty((String) pluginResultMap.get(PluginConstants.SERVICE_RESULT), "");

                    response.setOutPut(serviceResult);

                    RequestUtil.setContent(request, serviceResult);
                    ProcessorContextUtil.addThreadLocalLinkNode(context, current);
                    ProcessorContextUtil.addThreadLocalResponse(context, response);
                    break;
                } catch (Exception e) {
                    logger.error("do plugin service error url:" + url + " , do revisit ... " + e.getMessage(), e);
                    if (i == retryCount - 1) {
                        ResponseUtil.setResponseStatus(response, ProtocolStatusCodes.EXCEPTION);
                        throw e;
                    }
                    current.increaseRetryCount();
                }
            }
        }

    }
}
