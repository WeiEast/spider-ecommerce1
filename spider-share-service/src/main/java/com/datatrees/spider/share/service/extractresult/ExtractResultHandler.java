package com.datatrees.spider.share.service.extractresult;

import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;

public interface ExtractResultHandler {

    ResultType getResultType();



    AbstractExtractResult build(ExtractMessage extractMessage)
}
