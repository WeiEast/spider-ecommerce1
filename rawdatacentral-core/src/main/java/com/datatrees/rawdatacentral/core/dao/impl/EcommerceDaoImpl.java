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

import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.core.dao.EcommerceDao;
import com.datatrees.rawdatacentral.core.model.Ecommerce;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月31日 下午1:35:48 
 */
@Component
public class EcommerceDaoImpl extends BaseDao implements EcommerceDao{

    /* (non-Javadoc)
     * @see EcommerceDao#getAllEcommerce()
     */
    @Override
    public List<Ecommerce> getAllEcommerce() {
        return sqlMapClientTemplate.queryForList("Ecommerce.getAllEcommerce");
    }

}
