/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.core.common;


import com.datatrees.rawdatacentral.core.service.PropertiesService;


/**
 *
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2017年2月28日 下午4:43:54
 */
public enum DBConfiguration {
    INSTANCE;

    private PropertiesService propertiesService;


    public PropertiesService getPropertiesService() {
        return propertiesService;
    }


    public void setPropertiesService(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    // superdiamond字段长度有限制，当value的字段超出限制，应将配置放在数据库中
    public String getString(String key) {
        return propertiesService.getValueByKeyword(key);
    }
}
