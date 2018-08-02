/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.filter;

/**
 * determinate parser can return URL list
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 6, 2014 3:42:42 PM
 */
public class ParserUrlListFilter implements Filter {

    /**
     * @return null to not return url list
     */
    @Override
    public String filter(String url) {
        if (url.equalsIgnoreCase("url")) {
            return url;
        }
        return null;
    }
}
