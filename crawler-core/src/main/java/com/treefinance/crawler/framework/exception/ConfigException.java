package com.treefinance.crawler.framework.exception;

/**
 * @author Jerry
 * @since 20:12 22/01/2018
 */
public class ConfigException extends Exception {

    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }
}
