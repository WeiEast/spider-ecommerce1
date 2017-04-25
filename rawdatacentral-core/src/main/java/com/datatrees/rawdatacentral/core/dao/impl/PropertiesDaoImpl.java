/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.core.dao.impl;

import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.core.dao.PropertiesDao;

/**
 *
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2017年2月28日 下午5:29:02
 */
@Component
public class PropertiesDaoImpl extends BaseDao implements PropertiesDao {

    @Override
    public String getValueByKeyword(String keyword) {
        return (String)sqlMapClientTemplate.queryForObject("Properties.getValueByKeyword", keyword);
    }

}
