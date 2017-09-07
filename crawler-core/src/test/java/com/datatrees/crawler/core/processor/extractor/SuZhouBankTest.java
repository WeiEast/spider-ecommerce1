package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

/**
 * @author <A HREF="mailto:luyuan@datatrees.com.cn">Lu Yuan</A>
 * @version 1.0
 * @since 2016年7月22日 上午11:33:38
 */
public class SuZhouBankTest extends BaseExtractorTest {

    @Override
    protected String getConfigFile() {
        return "szbank/szBankExtractor.xml";
    }

    @Override
    protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
        map.put("subject", "苏州银行账单");
        // map.put("pageContent", this.getPageContent("src/test/resources/ABC/page_201502.html"));
        map.put("pageContent", this.getPageContent("/Users/luyuan/Downloads/11/pageContent"));
    }

}
