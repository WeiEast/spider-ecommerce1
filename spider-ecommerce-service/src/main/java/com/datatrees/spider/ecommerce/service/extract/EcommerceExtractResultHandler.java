package com.datatrees.spider.ecommerce.service.extract;

import javax.annotation.Resource;

import com.datatrees.spider.share.service.domain.data.EcommerceData;
import com.datatrees.spider.ecommerce.dao.EcommerceExtractResultDAO;
import com.datatrees.spider.share.domain.model.EcommerceExtractResult;
import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.extract.ExtractResultHandler;
import org.springframework.stereotype.Component;

@Component
public class EcommerceExtractResultHandler implements ExtractResultHandler {

    @Resource
    private EcommerceExtractResultDAO ecommerceExtractResultDao;

    @Override
    public ResultType getSupportResultType() {
        return ResultType.ECOMMERCE;
    }

    @Override
    public AbstractExtractResult build(ExtractMessage extractMessage) {
        Object object = extractMessage.getMessageObject();
        EcommerceExtractResult result = new EcommerceExtractResult();
        result.setEcommerceId(extractMessage.getTypeId());
        result.setUniqueSign(((EcommerceData) object).getUniqueSign());
        result.setUrl(((EcommerceData) object).getUrl());
        result.setExtraInfo(((EcommerceData) object).getExtraInfo());
        return result;
    }

    @Override
    public Class<? extends AbstractExtractResult> getSupportResult() {
        return EcommerceExtractResult.class;
    }

    @Override
    public void save(AbstractExtractResult result) {
        ecommerceExtractResultDao.insert((EcommerceExtractResult) result);
    }
}
