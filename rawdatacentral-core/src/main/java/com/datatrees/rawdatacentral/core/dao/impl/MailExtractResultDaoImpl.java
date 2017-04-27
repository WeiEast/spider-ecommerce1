package com.datatrees.rawdatacentral.core.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datatrees.rawdatacentral.core.dao.MailExtractResultDao;
import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.domain.model.MailExtractResult;

/**
 * Created by wuminlang on 15/7/29.
 */
@Component
public class MailExtractResultDaoImpl extends BaseDao implements MailExtractResultDao {

    @Override
    public int insertMailExtractResult(MailExtractResult result) {
        return (int) sqlMapClientTemplate.insert("MailExtractResult.insert", result);
    }

}
