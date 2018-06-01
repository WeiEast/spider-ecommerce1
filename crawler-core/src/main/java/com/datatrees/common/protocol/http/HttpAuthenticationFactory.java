/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol.http;

// JDK imports

import java.util.*;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the Http protocol implementation with the ability to authenticate when prompted. The
 * goal is to provide multiple authentication types but for now just the
 * {@link HttpBasicAuthentication} authentication type is provided.
 * 
 * @see HttpBasicAuthentication
 * @see Http
 * @see HttpResponse
 * 
 * @author Matt Tencati
 */
public class HttpAuthenticationFactory implements Configurable {

    /**
     * The HTTP Authentication (WWW-Authenticate) header which is returned by a webserver requiring
     * authentication.
     */
    public static final  String        WWW_AUTHENTICATE = "WWW-Authenticate";

    private static final Logger        LOG              = LoggerFactory.getLogger(HttpAuthenticationFactory.class);

    private static       Map<?, ?>     auths            = new TreeMap<Object, Object>();

    private              Configuration conf             = null;


    public HttpAuthenticationFactory(Configuration conf) {
        setConf(conf);
    }


    /*
     * ---------------------------------- * <implementation:Configurable> *
     * ----------------------------------
     */

    public void setConf(Configuration conf) {
        this.conf = conf;
        // if (conf.getBoolean("http.auth.verbose", false)) {
        // LOG.setLevel(Level.FINE);
        // } else {
        // LOG.setLevel(Level.WARNING);
        // }
    }

    public Configuration getConf() {
        return conf;
    }

    /*
     * ---------------------------------- * <implementation:Configurable> *
     * ----------------------------------
     */


    @SuppressWarnings("unchecked")
    public HttpAuthentication findAuthentication(Metadata header) {

        if (header == null) return null;

        try {
            Collection challenge = null;
            if (header instanceof Metadata) {
                Object o = header.get(WWW_AUTHENTICATE);
                if (o instanceof Collection) {
                    challenge = (Collection<?>) o;
                } else {
                    challenge = new ArrayList<String>();
                    challenge.add(o.toString());
                }
            } else {
                String challengeString = header.get(WWW_AUTHENTICATE);
                if (challengeString != null) {
                    challenge = new ArrayList<Object>();
                    challenge.add(challengeString);
                }
            }
            if (challenge == null) {
                LOG.trace("Authentication challenge is null");
                return null;
            }

            Iterator<?> i = challenge.iterator();
            HttpAuthentication auth = null;
            while (i.hasNext() && auth == null) {
                String challengeString = (String) i.next();
                if (challengeString.equals("NTLM")) {
                    challengeString = "Basic realm=techweb";
                }

                LOG.trace("Checking challengeString=" + challengeString);
                auth = HttpBasicAuthentication.getAuthentication(challengeString, conf);
                if (auth != null) return auth;

                // TODO Add additional Authentication lookups here
            }
        } catch (Exception e) {
            LOG.error("Failed with following exception: ", e);
        }
        return null;
    }
}
