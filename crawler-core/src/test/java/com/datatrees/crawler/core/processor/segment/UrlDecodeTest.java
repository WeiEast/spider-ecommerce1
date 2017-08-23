/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.segment;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import junit.framework.Assert;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 3:07:49 PM
 */
public class UrlDecodeTest extends BaseConfigTest {

    @Test
    public void testUrlDecode() {
        String content = "xx%2Fss";
        URLCodec codec = new URLCodec();
        try {
            String expected = "xx/ss";
            String result = codec.decode(content);
            Assert.assertEquals(expected, result);
        } catch (DecoderException e) {
            Assert.fail(e.getMessage());
        }
    }

}
