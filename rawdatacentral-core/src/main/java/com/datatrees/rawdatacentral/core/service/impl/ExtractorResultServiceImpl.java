package com.datatrees.rawdatacentral.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.dao.EBankExtractResultDao;
import com.datatrees.rawdatacentral.core.model.MailBill;
import com.datatrees.rawdatacentral.domain.model.EBankExtractResult;
import com.datatrees.rawdatacentral.core.service.ExtractorResultService;
import org.springframework.stereotype.Service;

import com.datatrees.rawdatacentral.core.dao.DefaultExtractResultDao;
import com.datatrees.rawdatacentral.core.dao.EcommerceExtractResultDao;
import com.datatrees.rawdatacentral.core.dao.MailExtractResultDao;
import com.datatrees.rawdatacentral.core.dao.OperatorExtractResultDao;
import com.datatrees.rawdatacentral.domain.model.DefaultExtractResult;
import com.datatrees.rawdatacentral.domain.model.EcommerceExtractResult;
import com.datatrees.rawdatacentral.domain.model.MailExtractResult;
import com.datatrees.rawdatacentral.domain.model.OperatorExtractResult;

/**
 * Created by wuminlang on 15/7/28.
 */
@Service
public class ExtractorResultServiceImpl implements ExtractorResultService {

    @Resource
    private MailExtractResultDao mailExtractResultDao;

    @Resource
    private EcommerceExtractResultDao ecommerceExtractResultDao;

    @Resource
    private OperatorExtractResultDao operatorExtractResultDao;

    @Resource
    private EBankExtractResultDao eBankExtractResultDao;

    @Resource
    private DefaultExtractResultDao defaultExtractResultDao;

    @Override
    public int insertMailExtractResult(MailExtractResult mailExtractResult) {
        return mailExtractResultDao.insertMailExtractResult(mailExtractResult);
    }

    @Override
    public int insertEcommerceExtractResult(EcommerceExtractResult ecommerceExtractResult) {
        return ecommerceExtractResultDao.insertEcommerceExtractResult(ecommerceExtractResult);
    }

    @Override
    public int insertOperatorExtractResult(OperatorExtractResult operatorExtractResult) {
        return operatorExtractResultDao.insertOperatorExtractResult(operatorExtractResult);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractorResultService#insertEBankExtractResult(com.datatrees
     * .rawdatacentral.core.model.result.EBankExtractResult)
     */
    @Override
    public int insertEBankExtractResult(EBankExtractResult result) {
        return eBankExtractResultDao.insertEBankExtractResult(result);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractorResultService#insertDefaultExtractResult(com.
     * datatrees.rawdatacentral.core.model.result.DefaultExtractResult)
     */
    @Override
    public int insertDefaultExtractResult(DefaultExtractResult defaultExtractResult) {
        return defaultExtractResultDao.insertDefaultExtractResult(defaultExtractResult);
    }

}
