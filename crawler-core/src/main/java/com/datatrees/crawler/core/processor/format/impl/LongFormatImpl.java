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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 13, 2014 9:57:05 AM
 */
public class LongFormatImpl extends AbstractFormat {

    private static final Logger log = LoggerFactory.getLogger(LongFormatImpl.class);

    /**
     * number format
     */
    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        Long result = null;
        if (StringUtils.isEmpty(orginal)) {
            log.warn("orginal empty!");
            return result;
        } else {
            orginal = orginal.trim().replaceAll("\\s", "");
        }
        try {
            result = Long.parseLong(orginal);
        } catch (Exception e) {
            log.warn("parser " + orginal + " to int error.");
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.format.AbstractFormat#isResultType(java.lang.Object)
     */
    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof Long) {
            return true;
        } else {
            return false;
        }
    }

}
