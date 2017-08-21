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
 * @since 2016年7月21日 下午7:33:35
 */
public class CzBankTest extends BaseExtractorTest {

  @Override
  protected String getConfigFile() {
    return "czbank/CzBankExtratorConfig.xml";
  }

  @Override
  protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
    map.put("subject", "信用社账单");
//        map.put("pageContent", this.getPageContent("src/test/resources/czbank/201607.html"));
//        map.put("pageContent", this.getPageContent("src/test/resources/czbank/201507.html"));
    map.put("pageContent", this.getPageContent("src/test/resources/czbank/201512.html"));
  }

}
