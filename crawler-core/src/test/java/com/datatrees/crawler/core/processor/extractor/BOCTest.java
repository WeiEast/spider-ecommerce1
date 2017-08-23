/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月15日 下午1:39:17
 */
public class BOCTest extends BaseExtractorTest {

    @Override
    protected String getConfigFile() {
        return "cbb/CBBExtractorConfig.xml";
    }

    @Override
    protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
        map.put("subject", "中行账单");
        // map.put("pageContent", this.getPageContent("src/test/resources/ABC/page_201502.html"));
        map.put("pageContent", this.getPageContent("/Users/wangcheng/Downloads/1000344055_10_20160501_5487579/pageContent.html"));
    }
}
