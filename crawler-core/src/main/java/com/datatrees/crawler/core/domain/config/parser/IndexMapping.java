package com.datatrees.crawler.core.domain.config.parser;

import java.io.Serializable;

import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 9:25:00 PM
 */
@Tag("map")
public class IndexMapping implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6418187744014381993L;
    private Integer groupIndex;
    private String  placeholder;

    @Attr("group-index")
    public Integer getGroupIndex() {
        return groupIndex;
    }

    @Node("@group-index")
    public void setGroupIndex(Integer groupIndex) {
        this.groupIndex = groupIndex;
    }

    @Attr("placeholder")
    public String getPlaceholder() {
        return placeholder;
    }

    @Node("@placeholder")
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

}
