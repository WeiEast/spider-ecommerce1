package com.datatrees.rawdatacentral.core.service.impl;

import com.datatrees.rawdatacentral.dao.*;
import com.datatrees.rawdatacentral.dao.EbankExtractResultDAO;
import com.datatrees.rawdatacentral.core.service.ExtractorResultService;
import com.datatrees.rawdatacentral.domain.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by wuminlang on 15/7/28.
 */
@Service
public class ExtractorResultServiceImpl implements ExtractorResultService {

    @Resource
    private MailExtractResultDAO mailExtractResultDao;

    @Resource
    private EcommerceExtractResultDAO ecommerceExtractResultDao;

    @Resource
    private OperatorExtractResultDAO operatorExtractResultDao;

    @Resource
    private EbankExtractResultDAO eBankExtractResultDao;

    @Resource
    private DefaultExtractResultDAO defaultExtractResultDAO;

    @Override
    public int insertMailExtractResult(MailExtractResult mailExtractResult) {
        return mailExtractResultDao.insert(mailExtractResult);
    }

    @Override
    public int insertEcommerceExtractResult(EcommerceExtractResult ecommerceExtractResult) {
        return ecommerceExtractResultDao.insert(ecommerceExtractResult);
    }

    @Override
    public int insertOperatorExtractResult(OperatorExtractResult operatorExtractResult) {
        return operatorExtractResultDao.insert(operatorExtractResult);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractorResultService#insert(com.datatrees
     * .rawdatacentral.core.model.result.EBankExtractResult)
     */
    @Override
    public int insertEBankExtractResult(EBankExtractResult result) {
        return eBankExtractResultDao.insert(result);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractorResultService#insert(com.
     * datatrees.rawdatacentral.core.model.result.DefaultExtractResult)
     */
    @Override
    public int insertDefaultExtractResult(DefaultExtractResult defaultExtractResult) {
        return defaultExtractResultDAO.insert(defaultExtractResult);
    }

}
