/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common.exception;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 下午2:33:22
 */
public class NoProxyException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 5091756903422887675L;

    /**
     *
     */
    public NoProxyException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public NoProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public NoProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public NoProxyException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public NoProxyException(Throwable cause) {
        super(cause);
    }

}
