package com.datatrees.rawdatacentral.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datatrees.rawdatacentral.core.model.MailBill;
import com.datatrees.rawdatacentral.core.model.result.DefaultExtractResult;
import com.datatrees.rawdatacentral.core.model.result.EBankExtractResult;
import com.datatrees.rawdatacentral.core.model.result.EcommerceExtractResult;
import com.datatrees.rawdatacentral.core.model.result.MailExtractResult;
import com.datatrees.rawdatacentral.core.model.result.OperatorExtractResult;

/**
 * Created by wuminlang on 15/7/28.
 */
public interface ExtractorResultService {
    public int insertMailExtractResult(MailExtractResult mailExtractResult);

    public int insertEBankExtractResult(EBankExtractResult result);

    public int insertEcommerceExtractResult(EcommerceExtractResult ecommerceExtractResult);

    public int insertOperatorExtractResult(OperatorExtractResult operatorExtractResult);

    public int insertDefaultExtractResult(DefaultExtractResult defaultExtractResult);

    Set<String> getUserSuccessParsedMailKeySet(int userId);

    Set<String> getUserSuccessParsedEBankBillSet(int userId);

    List<Map> getReissueDetectMails(int userId, int taskid, List<MailBill> lists);

}
