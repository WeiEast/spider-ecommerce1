/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol.http;

/**
 * Can be used to identify problems during creation of Authentication objects. In the future it may
 * be used as a method of collecting authentication failures during Http protocol transfer in order
 * to present the user with credentials required during a future fetch.
 */
public class HttpAuthenticationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with null as its detail message.
     */
    public HttpAuthenticationException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message. The detail message is saved for later retrieval by the
     *        {@link Throwable#getMessage()} method.
     */
    public HttpAuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     * 
     * @param message the detail message. The detail message is saved for later retrieval by the
     *        {@link Throwable#getMessage()} method.
     * @param cause the cause (use {@link #getCause()} to retrieve the cause)
     */
    public HttpAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and detail message from given clause if
     * it is not null.
     * 
     * @param cause the cause (use {@link #getCause()} to retrieve the cause)
     */
    public HttpAuthenticationException(Throwable cause) {
        super(cause);
    }

}
