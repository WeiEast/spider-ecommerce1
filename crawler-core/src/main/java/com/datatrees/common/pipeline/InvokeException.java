package com.datatrees.common.pipeline;

/**
 * @author Jerry
 * @since 11:51 2018/5/28
 */
public class InvokeException extends Exception {

    public InvokeException() {
    }

    public InvokeException(String message) {
        super(message);
    }

    public InvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeException(Throwable cause) {
        super(cause);
    }
}
