/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ControllerThreadSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummySSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    /** Logger object for this class. */
    private static final Logger     LOG        = LoggerFactory.getLogger(DummySSLProtocolSocketFactory.class);

    private              SSLContext sslcontext = null;

    /**
     * Constructor for DummySSLProtocolSocketFactory.
     */
    public DummySSLProtocolSocketFactory() {
        super();
    }

    private static SSLContext createEasySSLContext() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] {new DummyX509TrustManager(null)}, null);
            return context;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);

            throw new HttpClientError(e.toString());
        }
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }

    /**
     * @see org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory#createSocket(String,int,InetAddress,int)
     */
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
            throws IOException, UnknownHostException {

        return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    /**
     * Attempts to get a new socket connection to the given host within the given time limit.
     * <p>
     * To circumvent the limitations of older JREs that do not support connect timeout a controller
     * thread is executed. The controller thread attempts to create a new socket within the given
     * limit of time. If socket constructor does not return until the timeout expires, the
     * controller terminates and throws an {@link ConnectTimeoutException}
     * </p>
     *
     * @param host the host name/IP
     * @param port the port on the host
     * @param localAddress the local host name/IP to bind the socket to
     * @param localPort the port on the local machine
     * @param params {@link HttpConnectionParams Http connection parameters}
     *
     * @return Socket a new socket
     *
     * @throws IOException if an I/O error occurs while creating the socket
     * @throws UnknownHostException if the IP address of the host cannot be determined
     */
    public Socket createSocket(final String host, final int port, final InetAddress localAddress,
            final int localPort, final HttpConnectionParams params) throws IOException,
            UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        if (timeout == 0) {
            return createSocket(host, port, localAddress, localPort);
        } else {
            // To be eventually deprecated when migrated to Java 1.4 or above
            return ControllerThreadSocketFactory.createSocket(this, host, port, localAddress,
                    localPort, timeout);
        }
    }

    /**
     * @see org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory#createSocket(String,int)
     */
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host, port);
    }

    /**
     * @see org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory#createSocket(Socket,String,int,boolean)
     */
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(DummySSLProtocolSocketFactory.class));
    }

    public int hashCode() {
        return DummySSLProtocolSocketFactory.class.hashCode();
    }

}
