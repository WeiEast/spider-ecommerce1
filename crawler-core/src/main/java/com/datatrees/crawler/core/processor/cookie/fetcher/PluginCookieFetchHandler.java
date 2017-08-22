/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.cookie.fetcher;

import com.datatrees.crawler.core.domain.config.properties.cookie.CustomCookie;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.plugin.PluginCaller;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginConfSupplier;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 27, 2014 6:42:51 PM
 */
public class PluginCookieFetchHandler extends CookieFetchHandler {

    private static final Logger log = LoggerFactory.getLogger(PluginCookieFetchHandler.class);

    private SearchProcessorContext context = null;

    public PluginCookieFetchHandler(SearchProcessorContext wrapper) {
        super();
        this.context = wrapper;
    }

    @Override
    public String getCookie() {
        String result = "";
        try {
            CustomCookie cookieConf = (CustomCookie) context.getCookieConf();
            //call plugin 
            String pid = cookieConf.getHandleConfig();

            Object respOutput = PluginCaller.call(context, pid, (PluginConfSupplier) pluginWrapper -> {
                Map<String, String> params = new HashMap<>();
                params.put(PluginConstants.EXTRA_CONFIG, pluginWrapper.getExtraConfig());

                return params;
            });

            result = (String) respOutput;
        } catch (Exception e) {
            log.error("invoke PluginCookieFetchHandler error!", e);
        }
        return result;
    }
}
