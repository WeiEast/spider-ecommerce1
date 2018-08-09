package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.triple.TripleType;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

@Tag("operation")
@Path(".[@type='triple']")
public class TripleOperation extends AbstractOperation {

    /**
     *
     */
    private static final long       serialVersionUID = 393008470332561298L;

    private              TripleType tripleType;

    private              String     value;

    @Attr("triple-type")
    public TripleType getTripleType() {
        return tripleType;
    }

    @Node("@triple-type")
    public void setTripleType(String tripleType) {
        this.tripleType = TripleType.getOperationType(tripleType);
    }

    @Tag
    public String getValue() {
        return value;
    }

    @Node("text()")
    public void setValue(String value) {
        this.value = value;
    }
}
