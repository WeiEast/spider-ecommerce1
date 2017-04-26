/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.dao.impl;

import java.util.List;

import com.datatrees.rawdatacentral.core.dao.EBankExtractResultDao;
import com.datatrees.rawdatacentral.domain.model.EBankExtractResult;
import org.springframework.stereotype.Component;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月2日 下午5:54:48
 */
@Component
public class EBankExtractResultDaoImpl extends BaseDao implements EBankExtractResultDao {

    /*
     * (non-Javadoc)
     * 
     * @see
     * EBankExtractResultDao#insertEBankExtractResult(com.datatrees
     * .rawdatacentral.core.model.result.MailExtractResult)
     */
    @Override
    public int insertEBankExtractResult(EBankExtractResult result) {
        return (int) sqlMapClientTemplate.insert("EBankExtractResult.insert", result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * EBankExtractResultDao#getUserSuccessParsedEBankBillSet(int)
     */
    @Override
    public List<String> getUserSuccessParsedEBankBillSet(int userId) {
        return sqlMapClientTemplate.queryForList("EBankExtractResult.getUserSuccessParsedBillKeySet", userId);
    }

}
