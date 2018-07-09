package com.treefinance.crawler.framework.expression;

/**
 * @author Jerry
 * @since 09:51 2018/5/30
 */
public class PlaceholderResolveException extends UnexpectedExpressionException {

    public PlaceholderResolveException(String message) {
        super(message);
    }

    public PlaceholderResolveException(String message, Throwable cause) {
        super(message, cause);
    }
}
