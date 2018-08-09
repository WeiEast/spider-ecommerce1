package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
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
    private Boolean emptyToNull;

    @Tag
    public String getJsonpath() {
        return jsonpath;
    }

    @Node("text()")
    public void setJsonpath(String jsonpath) {
        this.jsonpath = jsonpath;
    }

    @Attr("empty-to-null")
    public Boolean getEmptyToNull() {
        return emptyToNull;
    }

    @Node("@empty-to-null")
    public void setEmptyToNull(Boolean emptyToNull) {
        this.emptyToNull = emptyToNull;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JsonPathOperation [jsonpath=" + jsonpath + "]";
    }

}
