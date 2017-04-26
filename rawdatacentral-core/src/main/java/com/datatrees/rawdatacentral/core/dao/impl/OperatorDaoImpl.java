/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.dao.impl;

import java.util.List;

import com.datatrees.rawdatacentral.domain.model.Operator;
import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.core.dao.OperatorDao;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月31日 下午1:38:00 
 */
@Component
public class OperatorDaoImpl extends BaseDao implements OperatorDao{

    /* (non-Javadoc)
     * @see OperatorDao#getAllOperator()
     */
    @Override
    public List<Operator> getAllOperator() {
        return sqlMapClientTemplate.queryForList("Operator.getAllOperator");
    }

}
