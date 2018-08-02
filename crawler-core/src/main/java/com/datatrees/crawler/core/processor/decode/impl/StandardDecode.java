/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.decode.impl;

import java.nio.charset.Charset;

import com.datatrees.common.util.StringUtils;
import com.datatrees.crawler.core.processor.decode.AbstractDecoder;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 下午5:48:19
 */
public class StandardDecode extends AbstractDecoder {

    private static final Logger log = LoggerFactory.getLogger(HexDecoder.class);

    @Override
    public String decode(String content, Charset charset) {

        String result = content;
        if (charset == null) {
            charset = Charset.defaultCharset();
            log.warn("using default charset! " + charset);
        }
        result = result.replace("\\x", "\\u00").trim();
        try {
            result = StringEscapeUtils.unescapeJava(result);
        } catch (Exception e) {
            StringBuffer sb = new StringBuffer();
            String[] hex = result.split("\\\\u");
            for (int i = 0; i < hex.length; i++) {
                if (StringUtils.isNotBlank(hex[i])) {
                    int data = Integer.parseInt(hex[i], 16);
                    sb.append((char) data);
                }
            }
            result = sb.toString();
        }
        result = StringEscapeUtils.unescapeHtml(result);
        return result;
    }
}
