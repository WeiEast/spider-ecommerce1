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
import java.util.Objects;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.Processor;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.exception.PluginInvokeException;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 1:10:48 PM
 */
public abstract class Plugin<T extends AbstractPlugin> extends Processor {

    private static final Logger logger = LoggerFactory.getLogger(Plugin.class);
    private final T                        metadata;
    private final AbstractProcessorContext context;

    public Plugin(T metadata, AbstractProcessorContext context) {
        this.metadata = Objects.requireNonNull(metadata);
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        try {
            String args = getPhaseInput(request);

            Object result = invokePlugin(metadata, args, request);

            response.setOutPut(result);
        } catch (PluginInvokeException e) {
            logger.error("Error invoking plugin: {}", metadata.getId(), e);
            throw e;
        } catch (Throwable e) {
            logger.error("Error invoking plugin: {} ", metadata.getId(), e);
            throw new PluginInvokeException("Error invoking plugin: " + metadata.getId(), e);
        }
    }

    protected abstract Object invokePlugin(T metadata, String args, Request request) throws Exception;

    /**
     * get Plugin inputs rules : first argument is config other arguments are
     * dynamic by runtime business logic should impl each state
     */
    private String getPhaseInput(Request req) {
        Map<String, String> params = RequestUtil.getPluginRuntimeConf(req);
        if (MapUtils.isEmpty(params)) {
            params = new HashMap<>();
        }

        params.put(PluginConstants.EXTRA_CONFIG, metadata.getExtraConfig());

        return GsonUtils.toJson(params);
    }

    public AbstractProcessorContext getContext() {
        return context;
    }
}
