package com.treefinance.crawler.framework.context;

import javax.annotation.Nonnull;
import java.util.*;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import org.apache.commons.collections.CollectionUtils;
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
        return FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
    }

    public static Object getVisibleField(String name, Request request, Response response) {
        Object result = null;
        Map<String, FieldExtractorWarpper> map = ResponseUtil.getResponseFieldResult(response);
        FieldExtractorWarpper fieldExtractResult = map.get(name);
        if (fieldExtractResult != null) {
            result = fieldExtractResult.getResult();
        }
        if (result == null) {
            result = RequestUtil.getSourceMap(request).get(name);
        }
        LOGGER.debug("Search visible field in extracted field scope or global field scope. - name: {},result: {}", name, result);

        return result;
    }

    /**
     * 获取当前上下文可见的值栈，包含解析出的字段值，共享的值。其中解析值优先于共享值。
     */
    public static Map<String, Object> getVisibleFields(Request request, Response response) {
        List<Map<String, Object>> fieldScopes = new ArrayList<>();

        if (response != null) {
            Map<String, Object> extractFields = getExtractFields(response);
            fieldScopes.add(extractFields);
        }

        if (request != null) {
            Map<String, Object> sharedFields = getSharedFields(request);
            fieldScopes.add(sharedFields);
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
