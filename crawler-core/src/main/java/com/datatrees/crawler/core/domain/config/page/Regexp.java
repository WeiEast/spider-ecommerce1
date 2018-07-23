/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.page;

import java.io.Serializable;

import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 4:54:10 PM
 */
public class Regexp implements Serializable {

    /**
     *
     */
    private static final long    serialVersionUID = 996749945913257735L;

    private              String  regex;

    private              Integer index;

    @Tag
    public String getRegex() {
        return regex;
    }

    @Node("text()")
    public void setRegex(String regex) {
        this.regex = regex;
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
