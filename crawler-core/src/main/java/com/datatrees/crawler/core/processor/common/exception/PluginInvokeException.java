package com.datatrees.crawler.core.processor.common.exception;

/**
 * @author Jerry
 * @since 11:48 14/08/2017
 */
public class PluginInvokeException extends Exception {
    public PluginInvokeException(String message) {
        super(message);
    }

    public PluginInvokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
