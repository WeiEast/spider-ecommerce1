/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.decode.impl;

import java.nio.charset.Charset;
import java.util.regex.Matcher;

import com.treefinance.crawler.framework.decode.Decoder;
import com.treefinance.toolkit.util.RegExp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 14, 2014 11:30:55 AM
 */
public class HexDecoder implements Decoder {

    public  static final HexDecoder DEFAULT = new HexDecoder();

    protected static final   Logger log     = LoggerFactory.getLogger(HexDecoder.class);

    protected static final String pattern = "((\\\\x([\\w\\d]{2}))+)";

    @Override
    public String decode(String content, Charset charset) {
        if (charset == null) {
            charset = Charset.defaultCharset();
            log.warn("using default charset! " + charset);
        }

        return replaceByRegex(content, charset, pattern);
    }

    /**
     * @param content
     * @param charset
     * @param regex
     * @return
     */
    private String replaceByRegex(String content, Charset charset, String regex) {
        Matcher matcher = RegExp.getMatcher(regex, content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String orginal = matcher.group(1);
            String rel = hexToString(orginal, charset);
            log.debug("orginal:" + orginal + " rel:\t" + rel);
            rel = rel.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
            matcher.appendReplacement(sb, rel);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String hexToString(String content, Charset charSet) {
        String result = "";
        try {
            String sourceArr[] = content.split("\\\\");
            byte[] byteArr = new byte[sourceArr.length - 1];
            for (int i = 1; i < sourceArr.length; i++) {
                Integer hexInt = Integer.decode("0" + sourceArr[i]);
                byteArr[i - 1] = hexInt.byteValue();
            }
            result = (new String(byteArr, charSet));
        } catch (Exception e) {
            log.error("hex to String error", e);
        }
        return result;
    }

}
