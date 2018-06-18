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
import com.datatrees.crawler.core.domain.config.operation.impl.DecodeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.decode.DecodeType;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.decode.impl.BasicDecode;
import com.datatrees.crawler.core.processor.decode.impl.HexDecoder;
import com.datatrees.crawler.core.processor.decode.impl.StandardDecode;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月19日 下午12:05:28
 */
public class DecodeOperationImpl extends Operation<DecodeOperation> {

    private static final Logger log = LoggerFactory.getLogger(DecodeOperationImpl.class);

    public DecodeOperationImpl(@Nonnull DecodeOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected void doOperation(@Nonnull DecodeOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
      // get input
        String orginal = (String) operatingData;

        String charSet = StringUtils.isEmpty(operation.getCharset()) ? (StringUtils.isEmpty(RequestUtil.getContentCharset(request)) ? "UTF-8" : RequestUtil.getContentCharset(request)) : operation.getCharset();

        DecodeType decodeType = operation.getDecodeType();

        log.debug("decode-type: {}", decodeType);

        String result = decode(orginal, decodeType, charSet);
        response.setOutPut(result);
    }

    /**
     * @param original
     * @param decodeType
     * @param charset
     * @return
     */
    public static String decode(String original, DecodeType decodeType, String charset) {
        String result = original;
        try {
            if (decodeType != null && original != null) {
                switch (decodeType) {
                    case STANDARD:
                        result = new StandardDecode().decode(original, charset);
                        break;
                    case BASIC:
                        result = new BasicDecode().decode(original, charset);
                        break;
                    case HEX:
                        result = new HexDecoder().decode(original, charset);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("handlerDecode error!", e);
        }
        return result;
    }
}
