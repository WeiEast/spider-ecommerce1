/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.decode.impl;

import java.nio.charset.Charset;

import com.datatrees.common.util.StringUtils;
import com.treefinance.crawler.framework.decode.Decoder;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 下午5:48:19
 */
public class StandardDecoder implements Decoder {

    public static final  StandardDecoder DEFAULT = new StandardDecoder();
    private static final Logger          log     = LoggerFactory.getLogger(HexDecoder.class);

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
            StringBuilder sb = new StringBuilder();
            String[] hex = result.split("\\\\u");
            for (String aHex : hex) {
                if (StringUtils.isNotBlank(aHex)) {
                    int data = Integer.parseInt(aHex, 16);
                    sb.append((char) data);
                }
            }
            result = sb.toString();
        }
        result = StringEscapeUtils.unescapeHtml(result);
        return result;
    }
}
