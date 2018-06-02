package com.treefinance.crawler.framework.expression;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.context.FieldScopes;

/**
 * @author Jerry
 * @since 17:09 2018/5/15
 */
public final class StandardExpression {

    public static final List<String> URL_ENCODED_KEYS;

    static {
        String value = PropertiesConfiguration.getInstance().get("replaced.encode.keys", "keyword");

        String[] keys = value.split(",");
        URL_ENCODED_KEYS = Arrays.stream(keys).filter(key -> !key.isEmpty()).distinct().collect(Collectors.toList());
    }

    private StandardExpression() {
    }

    public static boolean find(String value) {
        return ExpressionExecutor.findExp(value);
    }

    public static String eval(String value, List<Map<String, Object>> fieldScopes) {
        return ExpressionExecutor.evalExp(value, () -> FieldScopes.merge(fieldScopes));
    }

    public static String eval(String value, Request request, Response response) {
        return ExpressionExecutor.evalExp(value, () -> FieldScopes.getVisibleFields(request, response));
    }

    public static String eval(String value, Map<String, Object> fieldStack) {
        return ExpressionExecutor.evalExp(value, fieldStack);
    }

    public static Object evalWithObject(String value, List<Map<String, Object>> fieldScopes) {
        return ExpressionExecutor.evalExpWithObject(value, () -> FieldScopes.merge(fieldScopes));
    }

    public static Object evalWithObject(String value, Request request, Response response) {
        return ExpressionExecutor.evalExpWithObject(value, () -> FieldScopes.getVisibleFields(request, response));
    }

    public static Object evalWithObject(String value, Map<String, Object> fieldStack) {
        return ExpressionExecutor.evalExpWithObject(value, fieldStack);
    }

    public static String evalUrl(String value, List<Map<String, Object>> fieldScopes, String charset) {
        return ExpressionExecutor.evalExp(value, () -> FieldScopes.merge(fieldScopes), URL_ENCODED_KEYS, charset);
    }

    public static String evalUrl(String value, Request request, Response response) {
        String charset = RequestUtil.getContentCharset(request);

        return evalUrl(value, request, response, charset);
    }

    public static String evalUrl(String value, Request request, Response response, String charset) {
        return ExpressionExecutor.evalExp(value, () -> FieldScopes.getVisibleFields(request, response), URL_ENCODED_KEYS, charset);
    }

    public static String evalUrl(String value, Map<String, Object> fieldStack, String charset) {
        return ExpressionExecutor.evalExp(value, fieldStack, URL_ENCODED_KEYS, charset);
    }
}
