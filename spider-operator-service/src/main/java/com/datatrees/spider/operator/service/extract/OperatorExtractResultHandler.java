package com.datatrees.spider.operator.service.extract;

import com.datatrees.rawdatacentral.core.model.data.OperatorData;
import com.datatrees.rawdatacentral.domain.model.OperatorExtractResult;
import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.extract.ExtractResultHandler;
import org.springframework.stereotype.Component;

@Component
public class OperatorExtractResultHandler implements ExtractResultHandler {

    @Override
    public ResultType getSupportResultType() {
        return ResultType.OPERATOR;
    }

    @Override
    public AbstractExtractResult build(ExtractMessage extractMessage) {
        Object object = extractMessage.getMessageObject();
        OperatorExtractResult result = new OperatorExtractResult();
        result.setOperatorId(extractMessage.getTypeId());
        result.setUrl(((OperatorData) object).getUrl());
        result.setUniqueSign(((OperatorData) object).getUniqueSign());
        result.setExtraInfo(((OperatorData) object).getExtraInfo());
        return result;
    }
}
