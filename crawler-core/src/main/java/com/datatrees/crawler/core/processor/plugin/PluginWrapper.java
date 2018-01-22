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
import com.treefinance.crawler.framework.extension.PluginFile;

/**
 * plugin wrapper which wrapper
 * plugin description and plugin location
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 5:39:30 PM
 */
public class PluginWrapper extends PluginFile {

    public PluginWrapper() {
    }

    public PluginWrapper(File file, AbstractPlugin plugin) {
        super(plugin, file);
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

    public void setPlugin(AbstractPlugin plugin) {
        super.setMetadata(plugin);
    }

    public AbstractPlugin getPlugin() {
        return super.getMetadata();
    }

    public boolean isForceReload() {
        return super.isReload();
    }

    public void setForceReload(boolean forceReload) {
        super.setReload(forceReload);
    }

}
