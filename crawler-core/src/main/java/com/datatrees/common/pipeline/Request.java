/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.pipeline;

import com.treefinance.crawler.framework.context.function.SpiderGenericRequest;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:08:31 PM
 */
public class Request extends SpiderGenericRequest {

    public Request() {
        super();
    }

    public Request(String content) {
        setInput(content);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Request [Input()=" + getInput() + "]";
    }

}
