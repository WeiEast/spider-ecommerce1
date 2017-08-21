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
public class CMBTest extends BaseExtractorTest {

  @Override
  protected String getConfigFile() {
    return "wdzj/wdzjExtractor.xml";
  }

  @Override
  protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
    map.put("subject", "招商银行信用卡电子账单");
    map.put(
        "pageContent",
        this.getPageContent(
            "/Users/wangcheng/51back/51files/7.16/crawler_lib/src/test/resources/xpath/table.html"));
    map.put("NameOnCard", "沈黎明");
    map.put("ColumnOfDate", "201605");
    // this.getPageContent("src/test/resources/cmb/201603.html"));
    // this.getPageContent("src/test/resources/cmb/jianban201401.html"));
    // this.getPageContent("src/test/resources/cmb/20150908.html"));
  }

}
