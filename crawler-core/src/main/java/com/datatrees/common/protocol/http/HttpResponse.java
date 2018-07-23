/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol.http;

// JDK imports

import java.net.URL;

import com.datatrees.common.protocol.Response;
import com.datatrees.common.protocol.metadata.Metadata;
import com.datatrees.common.protocol.metadata.SpellCheckedMetadata;
import org.apache.commons.httpclient.HttpState;

/**
 * An HTTP response.
 *
 * @author Susam Pal
 */
public class HttpResponse implements Response {

    private URL       url;

    private byte[]    content;

    private int       code;

    private Metadata  headers = new SpellCheckedMetadata();

    private String    redirectUrl;

    private HttpState state;

    public HttpResponse() {
        super();
    }

    public URL getUrl() {
        return url;
    }

    public HttpResponse setUrl(URL url) {
        this.url = url;
        return this;
    }

    public int getCode() {
        return code;
    }

    public HttpResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Metadata getHeaders() {
        return headers;
    }

    public byte[] getContent() {
        return content;
    }

    public HttpResponse setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public HttpResponse setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    /**
     * @return the state
     */
    public HttpState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(HttpState state) {
        this.state = state;
    }

}
