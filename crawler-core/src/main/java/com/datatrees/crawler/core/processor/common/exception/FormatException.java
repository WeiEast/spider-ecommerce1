/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common.exception;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 13, 2014 9:58:08 AM
 */
public class FormatException extends Exception {

    private static final long serialVersionUID = 4771152402176370660L;

    public FormatException() {
        super();
    }

    public FormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatException(String message) {
        super(message);
    }

    public FormatException(Throwable cause) {
        super(cause);
    }

}
