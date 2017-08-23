/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.decode.impl;

import java.nio.charset.Charset;

import com.datatrees.crawler.core.processor.decode.AbstractDecoder;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 12, 2014 8:22:14 PM
 */
public class BasicDecode extends AbstractDecoder {

    @Override
    public String decode(String content, Charset charset) {
        return StringEscapeUtils.unescapeHtml(content);
    }

}
