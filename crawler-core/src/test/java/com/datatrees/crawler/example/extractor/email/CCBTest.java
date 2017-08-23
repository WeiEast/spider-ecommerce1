/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.example.extractor.email;

import com.datatrees.crawler.example.extractor.SimpleExtractorTest;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月15日 下午1:39:17
 */
public class CCBTest extends SimpleExtractorTest {

    @Override
    protected String getWebsite() {
        return "ccb.com";
    }

    @Override
    protected String getAlias() {
        return "CCB";
    }

    @Override
    protected String getSubject() {
        return "建行账单";
    }

    @Override
    protected String getPageSource() {
        return "page_201707.html";
    }

}
