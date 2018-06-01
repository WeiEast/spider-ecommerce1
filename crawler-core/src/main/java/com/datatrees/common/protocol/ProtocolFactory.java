/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.http.WebClient;
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

    public synchronized Protocol getProtocol(ProtocolType type) {
        String cacheId = Protocol.X_POINT_ID + this.hashCode() + type.name();
        Protocol protocol = (Protocol) CacheUtil.getInstance().getNoExpiredObject(cacheId);
        if (protocol == null) {
            protocol = createProtocol(type);
            CacheUtil.getInstance().insertObject(cacheId, protocol);
        }

        return protocol;
    }

    private Protocol createProtocol(ProtocolType type) {
        if (ProtocolType.HTTP.equals(type)) {
            Protocol protocol = new WebClient();
            protocol.setConf(conf);
            return protocol;
        }
        throw new UnsupportedOperationException("Unsupported protocol type: " + type);
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

    public enum ProtocolType {
        HTTP, FTP, FILE, SFTP;
    }

}
