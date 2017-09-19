/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import java.io.File;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.common.exception.PluginException;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015-1-12 下午8:07:06
 */
public class SimplePluginManager extends PluginManager {

    private static final Logger logger               = LoggerFactory.getLogger(SimplePluginManager.class);
    private              String pluginPath           = PropertiesConfiguration.getInstance().get("plugin.store.path", "/tmp");
    private              String pluginCacheKeyPrefix = "plugin_local_prefix_";

    @Override
    public AbstractClientPlugin loadPlugin(String jarName, String mainClass) {
        return null;
    }

    @Override
    public PluginWrapper getPlugin(String websiteName, AbstractPlugin pluginDesc) throws PluginException {
        File plugin = new File("src/test/resources/plugin/" + websiteName + "login-plugin.jar");
        return new PluginWrapper(plugin, pluginDesc);
    }

}
