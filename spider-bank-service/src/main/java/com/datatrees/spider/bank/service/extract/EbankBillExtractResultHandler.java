package com.datatrees.spider.bank.service.extract;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.model.data.EBankData;
import com.datatrees.spider.bank.dao.EbankExtractResultDAO;
import com.datatrees.spider.bank.domain.model.EBankExtractResult;
import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.extract.ExtractResultHandler;
import org.springframework.stereotype.Component;

@Component
public class EbankBillExtractResultHandler implements ExtractResultHandler {

    @Resource
    private EbankExtractResultDAO eBankExtractResultDao;

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

    @Override
    public Class<? extends AbstractExtractResult> getSupportResult() {
        return EBankExtractResult.class;
    }

    @Override
    public void save(AbstractExtractResult result) {
        eBankExtractResultDao.insert((EBankExtractResult) result);
    }
}
