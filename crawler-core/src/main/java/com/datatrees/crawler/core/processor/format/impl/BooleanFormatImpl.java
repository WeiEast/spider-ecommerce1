/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.format.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import org.apache.commons.lang.BooleanUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 13, 2014 9:56:16 AM
 */
public class BooleanFormatImpl extends AbstractFormat {

    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        return BooleanUtils.toBoolean(orginal);
    }

    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof Boolean) {
            return true;
        } else {
            return false;
        }
    }
}
