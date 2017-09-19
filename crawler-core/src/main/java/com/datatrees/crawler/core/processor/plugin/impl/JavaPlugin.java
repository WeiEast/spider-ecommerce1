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
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.Plugin;
import com.datatrees.crawler.core.processor.plugin.PluginWrapper;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;

/**
 * java plugin for
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 1:10:33 PM
 */
public class JavaPlugin extends Plugin {

    @Override
    protected Object invokePlugin(PluginWrapper plugin, String args, Request request) throws Exception {

        AbstractProcessorContext context = RequestUtil.getProcessorContext(request);
        com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin javaPlugin
                = (com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin) plugin.getPlugin();

        String mainClass = javaPlugin.getMainClass();
        String fileName = javaPlugin.getFileName();
        AbstractClientPlugin clientPlugin = BeanFactoryUtils.getBean(PluginManager.class).loadPlugin(fileName, mainClass);
        if (context instanceof SearchProcessorContext) {
            // add proxy if necessary
            clientPlugin.setProxyManager(((SearchProcessorContext) context).getProxyManager());
        }
        // add http client
        clientPlugin.setWebClient(WebClientUtil.getWebClient());
        //插件自定义参数
        return clientPlugin.process(args, javaPlugin.getParams());
    }
}
