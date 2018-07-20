/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.treefinance.crawler.framework.context.FieldScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月21日 下午7:24:26
 */
public final class SourceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceUtil.class);

    private SourceUtil() {
    }

    public static Object getSourceMap(String sourceId, Request request, Response response) {
        Object result = FieldScopes.getVisibleField(sourceId, request, response);

        LOGGER.debug("Field value from sourceId: {}, result: {}", sourceId, result);

        return result;
    }

}
