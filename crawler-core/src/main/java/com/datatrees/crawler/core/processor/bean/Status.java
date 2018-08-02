/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.bean;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 12, 2014 3:29:12 PM
 */
public interface Status {

    int PRCESS_EXCEPTION = -2005;
    int BLOCKED          = -2004;
    int NO_SEARCH_RESULT = -2003;
    int FILTERED         = -2002;
    int LAST_PAGE        = -2000;
    int VISIT_SUCCESS    = 1;
    int VISIT_REDIRECT   = 2;
    int VISIT_ERROR      = 3;
    int NO_PROXY         = 4;
    int REQUEUE          = 1999;

}
