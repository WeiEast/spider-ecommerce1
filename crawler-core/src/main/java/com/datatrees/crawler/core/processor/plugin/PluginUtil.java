/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.classfile.ClassLoaderUtils;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.processor.common.exception.PluginException;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Apr 1, 2014 4:27:41 PM
 */
public final class PluginUtil {

    private static final Logger log = LoggerFactory.getLogger(PluginUtil.class);

    private PluginUtil() {
    }

    public static Map<String, Object> checkPluginResult(String result) throws PluginException {
        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
        try {
            resultMap.putAll((LinkedHashMap<String, Object>) GsonUtils.fromJson(result, new TypeToken<LinkedHashMap<String, Object>>() {}.getType()));
            if (resultMap.containsKey("errorCode")) {
                throw new PluginException("error duing plugin invoke!");
            }
        } catch (Exception e) {
            log.error("get Plugin result error!", e);
            throw new PluginException(e.getMessage());
        }
        return resultMap;
    }

    public static String mapPluginInput(Map<String, String> parameters) {
        String result = null;
        try {
            result = GsonUtils.toJson(parameters);
        } catch (Exception e) {
            log.error("get Plugin result error!", e);
        }
        return result;
    }

    //public static AbstractClientPlugin loadPlugin(PluginWrapper plugin,
    //        ClassLoader parent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    //    try {
    //        String mainClass = ((JavaPlugin) plugin.getPlugin()).getMainClass();
    //
    //        ClassLoader parentLoader = parent;
    //        if (parentLoader == null) {
    //            parentLoader = PluginUtil.class.getClassLoader();
    //        }
    //
    //        return ClassLoaderUtils.loadAndInstantiate(plugin.getFile(), parentLoader, plugin.isForceReload(), mainClass, AbstractClientPlugin.class);
    //    } catch (Throwable e) {
    //        log.error("loadPlugin error plugin={}", plugin, e);
    //        return null;
    //    }
    //}
}