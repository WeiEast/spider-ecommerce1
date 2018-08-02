/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.pipeline;

import com.treefinance.crawler.lang.AtomicAttributes;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:08:43 PM
 */
public class Response extends AtomicAttributes {

    private final static String OUTPUT = "Response.Output";

    public Response() {
        super();
    }

    public Response(String content) {
        setOutPut(content);
    }

    public static Response build() {
        return new Response();
    }

    public static Response build(String content) {
        return new Response(content);
    }

    public Object getOutPut() {
        return getAttribute(OUTPUT);
    }

    public Response setOutPut(Object content) {
        setAttribute(OUTPUT, content);
        return this;
    }
}
