package com.datatrees.crawler.core.processor.plugin;

import java.util.Map;

/**
 * @author Jerry
 * @since 16:59 15/05/2017
 */
public interface PluginConfSupplier {

  Map<String, String> get(PluginWrapper pluginWrapper) throws Exception;
}
