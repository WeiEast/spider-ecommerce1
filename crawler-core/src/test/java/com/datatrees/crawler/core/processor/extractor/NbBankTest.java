/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.core.processor.extractor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年7月20日 下午1:49:30
 */
public class NbBankTest extends BaseExtractorTest {

    @Override
    protected String getConfigFile() {
        return "NbBank/NbBankExtractorConfig.xml";
    }

    @Override
    protected List<Map<String, Object>> getExtractResources() throws Exception {
        Map<String, Object> mailMap = new HashMap<>();

        return Collections.singletonList(mailMap);
    }

    @Override
    protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
        map.put("subject", "信用社账单");
        //        mailMap.put("pageContent", this.getPageContent("src/test/resources/NbBank/201501.html"));
        //        mailMap.put("pageContent", this.getPageContent("src/test/resources/NbBank/201601.html"));
        map.put("pageContent", this.getPageContent("src/test/resources/NbBank/201606.html"));
        map.put("pageContent", this.getPageContent("/Users/zhangjiachen/Documents/newworkspace/rawdata/rawdata-submitter/fileoutput/1000783093/1000783093-1-c6a6c808186840153135972fb4b945e6/pageContent.html"));
        map.put("pageContent", this.getPageContent("/Users/zhangjiachen/Documents/newworkspace/rawdata/rawdata-submitter/fileoutput/0720/1000847630-1-6e4e0f856fcad7894b92743f495c9d57/pageContent.html"));
    }
}
