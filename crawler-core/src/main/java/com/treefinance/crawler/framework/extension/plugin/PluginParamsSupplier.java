package com.treefinance.crawler.framework.extension.plugin;

import java.util.Map;

/**
 * @author Jerry
 * @since 16:59 15/05/2017
 */
public interface PluginParamsSupplier {

    Map<String, String> get() throws Exception;
}
