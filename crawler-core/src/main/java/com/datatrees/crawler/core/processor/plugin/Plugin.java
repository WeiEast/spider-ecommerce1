/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.common.Processor;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.exception.PluginInvokeException;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 1:10:48 PM
 */
public abstract class Plugin extends Processor {

    private static final Logger        logger = LoggerFactory.getLogger(Plugin.class);
    protected            PluginWrapper plugin = null;

    public PluginWrapper getPluginDesc() {
        return plugin;
    }

    public void setPluginDesc(PluginWrapper plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void preProcess(Request request, Response response) throws Exception {
        Preconditions.checkNotNull(plugin, "plugin wrapper must not be null!");
        Preconditions.checkNotNull(plugin.getFile(), "plugin file must not be null!");
        Preconditions.checkNotNull(plugin.getPlugin(), "plugin config must not be null!");
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        try {
            String args = getPhaseInput(request);

            Object result = invokePlugin(plugin, args, request);

            response.setOutPut(result);
        } catch (PluginInvokeException e) {
            logger.error("Error invoking java plugin={}", plugin, e);
            throw e;
        } catch (Throwable e) {
            logger.error("Error invoking java plugin={} ", plugin, e);
            throw new PluginInvokeException("Error invoking plugin : " + plugin, e);
        }
    }

    protected abstract Object invokePlugin(PluginWrapper plugin, String args, Request request) throws Exception;

    /**
     * get Plugin inputs rules : first argument is config other arguments are
     * dynamic by runtime business logic should impl each state
     */
    private String getPhaseInput(Request req) {
        Map<String, String> pluginConf = RequestUtil.getPluginRuntimeConf(req);
        if (MapUtils.isEmpty(pluginConf)) {
            pluginConf = new HashMap<>();
        }

        pluginConf.put(PluginConstants.EXTRA_CONFIG, plugin.getExtraConfig());

        return GsonUtils.toJson(pluginConf);
    }
}
