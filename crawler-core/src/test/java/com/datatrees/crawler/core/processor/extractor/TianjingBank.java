package com.datatrees.crawler.core.processor.extractor;

import java.util.Map;

/**
 * @author <A HREF="mailto:luyuan@datatrees.com.cn">Lu Yuan</A>
 * @version 1.0
 * @since 2016年7月21日 下午3:57:31
 */
public class TianjingBank extends BaseExtractorTest {

    @Override
    protected String getConfigFile() {
        return "tjbank/tjBankExtractor.xml";
    }

    @Override
    protected void addSimpleExtractSource(Map<String, Object> map) throws Exception {
        map.put("subject", "天津银行账单");
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/ABC/page_201502.html"));
        map.put("pageContent", this.getPageContent("/Users/luyuan/Downloads/10/pageContent"));
    }

}
