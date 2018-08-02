/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.protocol.WebClientUtil;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.Plugin;
import com.treefinance.crawler.framework.extension.plugin.ProcessContextHolder;

/**
 * java plugin for
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 1:10:33 PM
 */
public class JavaPlugin extends Plugin<com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin> {

    public JavaPlugin(com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin metadata, AbstractProcessorContext context) {
        super(metadata, context);
    }

    @Override
    protected Object invokePlugin(com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin metadata, String args,
            Request request) throws Exception {
        AbstractClientPlugin clientPlugin = getContext().loadPlugin(metadata);
        if (getContext() instanceof SearchProcessorContext) {
            // add proxy if necessary
            clientPlugin.setProxyManager(((SearchProcessorContext) getContext()).getProxyManager());
        }
        // add http client
        clientPlugin.setWebClient(WebClientUtil.getWebClient());

        ProcessContextHolder.setProcessorContext(getContext());
        try {
            //插件自定义参数
            return clientPlugin.process(args, metadata.getParams());
        } finally {
            ProcessContextHolder.clearProcessorContext();
        }
    }
}
