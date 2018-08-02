/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.PythonPlugin;
import com.datatrees.crawler.core.domain.config.plugin.impl.ShellPlugin;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月7日 下午7:05:31
 */
public abstract class AbstractWebsiteConfig implements Serializable {

    /**
     *
     */
    private static final long                 serialVersionUID = 2850518517814023140L;

    private              List<AbstractPlugin> pluginList       = new ArrayList<>();

    private              String               parentWebsiteName;

    public AbstractWebsiteConfig() {
        super();
    }

    @Tag("plugin-definition")
    public List<AbstractPlugin> getPluginList() {
        return Collections.unmodifiableList(pluginList);
    }

    @Node(value = "plugin-definition/plugin", types = {JavaPlugin.class, ShellPlugin.class, PythonPlugin.class}, registered = true)
    public void setPluginList(AbstractPlugin plugin) {
        this.pluginList.add(plugin);
    }

    @Attr("extends")
    public String getParentWebsiteName() {
        return parentWebsiteName;
    }

    @Node("@extends")
    public void setParentWebsiteName(String parentWebsiteName) {
        this.parentWebsiteName = parentWebsiteName;
    }

    public void clone(AbstractWebsiteConfig cloneFrom) {
        if (CollectionUtils.isEmpty(pluginList)) {
            this.pluginList.addAll(cloneFrom.getPluginList());
        }
    }
}
