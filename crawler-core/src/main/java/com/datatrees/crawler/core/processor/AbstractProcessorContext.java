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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.processor.common.ProcessorResult;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.treefinance.crawler.framework.context.ProcessContext;
import com.treefinance.crawler.framework.extension.manager.WrappedExtension;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月7日 下午7:09:02
 */
public abstract class AbstractProcessorContext extends ProcessContext {

    protected final Long                        taskId;
    protected final Map<String, Object>         context;
    private final   Map<String, Object>         statusContext;
    private final   Map<Thread, Object>         threadContext;
    private final   ProcessorResult             processorResult;
    private final   ProcessorResult             processorLog;


    public AbstractProcessorContext(Website website, Long taskId) {
        super(website);
        this.taskId = Objects.requireNonNull(taskId);
        this.context = new ConcurrentHashMap<>();
        this.statusContext = new ConcurrentHashMap<>();
        this.threadContext = new ConcurrentHashMap<>();
        this.processorResult = new ProcessorResult();
        this.processorLog = new ProcessorResult();
    }

    public abstract void init();

    public Long getTaskId() {
        return taskId;
    }

    /**
     * @return the context
     */
    public Map<String, Object> getContext() {
        return context;
    }

    @Deprecated
    public AbstractPlugin getPluginDescByID(String pid) {
        return super.getPluginMetadataById(pid);
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
        context.put(key, value.toString());
    }

    /**
     * 从context取值
     * @param key
     * @return
     */
    public String getString(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object v = context.get(key);
        if (null == v) {
            return null;
        }
        return v.toString();
    }

    /**
     * 从context取值
     * @param key
     * @return
     */
    public Long getLong(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object v = context.get(key);
        if (null == v) {
            return null;
        } else if (v instanceof Long) {
            return (Long) v;
        } else if (v instanceof String) {
            return Long.valueOf((String) v);
        }
        return Long.valueOf(v.toString());
    }

    /**
     * 从context取值
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object v = context.get(key);
        if (null == v) {
            return null;
        }else if (v instanceof Boolean) {
            return (Boolean) v;
        } else if (v instanceof String) {
            return Boolean.valueOf((String) v);
        }
        throw new ClassCastException("Can not cast class '"+v.getClass()+"' to 'Boolean'.");
    }

    public <T> WrappedExtension<T> loadExtension(AbstractPlugin pluginMetadata, Class<T> extensionType) {
        Objects.requireNonNull(pluginMetadata);
        Long taskId = getLong(AttributeKey.TASK_ID);

        return getPluginManager().loadExtension(pluginMetadata, extensionType, taskId);
    }

    public AbstractClientPlugin loadPlugin(JavaPlugin pluginMetadata) {
        Objects.requireNonNull(pluginMetadata);
        String fileName = pluginMetadata.getFileName();
        String mainClass = pluginMetadata.getMainClass();
        Long taskId = getLong(AttributeKey.TASK_ID);

        return getPluginManager().loadPlugin(fileName, mainClass, taskId);
    }
}
