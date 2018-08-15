/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.process.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.DecodeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.decode.DecodeType;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.decode.impl.BasicDecoder;
import com.treefinance.crawler.framework.decode.impl.HexDecoder;
import com.treefinance.crawler.framework.decode.impl.StandardDecoder;
import com.treefinance.crawler.framework.process.operation.Operation;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月19日 下午12:05:28
 */
public class DecodeOperationImpl extends Operation<DecodeOperation> {

    public DecodeOperationImpl(@Nonnull DecodeOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected boolean isSkipped(@Nonnull DecodeOperation operation, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        // invalid decode operation and skip
        boolean flag = operation.getDecodeType() == null;
        if (flag) {
            logger.warn("Invalid decode operation and skip. 'decode-type' was null.");
        }
        return flag;
    }

    @Override
    protected Object doOperation(@Nonnull DecodeOperation operation, @Nonnull Object operatingData, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        String input = (String) operatingData;

        String charset = operation.getCharset();
        if (StringUtils.isEmpty(charset)) {
            charset = RequestUtil.getContentCharset(request);
            if (StringUtils.isEmpty(charset)) {
                charset = CharsetUtil.UTF_8_NAME;
            }
        }

        DecodeType decodeType = operation.getDecodeType();

        String result;
        switch (decodeType) {
            case BASIC:
                result = new BasicDecoder().decode(input, charset);
                break;
            case HEX:
                result = new HexDecoder().decode(input, charset);
                break;
            default:
                result = new StandardDecoder().decode(input, charset);
                break;
        }

        return result;
    }
}
