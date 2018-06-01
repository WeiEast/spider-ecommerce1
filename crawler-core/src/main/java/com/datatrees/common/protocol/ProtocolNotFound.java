/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

@SuppressWarnings("serial")
public class ProtocolNotFound extends ProtocolException {
    private String url;

    public ProtocolNotFound(String url) {
        this(url, "protocol not found for url=" + url);
    }

    public ProtocolNotFound(String url, String message) {
        super(message);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
