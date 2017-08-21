/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.util;

import org.junit.Test;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.common.CodecUtils;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月11日 上午11:20:27
 */
public class CodecUtilTest extends BaseConfigTest {
    @Test
    public void test() throws Exception {
        String content = "xpath/table.html$$$23e32e23e2个人资料";
        System.out.println(new String(CodecUtils.encrypt(content.getBytes(), content)));
        System.out.println(new String(CodecUtils.decrypt(new String(CodecUtils.encrypt(content.getBytes(), content)).getBytes(), content)));

    }

    @Test
    public void test1() throws Exception {
        System.out.println(new String(CodecUtils.decrypt("fks1vOd6dpFPUObxnBBWfsTmwu4ec8wYA5MHggVD3OSyDVYHJVi019bUilEKZ/KT".getBytes())));
    }
}
