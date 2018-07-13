package com.datatrees.crawler.core.domain.config.segment.impl;

import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;

/**
 * @author Jerry
 * @datetime 2015-07-17 19:42
 */
@Path(".[@type='jsonpath']")
public class JsonPathSegment extends AbstractSegment {

    private String jsonpath;

    @Attr("value")
    public String getJsonpath() {
        return jsonpath;
    }

    @Node("@value")
    public void setJsonpath(String jsonpath) {
        this.jsonpath = jsonpath;
    }

}
