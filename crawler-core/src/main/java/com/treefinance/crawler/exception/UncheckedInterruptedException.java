package com.treefinance.crawler.exception;

/**
 * @author Jerry
 * @since 21:17 2018/7/26
 */
public class UncheckedInterruptedException extends RuntimeException {

    public UncheckedInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
