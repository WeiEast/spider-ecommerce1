/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.pipeline;

import com.treefinance.crawler.lang.AtomicAttributes;
import com.treefinance.crawler.lang.Copyable;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:08:31 PM
 */
public class Request extends AtomicAttributes implements Copyable<Request> {

    private static String INPUT = "Request.input";

    public Request() {
        super();
    }

    public Request(String content) {
        setInput(content);
    }

    public static Request clone(Request request) {
        return request.copy();
    }

    public Object getInput() {
        return getAttribute(INPUT);
    }

    public Request setInput(Object content) {
        setAttribute(INPUT, content);
        return this;
    }

    @Override
    public Request copy() {
        Request req = new Request();
        req.addAttributes(this.getAttributes());
        return req;
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
