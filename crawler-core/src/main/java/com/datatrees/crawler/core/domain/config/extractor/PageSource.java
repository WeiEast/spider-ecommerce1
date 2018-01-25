/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.page.Regexp;
import com.datatrees.crawler.core.domain.config.page.Replacement;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 上午11:54:09
 */
@Tag("source")
public class PageSource {

    private String         field;
    private AbstractPlugin plugin;
    private List<Replacement> replacements = new ArrayList<>();
    private Regexp regexp;
    private String separator;

    public PageSource() {
        super();
    }

    @Attr(value = "plugin-ref", referenced = true)
    public AbstractPlugin getPlugin() {
        return plugin;
    }

    @Node(value = "@plugin-ref", referenced = true)
    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Attr("separator")
    public String getSeparator() {
        return separator;
    }

    @Node("@separator")
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Attr("split")
    public String getSplit() {
        return getSeparator();
    }

    @Node("@split")
    public void setSplit(String split) {
        setSeparator(split);
    }

    @Attr("field")
    public String getField() {
        return field;
    }

    @Node("@field")
    public void setField(String field) {
        this.field = field;
    }

    @Tag("replaces")
    public List<Replacement> getReplacements() {
        return Collections.unmodifiableList(replacements);
    }

    @Node("replaces/replace")
    public void setReplacements(Replacement replacement) {
        this.replacements.add(replacement);
    }

    @Tag("regex")
    public Regexp getRegexp() {
        return regexp;
    }

    @Node("regex")
    public void setRegexp(Regexp regexp) {
        this.regexp = regexp;
    }
}
