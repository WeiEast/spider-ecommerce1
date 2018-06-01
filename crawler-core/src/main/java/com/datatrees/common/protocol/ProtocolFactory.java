/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.file.FileClient;
import com.datatrees.common.protocol.ftp.FtpClient;
import com.datatrees.common.protocol.http.WebClient;
import com.datatrees.common.protocol.sftp.SFtpClient;
import com.datatrees.common.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and caches {@link Protocol} plugins. Protocol plugins should define the attribute
 * "protocolName" with the name of the protocol that they implement. Configuration object is used
 * for caching. Cache key is constructed from appending protocol name (eg. http) to constant
 * {@link Protocol#X_POINT_ID}.
 */
public class ProtocolFactory {

    public static final Logger LOG = LoggerFactory.getLogger(ProtocolFactory.class);

    private final Configuration conf;

    static final Cache CACHE = new Cache();

    private ProtocolFactory(Configuration conf) {
        this.conf = conf;
    }


    public static ProtocolFactory get(Configuration conf) {
        return CACHE.get(conf);
    }

    /**
     * Returns the appropriate {@link Protocol} implementation for a url.
     * 
     * @param urlString Url String
     * @return The appropriate {@link Protocol} implementation for a given {@link URL}.
     * @throws ProtocolNotFound when Protocol can not be found for urlString
     */
    public synchronized Protocol getProtocol(String urlString) throws ProtocolNotFound {
        try {
            URL url = new URL(urlString);
            String protocolName = url.getProtocol();
            if (protocolName == null) throw new ProtocolNotFound(urlString);

            String cacheId = Protocol.X_POINT_ID + this.hashCode() + protocolName;
            Protocol protocol = (Protocol) CacheUtil.getInstance().getNoExpiredObject(cacheId);
            if (protocol != null) {
                return protocol;
            }

            protocol = createProtocol(protocolName);
            if (protocol == null) {
                throw new ProtocolNotFound(protocolName);
            }

            CacheUtil.getInstance().insertObject(cacheId, protocol);
            return protocol;
        } catch (MalformedURLException e) {
            throw new ProtocolNotFound(urlString, e.toString());
        }
    }

    public synchronized Protocol getProtocol(ProtocolType type) throws ProtocolNotFound {
        String cacheId = Protocol.X_POINT_ID + this.hashCode() + type.name();
        Protocol protocol = (Protocol) CacheUtil.getInstance().getNoExpiredObject(cacheId);
        if (protocol != null) {
            return protocol;
        }

        protocol = createProtocol(type);
        if (protocol == null) {
            throw new ProtocolNotFound(type.name());
        }

        CacheUtil.getInstance().insertObject(cacheId, protocol);
        return protocol;
    }

    private Protocol createProtocol(String name) {
        Protocol protocol = null;

        if ("http".equalsIgnoreCase(name) || "https".equalsIgnoreCase(name)) {
            protocol = new WebClient();
        } else if ("ftp".equalsIgnoreCase(name)) {
            protocol = new FtpClient();
        } else if ("file".equalsIgnoreCase(name)) {
            protocol = new FileClient();
        } else if ("sftp".equalsIgnoreCase(name)) {
            protocol = new SFtpClient();
        }
        protocol.setConf(conf);
        return protocol;

    }

    private Protocol createProtocol(ProtocolType type) {
        Protocol protocol = null;

        switch (type) {
            case HTTP:
                protocol = new WebClient();
                break;
            case FTP:
                protocol = new FtpClient();
                break;
            case FILE:
                protocol = new FileClient();
                break;
            case SFTP:
                protocol = new SFtpClient();
                break;
            default:
                break;
        }
        protocol.setConf(conf);
        return protocol;

    }

    static class Cache {

       private static  Map<Configuration, ProtocolFactory> cache = new HashMap<Configuration, ProtocolFactory>();
       
        public synchronized ProtocolFactory get(Configuration conf) {
            ProtocolFactory factory = cache.get(conf);
            if (factory == null) {
                factory = new ProtocolFactory(conf);
                cache.put(conf, factory);
            }
            return factory;
        }
    }

    public static enum ProtocolType {
        HTTP, FTP, FILE, SFTP;
    }

}
