/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.process.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.EscapeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.escape.EscapeType;
import com.datatrees.crawler.core.domain.config.operation.impl.escape.HandlingType;
import com.treefinance.crawler.framework.process.operation.Operation;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月19日 下午12:05:28
 */
public class EscapeOperationImpl extends Operation<EscapeOperation> {

    public EscapeOperationImpl(@Nonnull EscapeOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected boolean isSkipped(@Nonnull EscapeOperation operation, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        // invalid escape operation and skip
        boolean flag = operation.getEscapeType() == null || operation.getHandlingType() == null;
        if (flag) {
            logger.warn("Invalid escape operation and skip. 'escape-type' or 'handling-type' was null.");
        }
        return flag;
    }

    @Override
    protected Object doOperation(@Nonnull EscapeOperation operation, @Nonnull Object operatingData, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        String input = (String) operatingData;

        EscapeType escapeType = operation.getEscapeType();
        HandlingType handlingType = operation.getHandlingType();

        String result;
        switch (escapeType) {
            case HTML:
                result = handleHtml(handlingType, input);
                break;
            case JAVA:
                result = handleJava(handlingType, input);
                break;
            case JS:
                result = handleJs(handlingType, input);
                break;
            default:
                result = handleXml(handlingType, input);
                break;
        }

        return result;
    }

    private String handleXml(HandlingType handlingType, String content) {
        String result;

        if (HandlingType.ESCAPE.equals(handlingType)) {
            result = StringEscapeUtils.escapeXml(content);
        } else {
            result = StringEscapeUtils.unescapeXml(content);
        }

        return result;
    }

    private String handleJs(HandlingType handlingType, String input) {
        String result;

        if (HandlingType.ESCAPE.equals(handlingType)) {
            result = StringEscapeUtils.escapeJavaScript(input);
        } else {
            result = StringEscapeUtils.unescapeJavaScript(input);
        }

        return result;
    }

    private String handleHtml(HandlingType handlingType, String content) {
        String result;

        if (HandlingType.ESCAPE.equals(handlingType)) {
            result = StringEscapeUtils.escapeHtml(content);
        } else {
            result = StringEscapeUtils.unescapeHtml(content);
        }

        return result;
    }

    private String handleJava(HandlingType handlingType, String content) {
        String result;

        if (HandlingType.ESCAPE.equals(handlingType)) {
            result = StringEscapeUtils.escapeJava(content);
        } else {
            result = StringEscapeUtils.unescapeJava(content);
        }

        return result;
    }

}
