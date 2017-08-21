package com.datatrees.crawler.core.processor.plugin;

/**
 * @author Jerry
 * @since 14:54 15/05/2017
 */
public interface PluginCallable<R> {

  R call(Plugin plugin) throws Exception;
}
