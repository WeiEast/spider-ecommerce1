/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.decode;

import com.datatrees.common.conf.Configuration;
import com.datatrees.crawler.core.domain.config.properties.UnicodeMode;
import com.datatrees.crawler.core.processor.decode.impl.*;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 12, 2014 8:03:50 PM
 */
public class DecodeFactory {

    private static final DecodeFactory instance = new DecodeFactory();

    private DecodeFactory() {}

    ;

    public static DecodeFactory instance() {
        return instance;
    }

    public AbstractDecoder getDecoder(UnicodeMode mode, Configuration conf) {
        AbstractDecoder decoder = null;
        if (mode != null) {
            switch (mode) {
                case COMPLEX:
                    decoder = new RegexDecoder();
                    break;
                case HEX:
                    decoder = new HexDecoder();
                    break;
                case STANDARD:
                    decoder = new StandardDecode();
                    break;
                case SPECIAL:
                    decoder = new SpecialDecoder();
                    break;
                default:
                    decoder = new BasicDecode();
                    break;
            }
        }

        if (decoder == null) {
            decoder = new BasicDecode();
        }

        decoder.setMode(mode);
        decoder.setConf(conf);
        return decoder;
    }

}
