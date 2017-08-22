/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.crawler.dubbo;


import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.datatrees.common.conf.PropertiesConfiguration;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年6月3日 下午8:38:15
 */
public enum DubboService {
    INSTANCE;
    private String dubboAddress = PropertiesConfiguration.getInstance().get("dubbo.zookeeper.address", "zookeeper://192.168.5.243:2181");
    private ApplicationConfig application = null;
    private RegistryConfig registry = null;
    private Map<Class<?>, ReferenceConfig<?>> map = new HashMap<Class<?>, ReferenceConfig<?>>();


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
                    // reference.setVersion(version);
                    map.put(classOfT, reference);
                }
            }
        }
        return reference;
    }


    public <T> T getService(Class<T> classOfT, String url) {
        ReferenceConfig<T> reference = getReference(classOfT, url);
        if (reference != null) {
            return reference.get();
        } else {
            return null;
        }
    }

    public <T> T getService(Class<T> classOfT) {
        return getService(classOfT, null);
    }
}
