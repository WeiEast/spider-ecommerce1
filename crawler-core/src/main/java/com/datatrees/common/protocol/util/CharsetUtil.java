/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2014
 */
package com.datatrees.common.protocol.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 9:43:26 AM
 */
public class CharsetUtil {
    private static final Logger LOGGER  = LoggerFactory.getLogger(CharsetUtil.class);
    public static final  Charset ASCII      = Consts.ASCII;
    public static final  Charset ISO_8859_1 = Consts.ISO_8859_1;
    public static final  Charset UTF_8      = Consts.UTF_8;
    public static final  String  UTF_8_NAME = UTF_8.name();
    public static final  String  DEFAULT    = UTF_8_NAME;

    public static Charset getCharset(String charsetName, String defaultCharset) {
        Charset charset = null;

        if (StringUtils.isNotEmpty(charsetName)) {
            try {
                charset = Charset.forName(charsetName);
            } catch (UnsupportedCharsetException e) {
                LOGGER.warn("Charset '{}' is not supported. And use default charset '{}' instead.", charsetName,
                        defaultCharset);
            }
        }

        if (charset == null && StringUtils.isNotEmpty(defaultCharset)) {
            try {
                charset = Charset.forName(defaultCharset);
            } catch (UnsupportedCharsetException e) {
                LOGGER.warn("Default charset '{}' is not supported. ", defaultCharset);
            }
        }

        LOGGER.warn("Can not find charset '{}' or default '{}'.", charsetName, defaultCharset);

        return charset;
    }

    public static Charset getCharset(String charsetName) {
        return getCharset(charsetName, DEFAULT);
    }

    public static String getDefaultCharsetName() {
        return Charset.defaultCharset().name();
    }

    public static Charset getDefaultCharset() {
        return Charset.defaultCharset();
    }

    public static boolean exist(String charsetName) {
        return Charset.isSupported(charsetName);
    }

}
