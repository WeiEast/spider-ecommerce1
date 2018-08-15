/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;
import java.io.UnsupportedEncodingException;

import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.CodecOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.codec.CodecType;
import com.datatrees.crawler.core.domain.config.operation.impl.codec.HandlingType;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;

/**
 * handle codec operation decode/encode etc..
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 27, 2014 12:30:43 PM
 */
public class CodecOperationImpl extends Operation<CodecOperation> {

    public CodecOperationImpl(@Nonnull CodecOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    protected boolean isSkipped(@Nonnull CodecOperation operation, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        // invalid codec operation and skip
        boolean flag = operation.getCodecType() == null || operation.getHandlingType() == null;
        if (flag) {
            logger.warn("Invalid codec operation and skip. 'codec-type' or 'handling-type' was null.");
        }
        return flag;
    }

    @Override
    protected Object doOperation(@Nonnull CodecOperation operation, @Nonnull Object operatingData, @Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        String input = (String) operatingData;

        String charset = RequestUtil.getContentCharset(request);
        charset = StringUtils.defaultIfEmpty(charset, CharsetUtil.UTF_8_NAME);

        CodecType cdType = operation.getCodecType();
        HandlingType handlingType = operation.getHandlingType();

        String result;
        switch (cdType) {
            case MD5:
                result = handleMd5(handlingType, input, charset);
                break;
            case BASE64:
                result = handleBase64(handlingType, input, charset);
                break;
            default:
                result = handleURL(handlingType, input, charset);
                break;
        }
        return result;
    }

    private String handleURL(HandlingType handlingType, String url, String charset) throws UnsupportedEncodingException, DecoderException {
        byte[] sourceBytes = url.getBytes(charset);

        byte[] dest;
        if (HandlingType.ENCODE.equals(handlingType)) {
            dest = URLCodec.encodeUrl(null, sourceBytes);
        } else {
            dest = URLCodec.decodeUrl(sourceBytes);
        }

        return new String(dest, charset);
    }

    private String handleBase64(HandlingType handlingType, String content, String charset) throws Exception {
        byte[] sourceBytes = content.getBytes(charset);

        byte[] dest;
        if (HandlingType.ENCODE.equals(handlingType)) {
            dest = Base64.encodeBase64(sourceBytes);
        } else {
            dest = Base64.decodeBase64(sourceBytes);
        }

        return new String(dest, charset);
    }

    private String handleMd5(HandlingType handlingType, String content, String charset) throws Exception {
        String result = content;

        if (HandlingType.ENCODE.equals(handlingType)) {
            byte[] sourceBytes = content.getBytes(charset);
            result = DigestUtils.md5Hex(sourceBytes);
        }

        return result;
    }
}
