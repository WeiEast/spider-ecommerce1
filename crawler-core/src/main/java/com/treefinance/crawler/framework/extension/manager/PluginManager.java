package com.treefinance.crawler.framework.extension.manager;

import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.treefinance.crawler.framework.exception.PluginException;

/**
 * @author Jerry
 * @since 01:02 23/01/2018
 */
public interface PluginManager extends ExtensionManager {

    /**
     * 从jar加载class
     */
    AbstractClientPlugin loadPlugin(String jarName, String mainClass, Long taskId) throws PluginException;

}
