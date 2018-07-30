package com.datatrees.spider.share.service.extract;

import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;

public interface ExtractResultHandler {

    ResultType getSupportResultType();

    AbstractExtractResult build(ExtractMessage extractMessage);

    Class<? extends AbstractExtractResult> getSupportResult();

    void save(AbstractExtractResult result);
}
