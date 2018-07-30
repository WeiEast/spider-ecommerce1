package com.datatrees.rawdatacentral.extractor.builder;

import com.datatrees.rawdatacentral.core.model.data.EBankData;
import com.datatrees.rawdatacentral.domain.model.EBankExtractResult;
import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.extract.ExtractResultHandler;
import org.springframework.stereotype.Component;

@Component
public class EbankBillExtractResultHandler implements ExtractResultHandler {

    @Override
    public ResultType getSupportResultType() {
        return ResultType.EBANKBILL;
    }

    @Override
    public AbstractExtractResult build(ExtractMessage extractMessage) {
        Object object = extractMessage.getMessageObject();
        EBankExtractResult result = new EBankExtractResult();
        result.setBankId(extractMessage.getTypeId());
        result.setUniqueSign(((EBankData) object).getUniqueSign());
        result.setUrl(((EBankData) object).getUrl());
        result.setExtraInfo(((EBankData) object).getExtraInfo());
        return result;
    }
}
