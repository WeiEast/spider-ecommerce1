package com.treefinance.crawler.framework.expression.spring;

import com.datatrees.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author Jerry
 * @since 14:16 2018/5/31
 */
public final class SpelExpParser {

    private static final Logger           LOGGER = LoggerFactory.getLogger(SpelExpParser.class);
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private SpelExpParser() {
    }

    public static <T> T parse(String expression, Class<T> clazz) {
        Expression exp = PARSER.parseExpression(expression);

        return exp.getValue(clazz);
    }

    public static <T> T parse(String expression, T defaultValue, Class<T> clazz) {
        if (StringUtils.isBlank(expression)) {
            LOGGER.warn("Blank expression and return default value: {}", defaultValue);
            return defaultValue;
        }

        return parse(expression, clazz);
    }
}
