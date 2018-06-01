/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.protocol;

import java.io.File;
import java.security.Security;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.*;
import com.datatrees.common.protocol.http.HTTPConstants;
import com.datatrees.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 7:57:52 PM
 */
public class WebClientUtil {

    private static Logger                log                = LoggerFactory.getLogger(WebClientUtil.class);
    private static Map<String, Protocol> protocolCache      = new ConcurrentHashMap<String, Protocol>();

    private static String                prefix             = "service";
    private static String                FILE_CLIENT_PREFIX = "file";


    static {
        try {
            File cacerts = null;
            String classPath = ClassLoader.getSystemClassLoader().getResource("").getPath();
            String cacertsPath = PropertiesConfiguration.getInstance().get("custom.net.ssl.trustStore", "cacerts/cacerts");
            if (StringUtils.isNotBlank(cacertsPath) && (cacerts = new File(classPath + cacertsPath)).exists()) {
                log.info("set custom ssl trustStore path:" + cacerts.getAbsolutePath());
                System.setProperty("javax.net.ssl.trustStore", cacerts.getAbsolutePath());
            }
            log.info("set jdk.tls.disabledAlgorithms empty.");
            Security.setProperty("jdk.tls.disabledAlgorithms", "");
            log.info("set jdk.certpath.disabledAlgorithms empty.");
            Security.setProperty("jdk.certpath.disabledAlgorithms", "");
        } catch (Exception e) {
            log.warn("WebClientUtil init  error...");
        }
    }

    private static ConfigurationBase getHTTPConfiguration(final String prefixName) {
        DefaultConfiguration newConfiguration = new DefaultConfiguration();
        ConfigurationBase configWapper = new ConfigurationWapper(newConfiguration, PropertiesConfiguration.getInstance());

        configWapper.set(HTTPConstants.CONNECTION_TIMEOUT,
                configWapper.get(prefixName + "." + HTTPConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS.toMillis(5) + ""));
        configWapper.set(HTTPConstants.SO_TIMEOUT, configWapper.get(prefixName + "." + HTTPConstants.SO_TIMEOUT, TimeUnit.SECONDS.toMillis(15) + ""));
        configWapper.set(HTTPConstants.CONNECTION_MANAGER_TIMEOUT,
                configWapper.get(prefixName + "." + HTTPConstants.CONNECTION_MANAGER_TIMEOUT, TimeUnit.SECONDS.toMillis(300) + ""));
        configWapper.set(HTTPConstants.HTTP_CONTENT_LIMIT,
                configWapper.get(prefixName + "." + HTTPConstants.HTTP_CONTENT_LIMIT, 4 * 1024 * 1024 + ""));// 4M

        configWapper.setInt(HTTPConstants.MAX_HOST_CONNECTIONS, configWapper.getInt(prefixName + "." + HTTPConstants.MAX_HOST_CONNECTIONS, 100));
        configWapper.setInt(HTTPConstants.MAX_TOTAL_CONNECTIONS, configWapper.getInt(prefixName + "." + HTTPConstants.MAX_TOTAL_CONNECTIONS, 100));

        return configWapper;
    }

    private static Protocol getWebClient(Configuration conf) {
        try {
            return ProtocolFactory.get(conf).getProtocol(ProtocolFactory.ProtocolType.HTTP);
        } catch (Exception e) {
            log.error("get client error", e);
        }
        return null;
    }

    public static Protocol getWebClient(String prefixName) {
        if (StringUtils.isBlank(prefixName)) {
            return getWebClient();
        } else {
            Protocol protocol = protocolCache.get(prefixName);
            if (protocol == null) {
                synchronized (protocolCache) {
                    protocol = protocolCache.get(prefixName);
                    if (protocol == null) {
                        Configuration NEW_HTTP_CONF = getHTTPConfiguration(prefixName);
                        protocol = getWebClient(NEW_HTTP_CONF);
                        protocolCache.putIfAbsent(prefixName, protocol);
                    }
                }
            }
            return protocol;
        }
    }

    public static Protocol getWebClient() {
        return getWebClient(PropertiesConfiguration.getInstance());
    }

    public static Protocol getServiceClient() {
        return getWebClient(prefix);
    }

    public static Protocol getFileClient() {
        return getWebClient(FILE_CLIENT_PREFIX);
    }

}
