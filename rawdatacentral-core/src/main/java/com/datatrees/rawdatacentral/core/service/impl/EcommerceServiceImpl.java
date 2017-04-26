/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.core.dao.EcommerceDao;
import org.springframework.stereotype.Service;

import com.datatrees.common.util.CacheUtil;
import com.datatrees.rawdatacentral.domain.model.Ecommerce;
import com.datatrees.rawdatacentral.core.service.EcommerceService;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:09:10
 */
@Service
public class EcommerceServiceImpl implements EcommerceService {
    @Resource
    private EcommerceDao ecommerceDao;

    /*
     * (non-Javadoc)
     * 
     * @see EcommerceService#getEcommerceByWebsiteId(int)
     */
    @Override
    public Ecommerce getEcommerceByWebsiteId(int websiteId) {
        Map<Integer, Ecommerce> ecommerceMap = (Map<Integer, Ecommerce>) CacheUtil.INSTANCE.getObject(Constants.ECOMMERCE_MAP_KEY);
        if (ecommerceMap == null) {
            List<Ecommerce> ecommerceList = ecommerceDao.getAllEcommerce();
            ecommerceMap = new HashMap<Integer, Ecommerce>();
            for (Ecommerce ecommerce : ecommerceList) {
                ecommerceMap.put(ecommerce.getWebsiteId(), ecommerce);
            }
            CacheUtil.INSTANCE.insertObject(Constants.ECOMMERCE_MAP_KEY, ecommerceMap);
        }
        return ecommerceMap.get(websiteId);
    }

}
