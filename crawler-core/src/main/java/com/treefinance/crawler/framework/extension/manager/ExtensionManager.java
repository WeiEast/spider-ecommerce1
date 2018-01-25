package com.treefinance.crawler.framework.extension.manager;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.treefinance.crawler.framework.exception.ExtensionException;

/**
 * @author Jerry
 * @since 11:03 23/01/2018
 */
public interface ExtensionManager {

    <T> WrappedExtension<T> loadExtension(AbstractPlugin metadata, Class<T> extensionType, Long taskId) throws ExtensionException;

    <T> T loadExtension(String jarName, String mainClass, Class<T> extensionType, Long taskId) throws ExtensionException;

}
