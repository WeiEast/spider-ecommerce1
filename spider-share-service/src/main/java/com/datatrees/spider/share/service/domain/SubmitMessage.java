package com.datatrees.spider.share.service.domain;

import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.treefinance.crawler.framework.process.domain.PageExtractObject;

/**
 * Created by wuminlang on 15/7/28.
 */
public class SubmitMessage {

    private final ExtractMessage extractMessage;

    private final AbstractExtractResult result;

    private PageExtractObject pageExtractObject;

    public SubmitMessage(ExtractMessage extractMessage, AbstractExtractResult result) {
        this.extractMessage = extractMessage;
        this.result = result;
    }


    public AbstractExtractResult getResult() {
        return result;
    }

    public ExtractMessage getExtractMessage() {
        return extractMessage;
    }

    public PageExtractObject getPageExtractObject() {
        return pageExtractObject;
    }

    public void setPageExtractObject(PageExtractObject pageExtractObject) {
        this.pageExtractObject = pageExtractObject;
    }

    public void addSubmitKey(String name, String value) {
        extractMessage.addSubmitKey(name, value);
    }

    @Override
    public String toString() {
        return "SubmitMessage [extractMessage=" + extractMessage + ", result=" + result + "]";
    }

}
