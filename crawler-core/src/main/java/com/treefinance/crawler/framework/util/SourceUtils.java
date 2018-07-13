package com.treefinance.crawler.framework.util;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.treefinance.crawler.framework.context.FieldScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 17:18 2018/7/13
 */
public final class SourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceUtils.class);

    private SourceUtils() {
    }

    public static Object getSourceValue(String sourceId, Request request, Response response) {
        Object result = FieldScopes.getVisibleField(sourceId, request, response);

        LOGGER.debug("Field value from sourceId: {}, result: {}", sourceId, result);

        return result;
    }
}
