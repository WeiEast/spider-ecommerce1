package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;
import java.util.regex.Pattern;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.operation.impl.TripleOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.triple.TripleType;
import com.datatrees.crawler.core.processor.common.*;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TripleOperationImpl extends Operation {

    private static final Logger log = LoggerFactory.getLogger(TripleOperationImpl.class);

    @Override
    public void process(Request request, Response response) throws Exception {
        TripleOperation operation = (TripleOperation) getOperation();
        // ${this}=${a}?${b}:${c}
        String expression = operation.getValue();
        TripleType type = operation.getTripleType();
        if (type == null) {
            type = TripleType.EQ;
        }

        String orginal = getInput(request, response);

        String result;

        if (log.isDebugEnabled()) {
            log.debug("TripleOperation input: " + String.format("value: %s", expression));
        }

        String firstParams = StringUtils.substringBefore(expression, type.getExpression());
        String secondParams = StringUtils.substringBetween(expression, type.getExpression(), "?");
        String firstResult = StringUtils.substringBetween(expression, "?", ":");
        String secondResult = StringUtils.substringAfterLast(expression, ":");

        firstParams = replaceFromContext(firstParams, orginal, request, response);
        secondParams = replaceFromContext(secondParams, orginal, request, response);
        firstResult = replaceFromContext(firstResult, orginal, request, response);
        secondResult = replaceFromContext(secondResult, orginal, request, response);

        result = this.doTriple(type, firstParams, secondParams, firstResult, secondResult);

        if (log.isDebugEnabled()) {
            log.debug("TripleOperation content: " + String.format("orginal: %s,expression: %s , dest: %s", orginal, expression, result));
        }

        response.setOutPut(result);
    }

    private String doTriple(TripleType type, String firstParams, String secondParams, String firstResult, String secondResult) {
        String result = secondResult;
        if (firstParams != null && secondParams != null) {
            switch (type) {
                case EQ:
                    if (firstParams.trim().equals(secondParams.trim())) {
                        result = firstResult;
                    }
                    break;
                case NE:
                    if (!firstParams.equals(secondParams)) {
                        result = firstResult;
                    }
                    break;
                case GT:
                    if (CalculateUtil.calculate(firstParams, 0d) > CalculateUtil.calculate(secondParams, 0d)) {
                        result = CalculateUtil.calculate(firstResult, 0) + "";
                    } else {
                        result = CalculateUtil.calculate(secondResult, 0) + "";
                    }
                    break;
                case LT:
                    if (CalculateUtil.calculate(firstParams, 0d) < CalculateUtil.calculate(secondParams, 0d)) {
                        result = CalculateUtil.calculate(firstResult, 0) + "";
                    } else {
                        result = CalculateUtil.calculate(secondResult, 0) + "";
                    }
                    break;
                case GE:
                    if (CalculateUtil.calculate(firstParams, 0d) >= CalculateUtil.calculate(secondParams, 0d)) {
                        result = CalculateUtil.calculate(firstResult, 0) + "";
                    } else {
                        result = CalculateUtil.calculate(secondResult, 0) + "";
                    }
                    break;
                case LE:
                    if (CalculateUtil.calculate(firstParams, 0d) <= CalculateUtil.calculate(secondParams, 0d)) {
                        result = CalculateUtil.calculate(firstResult, 0) + "";
                    } else {
                        result = CalculateUtil.calculate(secondResult, 0) + "";
                    }
                    break;
                case REGEX:
                    if (PatternUtils.match(secondParams, firstParams)) {
                        result = firstResult;
                    }
                    break;
                case CONTAINS:
                    if (PatternUtils.match(secondParams, firstParams, Pattern.CASE_INSENSITIVE)) {
                        result = firstResult;
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private String replaceFromContext(String params, String orginal, Request request, Response response) {
        if (StringUtils.isNotBlank(params) && params.contains("${this}")) {
            params = ReplaceUtils.replace("${this}", orginal, params);
        }
        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);
        String result = ReplaceUtils.replaceMap(fieldContext, sourceMap, params);
        if (result == params && result != null && result.startsWith("${") && result.endsWith("}")) {
            return "";
        } else {
            return result;
        }
    }
}
