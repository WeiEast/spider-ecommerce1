/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package org.apache.commons.httpclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jun 15, 2014 5:18:08 PM
 */
public class ProtocalHttpClient extends HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    public ProtocalHttpClient(HttpConnectionManager httpConnectionManager) {
        super(httpConnectionManager);
    }

    public int executeMethod(HostConfiguration hostconfig, final HttpMethod method, final HttpState state) throws IOException, HttpException {

        LOG.trace("enter HttpClient.executeMethod(HostConfiguration,HttpMethod,HttpState)");

        if (method == null) {
            throw new IllegalArgumentException("HttpMethod parameter may not be null");
        }
        HostConfiguration defaulthostconfig = getHostConfiguration();
        if (hostconfig == null) {
            hostconfig = defaulthostconfig;
        }
        URI uri = method.getURI();
        if (hostconfig == defaulthostconfig || uri.isAbsoluteURI()) {
            // make a deep copy of the host defaults
            hostconfig = new HostConfiguration(hostconfig);
            if (uri.isAbsoluteURI()) {
                hostconfig.setHost(uri);
            }
        }

        HttpMethodDirector methodDirector = new ProtocalHttpMethodDirector(getHttpConnectionManager(), hostconfig, getParams(), (state == null ? getState() : state));
        methodDirector.executeMethod(method);
        return method.getStatusCode();
    }
}
