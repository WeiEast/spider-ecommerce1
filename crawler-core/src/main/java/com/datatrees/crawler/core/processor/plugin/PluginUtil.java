/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import java.util.Collections;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.common.exception.PluginException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Apr 1, 2014 4:27:41 PM
 */
public final class PluginUtil {

    private PluginUtil() {
    }

    public static Map<String, Object> checkPluginResult(String result) throws PluginException {
        if (StringUtils.isEmpty(result)) {
            return Collections.emptyMap();
        }

        Map<String, Object> map;
        try {
            map = GsonUtils.fromJson(result, new TypeToken<Map<String, Object>>() {}.getType());
        } catch (Exception e) {
            throw new PluginException("Error parsing plugin result!", e);
        }

        if (map.containsKey("errorCode")) {
            throw new PluginException("error invoking plugin!");
        }

        return map;
    }
}
