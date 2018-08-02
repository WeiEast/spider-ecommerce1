/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.bean;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 12, 2014 3:36:53 PM
 */
public class StatusUtil {

    public static boolean success(int code) {
        return code == Status.VISIT_SUCCESS;
    }

    public static boolean serverError(int code) {
        return code == Status.VISIT_ERROR;
    }

    public static boolean blocked(int code) {
        return code == Status.BLOCKED;
    }

    public static String format(int code) {
        String result = null;
        switch (code) {
            case Status.BLOCKED:
                result = "BLOCKED";
                break;
            case Status.VISIT_ERROR:
                result = "VISIT PAGE ERROR";
                break;

            default:
                result = "SUCCESS";
                break;
        }
        return result;
    }

}
