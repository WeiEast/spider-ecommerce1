/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.pipeline;


/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:08:31 PM
 */
public class Request extends ContextBase {

    private static String INPUT = "Request.input";

    public Request() {
        super();
    }

    public Request(String content) {
        setInput(content);
    }

    public Request setInput(Object content) {
        setAttribute(INPUT, content);
        return this;
    }

    public Object getInput() {
        return getAttribute(INPUT);
    }

    public static Request clone(Request request) {
        Request newRepuest = new Request();
        newRepuest.context.putAll(request.context);
        return newRepuest;
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
