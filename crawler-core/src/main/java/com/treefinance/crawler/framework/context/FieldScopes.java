package com.treefinance.crawler.framework.context;

import javax.annotation.Nonnull;
import java.util.*;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.FieldExtractResult;
import com.datatrees.crawler.core.processor.extractor.FieldExtractResultSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 14:41 2018/5/16
 */
public final class FieldScopes {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldScopes.class);

    private FieldScopes() {
    }

    public static Map<String, Object> getSharedFields(Request request) {
        return RequestUtil.getSourceMap(request);
    }

    public static Map<String, Object> getExtractFields(Response response) {
        return ResponseUtil.getFieldExtractResultMap(response);
    }

    public static Object getVisibleField(String name, Request request, Response response) {
        Object result = null;
        FieldExtractResultSet fieldExtractResultSet = ResponseUtil.getFieldExtractResultSet(response);
        if (fieldExtractResultSet != null) {
            FieldExtractResult fieldExtractResult = fieldExtractResultSet.get(name);
            if (fieldExtractResult != null) {
                result = fieldExtractResult.getResult();
            }
        }
        if (result == null) {
            result = RequestUtil.getSourceMap(request).get(name);
        }
        LOGGER.debug("Search field in extracted fields or global fields. - name: {}, value: {}", name, result);

        return result;
    }

    /**
     * 获取当前上下文可见的值栈，包含解析出的字段值，共享的值。其中解析值优先于共享值。
     */
    public static Map<String, Object> getVisibleFields(Request request, Response response) {
        List<Map<String, Object>> fieldScopes = new ArrayList<>(2);

        if (response != null) {
            Map<String, Object> extractFields = getExtractFields(response);
            if (MapUtils.isNotEmpty(extractFields)) {
                fieldScopes.add(extractFields);
            }
        }

        if (request != null) {
            Map<String, Object> sharedFields = getSharedFields(request);
            if (MapUtils.isNotEmpty(sharedFields)) {
                fieldScopes.add(sharedFields);
            }
        }

        return merge(fieldScopes);
    }

    /**
     * 合并值栈，优先级按列表由前往后降序。
     */
    @Nonnull
    public static Map<String, Object> merge(@Nonnull List<Map<String, Object>> fieldScopes) {
        if (CollectionUtils.isEmpty(fieldScopes)) {
            return Collections.emptyMap();
        }

        Map<String, Object> map;
        if (fieldScopes.size() == 1) {
            map = fieldScopes.get(0);
        } else {
            map = new HashMap<>();
            for (Map<String, Object> fieldScope : fieldScopes) {
                if (fieldScope == null) continue;

                fieldScope.forEach((key, val) -> {
                    if (val != null) {
                        map.putIfAbsent(key, val);
                    }
                });
            }
        }

        return Collections.unmodifiableMap(map);
    }
}
