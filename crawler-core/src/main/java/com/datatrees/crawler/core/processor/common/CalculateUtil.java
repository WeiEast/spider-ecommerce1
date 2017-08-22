/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.StringUtils;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 下午1:47:57
 */
public class CalculateUtil {
    private static final Logger log = LoggerFactory.getLogger(CalculateUtil.class);
    private static final ExpressionParser parser = new SpelExpressionParser();

    public static Double sourceCalculate(Request request, String expression, Double defaultValue) {
        expression = SourceUtil.sourceExpression(request, expression);
        return CalculateUtil.calculate(expression, defaultValue);
    }



    public static Double calculate(String expression) {
        return calculate(expression, 0d);
    }

    public static Double calculate(String expression, Double defaultValue) {
        try {
            if (StringUtils.isNotBlank(expression) && !expression.contains("$")) {
                Double calculateResult = Double.valueOf(parser.parseExpression(expression).getValue().toString());
                log.debug("do calculate with expression:" + expression + ",result:" + calculateResult);
                return calculateResult;
            }
        } catch (Exception e) {
            log.error("calculate error with expression:" + expression + " ," + e.getMessage(), e);
        }
        log.warn("return defaultValue " + defaultValue + ",with expression:" + expression);
        return defaultValue;
    }

    public static Object sourceCalculate(Request request, Response response, String expression, Object defaultValue) {
        expression = SourceUtil.sourceExpression(request, response, expression);
        return CalculateUtil.calculate(expression, defaultValue);
    }

    public static Object calculate(String expression, Object defaultValue) {
        try {
            if (StringUtils.isNotBlank(expression) && !expression.contains("$")) {
                Object calculateResult = parser.parseExpression(expression).getValue();
                log.debug("do calculate with expression:" + expression + ",result:" + calculateResult);
                return calculateResult;
            }
        } catch (Exception e) {
            log.error("calculate error with expression:" + expression + " ," + e.getMessage(), e);
        }
        log.warn("return defaultValue " + defaultValue + ",with expression:" + expression);
        return defaultValue;
    }

}
