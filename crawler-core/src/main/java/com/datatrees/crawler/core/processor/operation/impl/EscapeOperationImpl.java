/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.EscapeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.escape.EscapeType;
import com.datatrees.crawler.core.domain.config.operation.impl.escape.HandlingType;
import com.datatrees.crawler.core.processor.operation.Operation;
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
    protected Object doOperation(@Nonnull EscapeOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {

        // get input
        String orginal = (String) operatingData;
        EscapeType escapeType = operation.getEscapeType();
        HandlingType handlType = operation.getHandlingType();

        logger.debug("escape-Type: {}, handling-Type: {}", escapeType, handlType);

        return handlerEscape(orginal, escapeType, handlType);
    }

    private String handlerEscape(String orginal, EscapeType escapeType, HandlingType handlType) {
        String result = orginal;

        try {
            if (escapeType != null && handlType != null) {
                switch (escapeType) {
                    case HTML:
                        result = handlerHtml(handlType, orginal);
                        break;
                    case JAVA:
                        result = handlerJava(handlType, orginal);
                        break;
                    case JS:
                        result = handlerJs(handlType, orginal);
                        break;
                    case XML:
                        result = handlerXml(handlType, orginal);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("handlerEscape error!", e);
        }
        return result;
    }

    private String handlerXml(HandlingType handlType, String orginal) throws Exception {
        String result = orginal;
        switch (handlType) {
            case ESCAPE:
                result = StringEscapeUtils.escapeXml(orginal);
                break;
            case UNESCAPE:
                result = StringEscapeUtils.unescapeXml(orginal);
                break;
            default:
                break;
        }
        return result;
    }

    private String handlerJs(HandlingType handlType, String orginal) throws Exception {
        String result = orginal;
        switch (handlType) {
            case ESCAPE:
                result = StringEscapeUtils.escapeJavaScript(orginal);
                break;
            case UNESCAPE:
                result = StringEscapeUtils.unescapeJavaScript(orginal);
                break;
            default:
                break;
        }
        return result;
    }

    private String handlerHtml(HandlingType handlType, String orginal) throws Exception {
        String result = orginal;
        switch (handlType) {
            case ESCAPE:
                result = StringEscapeUtils.escapeHtml(orginal);
                break;
            case UNESCAPE:
                result = StringEscapeUtils.unescapeHtml(orginal);
                break;
            default:
                break;
        }
        return result;
    }

    private String handlerJava(HandlingType handlType, String orginal) throws Exception {
        String result = orginal;
        switch (handlType) {
            case ESCAPE:
                result = StringEscapeUtils.escapeJava(orginal);
                break;
            case UNESCAPE:
                result = StringEscapeUtils.unescapeJava(orginal);
                break;
            default:
                break;
        }
        return result;
    }

}
