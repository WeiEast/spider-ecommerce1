/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.domain.model.Operator;
import com.datatrees.rawdatacentral.core.service.OperatorService;
import org.springframework.stereotype.Service;

import com.datatrees.common.util.CacheUtil;
import com.datatrees.rawdatacentral.core.dao.OperatorDao;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月31日 下午1:49:19 
 */
@Service
public class OperatorServiceImpl implements OperatorService {
    @Resource
    private OperatorDao operatorDao;

    /* (non-Javadoc)
     * @see OperatorService#getOperatorByWebsiteId(int)
     */
    @Override
    public Operator getOperatorByWebsiteId(int websiteId) {
        Map<Integer, Operator> operatorMap = (Map<Integer, Operator>) CacheUtil.INSTANCE.getObject(Constants.OPERATOR_MAP_KEY);
        if (operatorMap == null) {
            List<Operator> OperatorList = operatorDao.getAllOperator();
            operatorMap = new HashMap<Integer, Operator>();
            for (Operator operator : OperatorList) {
                operatorMap.put(operator.getWebsiteId(), operator);
            }
            CacheUtil.INSTANCE.insertObject(Constants.OPERATOR_MAP_KEY, operatorMap);
        }
        return operatorMap.get(websiteId);
    }

}
