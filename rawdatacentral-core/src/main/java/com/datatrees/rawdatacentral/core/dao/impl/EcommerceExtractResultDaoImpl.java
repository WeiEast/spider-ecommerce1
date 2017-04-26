package com.datatrees.rawdatacentral.core.dao.impl;

import com.datatrees.rawdatacentral.core.dao.EcommerceExtractResultDao;
import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.domain.model.EcommerceExtractResult;

/**
 * Created by wuminlang on 15/7/29.
 */
@Component
public class EcommerceExtractResultDaoImpl extends BaseDao implements EcommerceExtractResultDao {
    @Override
    public int insertEcommerceExtractResult(EcommerceExtractResult result) {
        return (int) sqlMapClientTemplate.insert("EcommerceExtractResult.insert", result);
    }
}
