/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月15日 下午1:39:17
 */
public class ZJRCUTest extends BaseExtractorTest {

  @Override
  protected String getConfigFile() {
    return "zjrcu/ZJRCUExtractorConfig.xml";
  }

  @Override
  protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
    map.put("subject", "信用社账单");
//        map.put("pageContent", this.getPageContent("src/test/resources/zjrcu/ZJRCU20150320.html"));
//        map.put("pageContent", this.getPageContent("src/test/resources/zjrcu/ZJRCU20151220.html"));
    map.put("pageContent", this.getPageContent(
        "/Users/wangcheng/Documents/newworkspace/rawdata1/rawdata/rawdata-submitter/fileoutput/1000458828/1000458828-1-fcc3c1d21e6bf298796b78e183c5f000/pageContent.html"));
  }

}
