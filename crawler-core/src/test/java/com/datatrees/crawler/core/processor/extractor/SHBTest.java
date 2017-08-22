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
public class SHBTest extends BaseExtractorTest {

  @Override
  protected String getConfigFile() {
    return "SHB/SHBExtractorConfig.xml";
  }

  @Override
  protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
    map.put("subject", "上海银行账单");
//        map.put("pageContent", this.getPageContent("src/test/resources/SHB/SHB20160311.html"));
    //  map.put("pageContent", this.getPageContent("src/test/resources/SHB/SHB20150717.html"));
    map.put("pageContent", this.getPageContent(
        "file:///Users/wangcheng/Downloads/%E8%90%A5%E9%94%80%E5%9B%BE%E7%89%87%E7%89%88%E4%BF%AE%E6%94%B9.html"));
//        map.put("pageContent", this.getPageContent("src/test/resources/SHB/SHB20151211.html"));
  }

}
