package com.treefinance.crawler.framework.expression;

import java.util.Map;

import com.treefinance.crawler.framework.context.FieldScopes;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;

/**
 * @author Jerry
 * @since 17:15 2018/5/15
 */
public class ExpressionEngine {

    private final SpiderRequest       request;

    private final SpiderResponse      response;

    private       Map<String, Object> visibleFields;

    public ExpressionEngine(SpiderRequest request, SpiderResponse response) {
        this.request = request;
        this.response = response;
    }

    public Map<String, Object> getVisibleFields() {
        if (visibleFields == null) {
            visibleFields = FieldScopes.getVisibleFields(request, response);
        }
        return visibleFields;
    }

    public String eval(String input) {
        return StandardExpression.eval(input, getVisibleFields());
    }
}
