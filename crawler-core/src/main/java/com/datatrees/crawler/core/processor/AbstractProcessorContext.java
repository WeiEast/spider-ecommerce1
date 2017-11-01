/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.properties.Properties;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.domain.config.service.impl.TaskHttpService;
import com.datatrees.crawler.core.processor.common.ProcessorResult;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.processor.plugin.PluginWrapper;
import com.datatrees.crawler.core.util.SynchronizedMap;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月7日 下午7:09:02
 */
public abstract class AbstractProcessorContext {

    protected final Website                     website;
    protected final Map<String, Object>         context;
    protected final Map<String, AbstractPlugin> pluginMaps;
    protected final Map<String, Object>         statusContext;
    protected final Map<Thread, Object>         threadContext;
    private final   ProcessorResult             processorResult;
    private final   ProcessorResult             processorLog;
    private         PluginManager               pluginManager;

    public AbstractProcessorContext(Website website) {
        Preconditions.checkNotNull(website, "input for website should not be empty!");
        this.website = website;
        context = new SynchronizedMap<>();
        pluginMaps = new SynchronizedMap<>();
        statusContext = new SynchronizedMap<>();
        threadContext = new ConcurrentHashMap<>();
        processorResult = new ProcessorResult();
        processorLog = new ProcessorResult();
    }

    public abstract void init();

    /**
     * @return the website
     */
    public Website getWebsite() {
        return website;
    }

    public String getWebsiteName() {
        return website.getWebsiteName();
    }

    /**
     * @return the context
     */
    public Map<String, Object> getContext() {
        return context;
    }

    public AbstractPlugin getPluginDescByID(String pid) {
        return pluginMaps.get(pid);
    }

    /**
     * @return the statusContext
     */
    public Map<String, Object> getStatusContext() {
        return statusContext;
    }

    /**
     * @return the threadContext
     */
    public Map<Thread, Object> getThreadContext() {
        return threadContext;
    }

    /**
     * @return the processorResult
     */
    public ProcessorResult getProcessorResult() {
        return processorResult;
    }

    /**
     * @return the processorLog
     */
    public ProcessorResult getProcessorLog() {
        return processorLog;
    }

    /**
     * 给context赋值
     * @param key
     * @param value
     * @return
     */
    public void set(String key, Object value) {
        context.put(key, value);
    }

    /**
     * 给context赋值
     * @param key
     * @param value
     * @return
     */
    public void setString(String key, Object value) {
        if (null == value) {
            context.put(key, null);
            return;
        }
        context.put(key, String.valueOf(value));
    }

    /**
     * 从context取值
     * @param key
     * @return
     */
    public String getString(String key) {
        if (StringUtils.isBlank(key) || !context.containsKey(key)) {
            return null;
        }
        Object v = context.get(key);
        if (null == v) {
            return null;
        }
        return String.valueOf(context.get(key));
    }

    /**
     * 从context取值
     * @param key
     * @return
     */
    public Long getLong(String key) {
        if (StringUtils.isBlank(key) || !context.containsKey(key)) {
            return null;
        }
        Object v = context.get(key);
        if (null == v) {
            return null;
        }
        return Long.valueOf(getString(key));
    }

    /**
     * 从context取值
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        if (StringUtils.isBlank(key) || !context.containsKey(key)) {
            return null;
        }
        Object v = context.get(key);
        if (null == v) {
            return null;
        }
        return Boolean.valueOf(getString(key));
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public PluginWrapper createPluginWrapper(AbstractPlugin plugin) {
        return getPluginManager().getPlugin(getWebsiteName(), plugin);
    }

    public PluginWrapper createPluginWrapper(String pluginId) {
        AbstractPlugin pluginDesc = getPluginDescByID(pluginId);

        return createPluginWrapper(pluginDesc);
    }

    public AbstractService getDefaultService() {
        AbstractService service = null;
        if (null != website && null != website.getSearchConfig() && null != website.getSearchConfig().getProperties()) {
            Properties properties = website.getSearchConfig().getProperties();
            Boolean useTaskHttp = properties.getUseTaskHttp();
            if (BooleanUtils.isTrue(useTaskHttp)) {
                service = new TaskHttpService();
                service.setServiceType("task_http");
            }
        }
        return service;
    }
}
