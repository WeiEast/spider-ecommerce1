/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.core.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.core.service.PropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.common.util.CacheUtil;
import com.datatrees.rawdatacentral.core.common.DBConfiguration;
import com.datatrees.rawdatacentral.core.dao.PropertiesDao;

/**
 *
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2017年2月28日 下午5:31:04
 */
@Service
public class PropertiesServiceImpl implements PropertiesService {
    @Resource
    private PropertiesDao propertiesDao;
    private static final Logger logger = LoggerFactory.getLogger(PropertiesServiceImpl.class);

    @Override
    public String getValueByKeyword(String keyword) {

        String value = (String) CacheUtil.getInstance().getObject(Constants.PROPERTIES_MAP_KEY + keyword);
        if (value == null) {
            value = propertiesDao.getValueByKeyword(keyword);
            logger.info("get value:" + value + "by key:" + keyword);
            CacheUtil.getInstance().insertObject(Constants.PROPERTIES_MAP_KEY + keyword, value);
        }
        return value;
    }

    @PostConstruct
    public void init() {
        DBConfiguration.INSTANCE.setPropertiesService(this);
    }
}
