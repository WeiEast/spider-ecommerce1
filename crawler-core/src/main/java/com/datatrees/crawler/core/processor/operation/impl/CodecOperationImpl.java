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

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.CodecOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.codec.CodecType;
import com.datatrees.crawler.core.domain.config.operation.impl.codec.HandlingType;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
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
    public void process(Request request, Response response) throws Exception {
        CodecOperation operation = getOperation();
        String charSet = RequestUtil.getContentCharset(request);
        // check default charset
        charSet = StringUtils.defaultIfEmpty(charSet, "UTF-8");

        CodecType cdType = operation.getCodecType();
        HandlingType handlType = operation.getHandlingType();

        logger.debug("codec-type: {}, handling-type: {}", cdType, handlType);

        // get input
        String input = OperationHelper.getStringInput(request, response);
        String result = handlerCodec(input, cdType, handlType, charSet);

        response.setOutPut(result);
    }

    private String handlerCodec(String orginal, CodecType cdType, HandlingType handlType, String charset) {
        String result = orginal;

        try {
            if (cdType != null && handlType != null) {
                switch (cdType) {
                    case MD5:
                        result = handlerMd5(handlType, orginal, charset);
                        break;
                    case BASE64:
                        result = handlerBase64(handlType, orginal, charset);
                        break;
                    case URI:
                        result = handlerURL(handlType, orginal, charset);
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            // ignore
            logger.error("codec error!", e);
        }
        return result;
    }

    private String handlerURL(HandlingType handlType, String orginal, String charset) throws UnsupportedEncodingException, DecoderException {
        String result = orginal;
        byte[] sourceBytes = orginal.getBytes(charset);
        byte[] dest = null;
        switch (handlType) {
            case ENCODE:
                dest = URLCodec.encodeUrl(null, sourceBytes);
                result = new String(dest, charset);
                break;

            default:
                dest = URLCodec.decodeUrl(sourceBytes);
                result = new String(dest, charset);
                break;
        }
        return result;
    }

    private String handlerBase64(HandlingType handlType, String orginal, String charset) throws Exception {
        String result = orginal;
        byte[] sourceBytes = orginal.getBytes(charset);
        byte[] dest = null;
        switch (handlType) {
            case ENCODE:
                dest = Base64.encodeBase64(sourceBytes);
                result = new String(dest, charset);
                break;

            default:
                dest = Base64.decodeBase64(sourceBytes);
                result = new String(dest, charset);
                break;
        }
        return result;
    }

    private String handlerMd5(HandlingType handlType, String orginal, String charset) throws Exception {
        String result = orginal;
        byte[] sourceBytes = orginal.getBytes(charset);
        switch (handlType) {
            case ENCODE:
                result = DigestUtils.md5Hex(sourceBytes);
                break;

            default:
                break;
        }
        return result;
    }
}
