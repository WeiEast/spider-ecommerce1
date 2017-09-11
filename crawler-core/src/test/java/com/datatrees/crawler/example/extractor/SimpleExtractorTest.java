package com.datatrees.crawler.example.extractor;

import java.io.File;
import java.util.Map;

/**
 * @author Jerry
 * @since 01:37 04/07/2017
 */
public abstract class SimpleExtractorTest extends ExtractorTest {

    @Override
    protected String getConfigFile() {
        return getRoot() + "/ExtractorConfig.xml";
    }

    private String getRoot() {
        return "email/" + getAlias();
    }

    protected abstract String getAlias();

    @Override
    protected void addSimpleExtractParameters(Map<String, Object> map) throws Exception {
        map.put("subject", getSubject());
        map.put("pageContent", this.getPageContent());

        appendExtractParameters(map);
    }

    protected void appendExtractParameters(Map<String, Object> map) {
    }

    protected abstract String getSubject();

    protected Object getPageContent() throws Exception {
        File file = getResource(getRoot() + "/source/" + getPageSource());
        return this.getPageContent(file);
    }

    protected abstract String getPageSource();
}
