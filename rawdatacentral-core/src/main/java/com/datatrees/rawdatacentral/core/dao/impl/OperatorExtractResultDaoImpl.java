package com.datatrees.rawdatacentral.core.dao.impl;

import com.datatrees.rawdatacentral.domain.model.OperatorExtractResult;
import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.core.dao.OperatorExtractResultDao;

/**
 * Created by wuminlang on 15/7/29.
 */
@Component
public class OperatorExtractResultDaoImpl extends BaseDao implements OperatorExtractResultDao {
    @Override
    public int insertOperatorExtractResult(OperatorExtractResult result) {
        return (int) sqlMapClientTemplate.insert("OperatorExtractResult.insert", result);
    }
}
