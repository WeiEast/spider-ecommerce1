package com.datatrees.crawler.core.domain.config.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 9:24:57 PM
 */
@Tag("pattern")
public class ParserPattern implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2300222575458432909L;
    private String             regex;
    private List<IndexMapping> indexMappings;// not necessary

    public ParserPattern() {
        super();
        indexMappings = new ArrayList<IndexMapping>();
    }

    @Tag("regex")
    public String getRegex() {
        return regex;
    }

    @Node("regex/text()")
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Tag("mappings")
    public List<IndexMapping> getIndexMappings() {
        return Collections.unmodifiableList(indexMappings);
    }

    @Node("mappings/map")
    public void setIndexMappings(IndexMapping indexMapping) {
        this.indexMappings.add(indexMapping);
    }

    public void setIndexMappings(List<IndexMapping> indexMappings) {
        this.indexMappings = indexMappings;
    }

}
