/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.decode;

import java.nio.charset.Charset;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.crawler.core.domain.config.properties.UnicodeMode;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 12, 2014 8:04:46 PM
 */
public abstract class AbstractDecoder implements Configurable {

    protected UnicodeMode   mode;

    protected Configuration conf;

    public String decode(String content, String charset) {
        return decode(content, CharsetUtil.getCharset(charset));
    }

    public abstract String decode(String content, Charset charset);

    public UnicodeMode getMode() {
        return mode;
    }

    public void setMode(UnicodeMode mode) {
        this.mode = mode;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

}
