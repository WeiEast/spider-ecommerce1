/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import org.apache.commons.httpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 13, 2014 2:45:34 PM
 */
public class CustomerRetryHandler implements HttpMethodRetryHandler, Configurable {

    private static final Logger        log                     = LoggerFactory.getLogger(CustomerRetryHandler.class);
    /** the number of times a method will be retried */
    private              int           retryCount              = 3;

    private              Configuration conf                    = null;

    private static       Class         SSL_HANDSHAKE_EXCEPTION = null;

    static {
        try {
            SSL_HANDSHAKE_EXCEPTION = Class.forName("javax.net.ssl.SSLHandshakeException");
        } catch (ClassNotFoundException ignore) {}
    }

    /**
     * Creates a new DefaultHttpMethodRetryHandler that retries up to 3 times but does not retry
     * methods that have successfully sent their requests.
     */
    public CustomerRetryHandler() {

    }

    /**
     * Used <code>retryCount</code> and <code>requestSentRetryEnabled</code> to determine if the
     * given method should be retried.
     * 
     * @see HttpMethodRetryHandler#retryMethod(HttpMethod, IOException, int)
     */
    public boolean retryMethod(final HttpMethod method, final IOException exception, int executionCount) {
        if (method == null) {
            throw new IllegalArgumentException("HTTP method may not be null");
        }
        if (exception == null) {
            throw new IllegalArgumentException("Exception parameter may not be null");
        }
        // HttpMethod interface is the WORST thing ever done to HttpClient
        if (method instanceof HttpMethodBase) {
            if (((HttpMethodBase) method).isAborted()) {
                return false;
            }
        }
        if (executionCount > this.retryCount) {
            // Do not retry if over max retry count
            return false;
        }
        HostConfiguration conf = method.getHostConfiguration();
        if (conf != null && conf.getProxyHost() != null) {
            // Do not retry if proxy is set
            return false;
        }
        if (exception instanceof NoHttpResponseException) {
            // Retry if the server dropped connection on us
            return true;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            return false;
        }
        if (exception instanceof NoRouteToHostException) {
            // Host unreachable
            return false;
        }
        if (SSL_HANDSHAKE_EXCEPTION != null && SSL_HANDSHAKE_EXCEPTION.isInstance(exception)) {
            // SSL handshake exception
            return false;
        }
        if (!method.isRequestSent()) {
            // Retry if the request has not been sent fully or
            // if it's OK to retry methods that have been sent
            return true;
        }
        // otherwise do not retry
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.hadoop.conf.Configurable#setConf(org.apache.hadoop.conf.Configuration)
     */
    @Override
    public void setConf(Configuration conf) {

        retryCount = conf.getInt(HTTPConstants.HTTP_MAX_RETRY_COUNT, 3);
        log.debug("set max retryCount from conf " + retryCount);
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
