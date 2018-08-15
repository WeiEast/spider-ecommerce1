package com.treefinance.crawler.framework.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * url's protocol registry.
 * @author Jerry
 * @since 10:36 2018/7/19
 */
public final class UrlProtocolRegistry {

    public static final  String             PROTOCOL_SEPARATOR = "://";
    private static final String             DEFAULT_PROTOCOLS  = "http,https,rtmp,qvod,pa,mms,rtsp,thunder,bdhb";
    private static       Collection<String> PROTOCOLS          = build(DEFAULT_PROTOCOLS.split(","));

    private UrlProtocolRegistry() {
    }

    private static ConcurrentLinkedQueue<String> build(String... protocols) {
        return Arrays.stream(protocols).map(StringUtils::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
    }

    /**
     * register new protocols
     * @param protocols the new protocol array to add
     */
    public static void register(String... protocols) {
        if (ArrayUtils.isNotEmpty(protocols)) {
            Arrays.stream(protocols).map(StringUtils::trim).filter(protocol -> StringUtils.isNotEmpty(protocol) && !PROTOCOLS.contains(protocol)).forEach(PROTOCOLS::add);
        }
    }

    /**
     * clear old protocols and reset to new protocols
     * @param protocols the new protocol array to add
     */
    public static void reset(String... protocols) {
        if (ArrayUtils.isEmpty(protocols)) {
            throw new IllegalArgumentException("The protocols must not be empty.");
        }

        PROTOCOLS = build(protocols);
    }

    /**
     * @return unmodifiable protocol collection.
     */
    public static Collection<String> getProtocols() {
        return Collections.unmodifiableCollection(PROTOCOLS);
    }
}
