/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.pipeline;

import com.treefinance.crawler.framework.context.function.SpiderGenericResponse;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:08:43 PM
 */
public class Response extends SpiderGenericResponse {

    public Response() {
        super();
    }

    public Response(String content) {
        setOutPut(content);
    }

}
