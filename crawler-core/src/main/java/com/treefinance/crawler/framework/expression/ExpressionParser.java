package com.treefinance.crawler.framework.expression;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.context.FieldScopes;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;

/**
 * @author Jerry
 * @since 15:11 2018/5/16
 */
public final class ExpressionParser {

    private final ExpressionMatcher matcher;

    private ExpressionParser() {
        this(null);
    }

    private ExpressionParser(String text) {
        this.matcher = ExpressionMatcher.match(text);
    }

    public static ExpressionParser parse(String text) {
        return new ExpressionParser(text);
    }

    public void reset(String newText) {
        matcher.reset(newText);
    }

    public boolean findExp() {
        return matcher.findExp();
    }

    public String evalExp(List<Map<String, Object>> fieldScopes) {
        return matcher.evalExp(() -> new ExpEvalContext(FieldScopes.merge(fieldScopes)));
    }

    public String evalExp(List<Map<String, Object>> fieldScopes, boolean failOnUnknown, boolean allowNull) {
        return matcher.evalExp(() -> new ExpEvalContext(FieldScopes.merge(fieldScopes), failOnUnknown, allowNull));
    }

    public String evalExp(Map<String, Object> placeholder) {
        return matcher.evalExp(new ExpEvalContext(placeholder));
    }

    public String evalExp(Map<String, Object> placeholder, boolean failOnUnknown, boolean allowNull) {
        return matcher.evalExp(new ExpEvalContext(placeholder, failOnUnknown, allowNull));
    }

    public String evalExp(Map<String, Object> placeholder, BiFunction<String, String, String> mappingFunction) {
        return matcher.evalExp(new ExpEvalContext(placeholder), mappingFunction);
    }

    public Object evalExpWithObject(Map<String, Object> placeholder) {
        return matcher.evalExpWithObject(new ExpEvalContext(placeholder));
    }

    public String eval(SpiderRequest request, SpiderResponse response) {
        return matcher.evalExp(() -> new ExpEvalContext(FieldScopes.getVisibleFields(request, response)));
    }

    public Object evalWithObject(SpiderRequest request, SpiderResponse response) {
        return matcher.evalExpWithObject(() -> new ExpEvalContext(FieldScopes.getVisibleFields(request, response)));
    }

    public String evalUrl(SpiderRequest request, SpiderResponse response, boolean failOnUnknown, boolean allowNull) {
        return matcher.evalExp(() -> {
            String charset = RequestUtil.getContentCharset(request);
            Map<String, Object> visibleFields = FieldScopes.getVisibleFields(request, response);
            return new UrlExpEvalContext(visibleFields, failOnUnknown, allowNull, StandardExpression.URL_ENCODED_KEYS, charset);
        });
    }

    public String evalUrl(SpiderRequest request, SpiderResponse response, String charset) {
        return matcher.evalExp(() -> {
            Map<String, Object> visibleFields = FieldScopes.getVisibleFields(request, response);
            return new UrlExpEvalContext(visibleFields, StandardExpression.URL_ENCODED_KEYS, charset);
        });
    }

}
