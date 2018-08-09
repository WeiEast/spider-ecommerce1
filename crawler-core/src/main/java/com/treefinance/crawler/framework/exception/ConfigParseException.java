package com.treefinance.crawler.framework.exception;

/**
 * @author Jerry
 * @since 16:25 2018/7/13
 */
public class ConfigParseException extends ConfigException {

    public ConfigParseException(String message) {
        super(message);
    }

    public ConfigParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
