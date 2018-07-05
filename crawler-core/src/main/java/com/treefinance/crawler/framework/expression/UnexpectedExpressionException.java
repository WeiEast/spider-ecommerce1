package com.treefinance.crawler.framework.expression;

/**
 * @author Jerry
 * @since 09:46 2018/5/30
 */
public class UnexpectedExpressionException extends RuntimeException {

    public UnexpectedExpressionException(String message) {
        super(message);
    }

    public UnexpectedExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
