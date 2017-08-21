/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.decode.impl;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.decode.AbstractDecoder;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 12, 2014 8:27:03 PM
 */
public class RegexDecoder extends AbstractDecoder {

    private static final Logger log = LoggerFactory.getLogger(RegexDecoder.class);

    @Override
    public String decode(String content, Charset charset) {
        if (PatternUtils.match(HexDecoder.pattern, content)) {
            log.debug("RegexDecoder using hex decoder");
            return new HexDecoder().decode(content, charset);
        } else {
            log.debug("RegexDecoder using basic decoder");
            return new BasicDecode().decode(content, charset);
        }
    }

}
