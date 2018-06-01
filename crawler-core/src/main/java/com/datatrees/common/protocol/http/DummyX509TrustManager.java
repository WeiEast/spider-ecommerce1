/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol.http;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class DummyX509TrustManager implements X509TrustManager {
    private X509TrustManager standardTrustManager = null;

    /**
     * Constructor for DummyX509TrustManager.
     */
    public DummyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException,
            KeyStoreException {
        super();
        String algo = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory factory = TrustManagerFactory.getInstance(algo);
        factory.init(keystore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException(algo + " trust manager not supported");
        }
        this.standardTrustManager = (X509TrustManager) trustmanagers[0];
    }

    /**
     * @see X509TrustManager#checkClientTrusted(X509Certificate[], String)
     */
    public boolean isClientTrusted(X509Certificate[] certificates) {
        return true;
    }

    /**
     * @see X509TrustManager#checkServerTrusted(X509Certificate[], String)
     */
    public boolean isServerTrusted(X509Certificate[] certificates) {
        return true;
    }

    /**
     * @see X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }

    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // do nothing

    }

    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // do nothing

    }
}
