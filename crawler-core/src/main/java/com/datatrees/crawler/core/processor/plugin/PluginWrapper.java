/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import java.io.File;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.plugin.PluginPhase;
import com.datatrees.crawler.core.domain.config.plugin.PluginType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * plugin wrapper which wrapper
 * plugin description and plugin location
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 5:39:30 PM
 */
public class PluginWrapper {

    private File           file;
    private AbstractPlugin plugin;
    private boolean        forceReload;

    public PluginWrapper() {
    }

    public PluginWrapper(File file, AbstractPlugin plugin) {
        super();
        this.file = file;
        this.plugin = plugin;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public AbstractPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    public PluginType getType() {
        return getPlugin().getType();
    }

    public PluginPhase getPhase() {
        return getPlugin().getPhase();
    }

    public String getExtraConfig() {
        return getPlugin().getExtraConfig();
    }

    public boolean isForceReload() {
        return forceReload;
    }

    public void setForceReload(boolean forceReload) {
        this.forceReload = forceReload;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("file", file.getAbsolutePath()).append("plugin", plugin.getFileName()).append("forceReload", forceReload).toString();
    }
}
