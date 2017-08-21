/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

/**
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年7月20日 下午9:34:18
 */
public class CqBankTest extends BaseExtractorTest {

  @Override
  protected String getConfigFile() {
    return "cqbank/CqBankExtratorConfig.xml";
  }

  @Override
  protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
    map.put("subject", "信用社账单");
    map.put("pageContent", this.getPageContent(
        "/Users/zhangjiachen/Documents/newworkspace/rawdata/rawdata-submitter/fileoutput/0720/1000844404-1-2d57c3be0de5a5c8cc3a40828c9269eb/pageContent.html"));
    map.put("pageContent", this.getPageContent("src/test/resources/cqbank/201607.html"));
    map.put("pageContent", this.getPageContent("src/test/resources/cqbank/201501.html"));
  }

}
