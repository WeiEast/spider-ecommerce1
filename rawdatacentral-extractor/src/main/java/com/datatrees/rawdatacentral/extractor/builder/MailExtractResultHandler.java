package com.datatrees.rawdatacentral.extractor.builder;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.model.data.MailBillData;
import com.datatrees.rawdatacentral.dao.MailExtractResultDAO;
import com.datatrees.rawdatacentral.domain.model.MailExtractResult;
import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.extract.ExtractResultHandler;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

@Component
public class MailExtractResultHandler implements ExtractResultHandler {

    @Resource
    private MailExtractResultDAO mailExtractResultDao;

    @Override
    public ResultType getSupportResultType() {
        return ResultType.MAILBILL;
    }

    @Override
    public AbstractExtractResult build(ExtractMessage extractMessage) {
        Object object = extractMessage.getMessageObject();
        MailExtractResult result = new MailExtractResult();
        result.setBankId(extractMessage.getTypeId());
        result.setReceiveAt(((MailBillData) object).getReceiveAt());
        result.setSubject(((MailBillData) object).getSubject());
        result.setSender(((MailBillData) object).getSender());
        result.setReceiver(((MailBillData) object).getReceiver());
        result.setUniqueSign(((MailBillData) object).getUniqueSign());
        result.setUrl(((MailBillData) object).getUrl());
        result.setFirstHand(BooleanUtils.isTrue(((MailBillData) object).getFirstHand()));
        result.setExtraInfo(((MailBillData) object).getExtraInfo());
        result.setMailHeader(((MailBillData) object).getMailHeader());
        return result;
    }

    @Override
    public Class<? extends AbstractExtractResult> getSupportResult() {
        return MailExtractResult.class;
    }

    @Override
    public void save(AbstractExtractResult result) {
        mailExtractResultDao.insert((MailExtractResult) result);
    }
}
