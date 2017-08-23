/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.filter;

/**
 * determinate current field need to request
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 4:45:53 PM
 */
public class FieldRequestFilter implements Filter {

    /**
     *
     */
    @Override
    public String filter(String data) {
        if (data.toLowerCase().endsWith("url")) {
            return null;
        }
        ;
        return data;
    }

}
