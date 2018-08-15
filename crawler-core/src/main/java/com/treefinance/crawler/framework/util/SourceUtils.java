package com.treefinance.crawler.framework.util;

import com.treefinance.crawler.framework.context.FieldScopes;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
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

    public static Object getSourceFieldValue(String sourceId, SpiderRequest request, SpiderResponse response) {
        Object result = FieldScopes.getVisibleField(sourceId, request, response);

        LOGGER.debug("Field value from sourceId: {}, result: {}", sourceId, result);

        return result;
    }
}
