package com.datatrees.crawler.core.domain.config.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 7:18:24 PM
 */
public enum ServiceType {
    Grab_Service("grab"),
    Task_Http_Service("task_http"),
    Plugin_Service("plugin");
    private static Map<String, ServiceType> serviceMap = new HashMap<String, ServiceType>();

    static {
        for (ServiceType service : values()) {
            serviceMap.put(service.getValue(), service);
        }
    }

    private final String value;

    ServiceType(String value) {
        this.value = value;
    }

    public static ServiceType getServiceType(String value) {
        return serviceMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
