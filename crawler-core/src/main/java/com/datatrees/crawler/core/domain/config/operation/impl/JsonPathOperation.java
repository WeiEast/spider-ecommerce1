package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author Jerry
 * @datetime 2015-07-17 20:00
 */
@Tag("operation")
@Path(".[@type='jsonpath']")
public class JsonPathOperation extends AbstractOperation {

    /**
     *
     */
    private static final long serialVersionUID = -926221137233814333L;
    private String jsonpath;

    @Tag
    public String getJsonpath() {
        return jsonpath;
    }

    @Node("text()")
    public void setJsonpath(String jsonpath) {
        this.jsonpath = jsonpath;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JsonPathOperation [jsonpath=" + jsonpath + "]";
    }

}
