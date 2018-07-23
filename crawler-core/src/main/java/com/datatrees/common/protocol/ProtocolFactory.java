/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.http.WebClient;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Creates and caches {@link Protocol} plugins. Protocol plugins should define the attribute
 * "protocolName" with the name of the protocol that they implement. Configuration object is used
 * for caching. Cache key is constructed from appending protocol name (eg. http) to constant
 */
public class ProtocolFactory {

    private static final Cache<Key, Protocol> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).softValues().build();

    public static Protocol getProtocol(ProtocolType type, Configuration conf) {

        Key key = new Key(conf, type.name());

        try {
            return cache.get(key, () -> createProtocol(type, conf));
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException("获取protocol失败！", e);
        }
    }

    private static Protocol createProtocol(ProtocolType type, Configuration conf) {
        if (ProtocolType.HTTP.equals(type)) {
            Protocol protocol = new WebClient();
            protocol.setConf(conf);
            return protocol;
        }
        throw new UnsupportedOperationException("Unsupported protocol type: " + type);
    }

    public enum ProtocolType {
        HTTP,
        FTP,
        FILE,
        SFTP;
    }

    private static class Key {

        private final Configuration conf;

        private final String        type;

        public Key(Configuration conf, String type) {
            this.conf = conf;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (conf != null ? !conf.equals(key.conf) : key.conf != null) return false;
            return type.equals(key.type);
        }

        @Override
        public int hashCode() {
            int result = conf != null ? conf.hashCode() : 0;
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
