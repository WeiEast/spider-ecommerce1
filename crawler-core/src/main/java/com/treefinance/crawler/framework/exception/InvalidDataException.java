package com.treefinance.crawler.framework.exception;

import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;

/**
 * @author Jerry
 * @since 14:27 2018/5/28
 */
public class InvalidDataException extends ResultEmptyException {

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
