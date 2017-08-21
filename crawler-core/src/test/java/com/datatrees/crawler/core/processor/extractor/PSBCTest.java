package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

public class PSBCTest extends BaseExtractorTest {

  @Override
  protected String getConfigFile() {
    return "psbc/PSBCExtractorConfig.xml";
  }

  @Override
  protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
    map.put("subject", "邮储银行账单");
    map.put(
        "pageContent",
        this.getPageContent(
            "/Users/wangcheng/51back/51files/7.16/crawler_lib/src/test/resources/psbc/psbc_old.html"));

  }

}
