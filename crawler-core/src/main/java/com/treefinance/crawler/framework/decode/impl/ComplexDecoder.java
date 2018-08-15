/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.decode.impl;

import java.nio.charset.Charset;

import com.treefinance.toolkit.util.RegExp;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 12, 2014 8:27:03 PM
 */
public class ComplexDecoder extends HexDecoder {

    public static ComplexDecoder DEFAULT = new ComplexDecoder();

    @Override
    public String decode(String content, Charset charset) {
        if (RegExp.find(content, pattern)) {
            log.debug("ComplexDecoder using hex decoder");
            return super.decode(content, charset);
        } else {
            log.debug("ComplexDecoder using basic decoder");
            return BasicDecoder.DEFAULT.decode(content, charset);
        }
    }

}
