/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.util;

import com.datatrees.common.protocol.ProtocolStatusCodes;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 4:11:30 PM
 */
@Deprecated
public class ResponseCodeUtil {

    public static boolean isSuccess(int code) {
        return ProtocolStatusCodes.SUCCESS == code;
    }

    public static boolean isRedirector(int code) {
        return code == ProtocolStatusCodes.MOVED || code == ProtocolStatusCodes.TEMP_MOVED;
    }

    public static boolean isFail(int code) {
        return code == ProtocolStatusCodes.EXCEPTION;
    }

}
