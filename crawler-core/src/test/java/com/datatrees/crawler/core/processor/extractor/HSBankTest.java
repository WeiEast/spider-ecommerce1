package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

/**
 * @author <A HREF="mailto:luyuan@datatrees.com.cn">Lu Yuan</A>
 * @version 1.0
 * @since 2016年7月20日 下午9:41:46
 */
public class HSBankTest extends BaseExtractorTest {

  @Override
  protected String getConfigFile() {
    return "hsbank/hsBankExtractor.xml";
  }

  @Override
  protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
    map.put("subject", "徽商银行账单");
    // map.put("pageContent", this.getPageContent("src/test/resources/ABC/page_201502.html"));
    map.put("pageContent", this.getPageContent("/Users/luyuan/Downloads/7/pageContent1"));
  }

}
