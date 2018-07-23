/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

/**
 * Simple aggregate to pass from protocol plugins both content and protocol status.
 *
 * @author Andrzej Bialecki &lt;ab@getopt.org&gt;
 */
public class ProtocolOutput {

    public static ProtocolOutput NULL = new Null(Content.NULL);

    private Content  content;

    private int      statusCode;

    private Response response;

    /**
     * @param content
     * @param statusCode
     * @param response
     */
    public ProtocolOutput(Content content, int statusCode, Response response) {
        super();
        this.content = content;
        this.statusCode = statusCode;
        this.response = response;
    }

    public ProtocolOutput(Content content, int statusCode) {
        this.content = content;
        this.statusCode = statusCode;
    }

    public ProtocolOutput(Content content) {
        this.content = content;
        this.statusCode = ProtocolStatusCodes.SUCCESS;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return this.statusCode == ProtocolStatusCodes.SUCCESS;
    }

    public boolean needRetry() {
        return this.statusCode == ProtocolStatusCodes.EXCEPTION || this.statusCode == ProtocolStatusCodes.SERVER_EXCEPTION;
    }

    public boolean isRedirector() {
        return this.statusCode == ProtocolStatusCodes.MOVED || this.statusCode == ProtocolStatusCodes.TEMP_MOVED;
    }

    /**
     * @return the response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * null instance mean dummy
     */
    private static class Null extends ProtocolOutput {

        public Null(Content content) {
            super(content);
        }
    }

}
