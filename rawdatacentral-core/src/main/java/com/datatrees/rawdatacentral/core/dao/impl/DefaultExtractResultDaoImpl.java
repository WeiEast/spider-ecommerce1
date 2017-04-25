package com.datatrees.rawdatacentral.core.dao.impl;

import com.datatrees.rawdatacentral.core.model.result.DefaultExtractResult;
import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.core.dao.DefaultExtractResultDao;

/**
 * Created by wuminlang on 15/7/29.
 */
@Component
public class DefaultExtractResultDaoImpl extends BaseDao implements DefaultExtractResultDao {
    @Override
    public int insertDefaultExtractResult(DefaultExtractResult result) {
        return (int) sqlMapClientTemplate.insert("DefaultExtractResult.insert", result);
    }
}
