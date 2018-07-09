package com.treefinance.crawler.framework.expression;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.treefinance.crawler.framework.context.FieldScopes;

/**
 * @author Jerry
 * @since 17:15 2018/5/15
 */
public class ExpressionEngine {

    private final Request             request;
    private final Response            response;
    private       Map<String, Object> visibleFields;

    public ExpressionEngine(Request request, Response response) {
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
