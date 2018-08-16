/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.processor.common.ProcessorResult;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.util.SynchronizedMap;
import com.treefinance.crawler.framework.context.ProcessContext;
import com.treefinance.crawler.framework.extension.manager.WrappedExtension;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月7日 下午7:09:02
 */
public abstract class AbstractProcessorContext extends ProcessContext {

    protected final Long                            taskId;

    protected       Map<String, Object>             context;

    private final   Map<String, Object>             statusContext;

    private final   Map<Thread, Object>             threadContext;

    private         ProcessorResult<String, Object> processorResult;

    public AbstractProcessorContext(Website website, Long taskId) {
        super(website);
        this.taskId = Objects.requireNonNull(taskId);
        this.context = new SynchronizedMap<>();
        this.statusContext = new SynchronizedMap<>();
        this.threadContext = new SynchronizedMap<>();
        this.processorResult = new ProcessorResult<>();
    }

    public abstract void init();

    public Long getTaskId() {
        return taskId;
    }

    @Deprecated
    public AbstractPlugin getPluginDescByID(String pid) {
        return super.getPluginMetadataById(pid);
    }

    /**
     * @return the context
     */
    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        if (context == null) {
            getContext().clear();
        } else {
            this.context = new SynchronizedMap<>(context);
        }
    }

    /**
     * the shared fields map with the global context scope.
     * @return the unmodifiable map.
     * @see #getContext()
     */
    public Map<String, Object> getVisibleScope() {
        return Collections.unmodifiableMap(context);
    }

    public Object getAttribute(String name) {
        return getContext().get(name);
    }

    public void addAttribute(String name, Object value) {
        if (value == null) {
            getContext().remove(name);
        } else {
            getContext().put(name, value);
        }
    }

    public void addAttributes(Map<String, Object> attributes) {
        if (attributes != null) {
            getContext().putAll(attributes);
        }
    }

    /**
     * @return the statusContext
     */
    public Map<String, Object> getStatusContext() {
        return statusContext;
    }

    public void addStatusAttr(String name, Object value) {
        getStatusContext().put(name, value);
    }

    /**
     * @return the threadContext
     */
    public Map<Thread, Object> getThreadContext() {
        return threadContext;
    }

    public Object getThreadAttr(Thread thread, String key) {
        Map<String, Object> map = threadAttributes(thread);
        return map.get(key);
    }

    public Object computeThreadAttrIfAbsent(Thread thread, String key, Function<String, Object> mappingFunction) {
        Map<String, Object> map = threadAttributes(thread);

        return map.computeIfAbsent(key, mappingFunction);
    }

    public void removeThreadAttr(Thread thread, String key) {
        Map<String, Object> map = threadAttributes(thread);
        map.remove(key);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private Map<String, Object> threadAttributes(Thread thread) {
        return (Map<String, Object>) getThreadContext().computeIfAbsent(thread, t -> new HashMap<String, Object>());
    }

    /**
     * @return the processorResult
     */
    public ProcessorResult<String, Object> getProcessorResult() {
        return processorResult;
    }

    public void setProcessorResult(Map<String, Object> processorResult) {
        if (processorResult == null) {
            getProcessorResult().clear();
        } else {
            this.processorResult = new ProcessorResult<>(processorResult);
        }
    }

    /**
     * the shared fields map with the global processor-result scope.
     * @return the unmodifiable map.
     */
    public Map<String, Object> getResultScope() {
        return Collections.unmodifiableMap(processorResult);
    }

    public void addProcessorResult(String name, Object value) {
        if (value == null) {
            getProcessorResult().remove(name);
        } else {
            getProcessorResult().put(name, value);
        }
    }

    public void addProcessorResult(Map<String, Object> processorResult) {
        if (processorResult != null) {
            getProcessorResult().putAll(processorResult);
        }
    }

    /**
     * 给context赋值
     * @param key
     * @param value
     * @return
     */
    public void set(String key, Object value) {
        addAttribute(key, value);
    }

    /**
     * 给context赋值
     * @param key
     * @param value
     * @return
     */
    public void setString(String key, Object value) {
        if (null == value) {
            getContext().put(key, null);
            return;
        }
        getContext().put(key, value.toString());
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
        Object v = getContext().get(key);
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
        Object v = getContext().get(key);
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
        Object v = getContext().get(key);
        if (null == v) {
            return null;
        } else if (v instanceof Boolean) {
            return (Boolean) v;
        } else if (v instanceof String) {
            return Boolean.valueOf((String) v);
        }
        throw new ClassCastException("Can not cast class '" + v.getClass() + "' to 'Boolean'.");
    }

    public <T> WrappedExtension<T> loadExtension(@Nonnull AbstractPlugin pluginMetadata, @Nonnull Class<T> extensionType) {
        Objects.requireNonNull(pluginMetadata);

        return getPluginManager().loadExtension(pluginMetadata, extensionType, taskId);
    }

    public AbstractClientPlugin loadPlugin(@Nonnull JavaPlugin pluginMetadata) {
        Objects.requireNonNull(pluginMetadata);
        String fileName = pluginMetadata.getFileName();
        String mainClass = pluginMetadata.getMainClass();

        return getPluginManager().loadPlugin(fileName, mainClass, taskId);
    }
}
