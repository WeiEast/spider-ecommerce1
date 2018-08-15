/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.util;

import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.expression.spring.SpelExpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 下午1:47:57
 */
public final class CalculateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateUtils.class);

    private CalculateUtils() {
    }

    public static Double calculate(String expression) {
        return calculate(expression, 0d, Double.class);
    }

    public static <T> T calculate(String expression, T defaultValue, Class<T> clazz) {
        return SpelExpParser.parse(expression, defaultValue, clazz);
    }

    public static <T> T calculate(String expression, SpiderRequest request, SpiderResponse response, T defaultValue, Class<T> clazz) {
        String exp = StandardExpression.eval(expression, request, response);

        LOGGER.debug("Actual calculate expression: {}", exp);

        return calculate(exp, defaultValue, clazz);
    }

    public static Double calculate(String expression, SpiderRequest request, SpiderResponse response, Double defaultValue) {
        return calculate(expression, request, response, defaultValue, Double.class);
    }

    public static double calculate(String expression, SpiderRequest request, SpiderResponse response) {
        return calculate(expression, request, response, 0d);
    }

}
