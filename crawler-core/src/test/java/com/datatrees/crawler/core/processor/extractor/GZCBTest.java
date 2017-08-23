/**
 * This document and its contents are protected by copyright 2016 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

public class GZCBTest extends BaseExtractorTest {

    @Override
    protected String getConfigFile() {
        return "GZCB/GZCBxtractorConfig.xml";
    }

    @Override
    protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
        map.put("subject", "广州银行账单");
        //                map.put("pageContent", this.getPageContent("src/test/resources/GZCB/GZCB20151115.html"));
        map.put("pageContent", this.getPageContent("/Users/wangcheng/Documents/newworkspace/rawdata1/rawdata/rawdata-submitter/fileoutput/1000445325/1000445325-1-0a66d38a07647e628f74f71634da707b/pageContent.html"));
    }

}
