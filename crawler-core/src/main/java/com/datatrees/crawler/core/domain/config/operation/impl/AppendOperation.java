package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

@Tag("operation")
@Path(".[@type='append']")
public class AppendOperation extends AbstractOperation {

    private static final long serialVersionUID = -7536995227560319224L;
    private Integer index;
    private String  value;

    @Tag
    public String getValue() {
        return value;
    }

    @Node("text()")
    public void setValue(String value) {
        this.value = value;
    }

    @Attr("index")
    public Integer getIndex() {
        return index;
    }

    @Node("@index")
    public void setIndex(Integer index) {
        this.index = index;
    }
}
