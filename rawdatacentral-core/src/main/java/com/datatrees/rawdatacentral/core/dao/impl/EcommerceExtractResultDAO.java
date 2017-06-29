package com.datatrees.rawdatacentral.core.dao.impl;

import com.datatrees.rawdatacentral.dao.EcommerceExtractResultDAO;
import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.domain.model.EcommerceExtractResult;

/**
 * Created by wuminlang on 15/7/29.
 */
@Component
public class EcommerceExtractResultDAO extends BaseDao implements EcommerceExtractResultDAO {
    @Override
    public int insertEcommerceExtractResult(EcommerceExtractResult result) {
        return (int) sqlMapClientTemplate.insert("EcommerceExtractResult.insert", result);
    }
}
