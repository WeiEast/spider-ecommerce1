package com.datatrees.spider.share.service.extract.impl;

import javax.annotation.Resource;

import com.datatrees.spider.share.domain.DefaultData;
import com.datatrees.spider.share.dao.DefaultExtractResultDAO;
import com.datatrees.spider.share.domain.DefaultExtractResult;
import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.extract.ExtractResultHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultExtractResultHandler implements ExtractResultHandler {

    @Resource
    private DefaultExtractResultDAO defaultExtractResultDAO;

    @Override
    public ResultType getSupportResultType() {
        return ResultType.DEFAULT;
    }

    @Override
    public AbstractExtractResult build(ExtractMessage extractMessage) {
        Object object = extractMessage.getMessageObject();
        DefaultExtractResult result = new DefaultExtractResult();
        result.setUrl(((DefaultData) object).getUrl());
        result.setUniqueSign(((DefaultData) object).getUniqueSign());
        result.setExtraInfo(((DefaultData) object).getExtraInfo());
        return result;
    }

    @Override
    public Class<? extends AbstractExtractResult> getSupportResult() {
        return DefaultExtractResult.class;
    }

    @Override
    public void save(AbstractExtractResult result) {
        defaultExtractResultDAO.insert((DefaultExtractResult) result);
    }
}
