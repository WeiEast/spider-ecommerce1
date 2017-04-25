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
import com.datatrees.rawdatacentral.core.model.result.EBankExtractResult;
import com.datatrees.rawdatacentral.core.service.ExtractorResultService;
import org.springframework.stereotype.Service;

import com.datatrees.rawdatacentral.core.dao.DefaultExtractResultDao;
import com.datatrees.rawdatacentral.core.dao.EcommerceExtractResultDao;
import com.datatrees.rawdatacentral.core.dao.MailExtractResultDao;
import com.datatrees.rawdatacentral.core.dao.OperatorExtractResultDao;
import com.datatrees.rawdatacentral.core.model.result.DefaultExtractResult;
import com.datatrees.rawdatacentral.core.model.result.EcommerceExtractResult;
import com.datatrees.rawdatacentral.core.model.result.MailExtractResult;
import com.datatrees.rawdatacentral.core.model.result.OperatorExtractResult;

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
     * ExtractorResultService#getUserSuccessParsedMailKeySet(int)
     */
    @Override
    public Set<String> getUserSuccessParsedMailKeySet(int userId) {
        Set<String> set = new HashSet<String>();
        set.addAll(mailExtractResultDao.getUserSuccessParsedMailKeySet(userId));
        return set;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractorResultService#insertEBankExtractResult(com.datatrees
     * .rawdata.core.model.result.EBankExtractResult)
     */
    @Override
    public int insertEBankExtractResult(EBankExtractResult result) {
        return eBankExtractResultDao.insertEBankExtractResult(result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractorResultService#getUserSuccessParsedEBankBillSet
     * (int)
     */
    @Override
    public Set<String> getUserSuccessParsedEBankBillSet(int userId) {
        Set<String> set = new HashSet<String>();
        set.addAll(eBankExtractResultDao.getUserSuccessParsedEBankBillSet(userId));
        return set;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractorResultService#insertDefaultExtractResult(com.
     * datatrees.rawdata.core.model.result.DefaultExtractResult)
     */
    @Override
    public int insertDefaultExtractResult(DefaultExtractResult defaultExtractResult) {
        return defaultExtractResultDao.insertDefaultExtractResult(defaultExtractResult);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ExtractorResultService#getReissueDetectMails(int,
     * int, java.util.List)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<Map> getReissueDetectMails(int userId, int taskid, List<MailBill> lists) {
        Map<String, Map> mailBillMaps = new HashMap<String, Map>();
        for (MailBill mailBill : lists) {
            Map mailNode = mailBillMaps.get(mailBill.getMailId());
            if (mailNode != null) {
                mailNode.put("billIds", mailNode.get("billIds") + "," + mailBill.getBillId());
            } else {
                mailNode = new HashMap();
                mailNode.put("billIds", mailBill.getBillId());
                mailNode.put("mailId", mailBill.getMailId());
                mailBillMaps.put(mailBill.getMailId(), mailNode);
            }
        }
        List<Map> mailMapList = mailExtractResultDao.getReissueDetectMails(userId, taskid, mailBillMaps.keySet());
        for (Map mailMap : mailMapList) {
            mailBillMaps.get(mailMap.get("mailId")).putAll(mailMap);
        }
        return new ArrayList<Map>(mailBillMaps.values());
    }
}
