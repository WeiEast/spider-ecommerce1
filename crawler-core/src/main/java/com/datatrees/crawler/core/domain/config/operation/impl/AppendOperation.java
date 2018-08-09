package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;
import org.apache.commons.lang3.StringUtils;

@Tag("operation")
@Path(".[@type='append']")
public class AppendOperation extends AbstractOperation {

    private static final long    serialVersionUID = -7536995227560319224L;

    private              Integer index;

    private              String  value;

    @Tag
    public String getValue() {
        return StringUtils.defaultString(value);
    }

    @Node("text()")
    public void setValue(String value) {
        this.value = value;
    }

    @Attr("index")
    public Integer getIndex() {
        return index == null ? -1 : index;
    }

    @Node("@index")
    public void setIndex(Integer index) {
        this.index = index;
    }
}
