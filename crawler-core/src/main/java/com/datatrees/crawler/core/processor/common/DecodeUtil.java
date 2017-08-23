/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.config.properties.UnicodeMode;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.decode.AbstractDecoder;
import com.datatrees.crawler.core.processor.decode.DecodeFactory;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 下午5:57:10
 */
public class DecodeUtil {

    public static String decodeContent(String content, Request request) {
        String result = content;
        boolean change = false;
        String charset = RequestUtil.getContentCharset(request);
        SearchProcessorContext wrapper = (SearchProcessorContext) RequestUtil.getProcessorContext(request);
        if (wrapper.needDecoder()) {
            UnicodeMode unicodeMode = wrapper.getSearchConfig().getProperties().getUnicodeMode();
            AbstractDecoder decoder = DecodeFactory.instance().getDecoder(unicodeMode, RequestUtil.getConf(request));
            if (decoder != null) {
                change = true;
                result = decoder.decode(content, charset);
            }
        }
        if (change) {
            RequestUtil.setContent(request, result);
        }
        return result;
    }

}
