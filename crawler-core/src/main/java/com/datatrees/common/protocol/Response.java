/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

// JDK imports

import java.net.URL;

import com.datatrees.common.protocol.metadata.HttpHeaders;
import com.datatrees.common.protocol.metadata.Metadata;

/**
 * A response inteface. Makes all protocols model HTTP.
 */
public interface Response extends HttpHeaders {

    /** Returns the URL used to retrieve this response. */
    public URL getUrl();

    /** Returns the response code. */
    public int getCode();

    /** Returns the value of a named header. */
    public String getHeader(String name);

    /** Returns all the headers. */
    public Metadata getHeaders();

    /** Returns the full content of the response. */
    public byte[] getContent();

}
