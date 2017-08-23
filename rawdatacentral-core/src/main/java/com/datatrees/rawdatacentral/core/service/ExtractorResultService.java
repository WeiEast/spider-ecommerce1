package com.datatrees.rawdatacentral.core.service;

import com.datatrees.rawdatacentral.domain.model.*;

/**
 * Created by wuminlang on 15/7/28.
 */
public interface ExtractorResultService {

    public int insertMailExtractResult(MailExtractResult mailExtractResult);

    public int insertEBankExtractResult(EBankExtractResult result);

    public int insertEcommerceExtractResult(EcommerceExtractResult ecommerceExtractResult);

    public int insertOperatorExtractResult(OperatorExtractResult operatorExtractResult);

    public int insertDefaultExtractResult(DefaultExtractResult defaultExtractResult);

}
