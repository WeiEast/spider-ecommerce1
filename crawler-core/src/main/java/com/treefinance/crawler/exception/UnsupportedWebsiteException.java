package com.treefinance.crawler.exception;

/**
 * @author Jerry
 * @since 14:50 2018/6/20
 */
public class UnsupportedWebsiteException extends UnexpectedException {

    public UnsupportedWebsiteException() {
    }

    public UnsupportedWebsiteException(String message) {
        super(message);
    }

    public UnsupportedWebsiteException(Throwable e) {
        super(e);
    }

    public UnsupportedWebsiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
