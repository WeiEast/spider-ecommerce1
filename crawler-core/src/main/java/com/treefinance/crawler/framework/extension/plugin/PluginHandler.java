/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.extension.plugin;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.exception.PluginInvokeException;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.pipeline.ProcessorInvokerAdapter;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 1:10:48 PM
 */
public abstract class PluginHandler<T extends AbstractPlugin> extends ProcessorInvokerAdapter {

    private final T                        metadata;

    private final AbstractProcessorContext context;

    public PluginHandler(@Nonnull T metadata, @Nonnull AbstractProcessorContext context) {
        this.metadata = Objects.requireNonNull(metadata);
        this.context = Objects.requireNonNull(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        try {
            Map<String, String> params = new HashMap<>();

            Object input = request.getInput();
            if (input instanceof Map) {
                params.putAll((Map<String, String>) input);
            }

            params.put(PluginConstants.EXTRA_CONFIG, metadata.getExtraConfig());

            String args =  GsonUtils.toJson(params);

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

    protected abstract Object invokePlugin(T metadata, String args, SpiderRequest request) throws Exception;

    public AbstractProcessorContext getContext() {
        return context;
    }
}
