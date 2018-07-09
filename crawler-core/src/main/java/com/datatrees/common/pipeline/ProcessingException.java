package com.datatrees.common.pipeline;

/**
 * @author Jerry
 * @since 20:40 2018/5/14
 */
public class ProcessingException extends InvokeException {

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(Throwable cause) {
        super(cause);
    }
}
