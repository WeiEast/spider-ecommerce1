/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral;


import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年6月3日 下午8:38:15
 */
public enum DubboService {
    INSTANCE;
    private String dubboAddress = "zookeeper://192.168.5.241:2181";
    private ApplicationConfig application = null;
    private RegistryConfig registry = null;
    private Map<String, ReferenceConfig<?>> map = new HashMap<String, ReferenceConfig<?>>();


    {
        application = new ApplicationConfig();
        application.setName("rawdata_consumer");
        registry = new RegistryConfig();
        registry.setAddress(dubboAddress);
    }

    private <T> ReferenceConfig<T> getReference(Class<T> classOfT, String url) {
        ReferenceConfig<T> reference = (ReferenceConfig<T>) map.get(classOfT);
        if (reference == null) {
            synchronized (map) {
                reference = (ReferenceConfig<T>) map.get(classOfT);
                if (reference == null) {
                    reference = new ReferenceConfig<T>();
                    if (url != null) {
                        reference.setCheck(false);
                        reference.setUrl(url);
                    }
                    reference.setApplication(application);
                    reference.setRegistry(registry);
                    reference.setInterface(classOfT);
                    reference.setTimeout(10000);
                    reference.setRetries(3);
                    // reference.setVersion(version);
                    map.put(getReferenceKey(reference), reference);
                }
            }
        }
        return reference;
    }

    private <T> String getReferenceKey(ReferenceConfig<T> reference) {
        return reference.getInterface() + "_G" + reference.getGroup() + "_V" + reference.getVersion();
    }

    public <T> T getService(Class<T> classOfT, String url) {
        ReferenceConfig<T> reference = initReferenceConfig(classOfT);
        if (url != null) {
            reference.setCheck(false);
            reference.setUrl(url);
        }
        return getService(reference);
    }

    public <T> T getServiceByGroup(Class<T> classOfT, String group) {
        ReferenceConfig<T> reference = initReferenceConfig(classOfT);
        reference.setGroup(group);
        return getService(reference);
    }

    public <T> T getService(Class<T> classOfT) {
        ReferenceConfig<T> reference = initReferenceConfig(classOfT);
        return getService(reference);
    }

    public <T> ReferenceConfig<T> initReferenceConfig(Class<T> classOfT) {
        ReferenceConfig<T> reference = new ReferenceConfig<T>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setTimeout(10000);
        reference.setInterface(classOfT);
        return reference;
    }


    public <T> T getService(ReferenceConfig<T> config) {
        ReferenceConfig<T> reference = getReferenceConfig(config);
        if (reference != null) {
            return reference.get();
        } else {
            return null;
        }
    }

    public <T> ReferenceConfig<T> getReferenceConfig(ReferenceConfig<T> config) {
        ReferenceConfig<T> reference = (ReferenceConfig<T>) map.get(getReferenceKey(config));
        if (reference == null) {
            synchronized (map) {
                reference = (ReferenceConfig<T>) map.get(getReferenceKey(config));
                if (reference == null) {
                    reference = config;
                    map.put(getReferenceKey(config), config);
                }
            }
        }
        return reference;
    }
}
