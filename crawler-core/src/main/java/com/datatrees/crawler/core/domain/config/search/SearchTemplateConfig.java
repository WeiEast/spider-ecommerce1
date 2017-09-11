/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.ChildTag;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import com.datatrees.crawler.core.util.xml.definition.AbstractBeanDefinition;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 5:05:45 PM
 */
@Tag("search-template")
public class SearchTemplateConfig extends AbstractBeanDefinition implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7796504114576595157L;
    private SearchType               type;
    private Integer                  maxDepth;
    private Request                  request;
    private List<SearchSequenceUnit> searchSequence;
    private AbstractPlugin           plugin;
    private List<String>             resultTagList;
    private Boolean                  autoStart;
    private Integer                  threadCount;
    private Integer                  waitIntervalMillis;

    public SearchTemplateConfig() {
        super();
        searchSequence = new ArrayList<SearchSequenceUnit>();
        resultTagList = new ArrayList<String>();
    }

    @ChildTag("result-tag-list/result-tag")
    public List<String> getResultTagList() {
        return Collections.unmodifiableList(resultTagList);
    }

    @Node("result-tag-list/result-tag/text()")
    public void setResultTagList(String resultTag) {
        this.resultTagList.add(resultTag);
    }

    @Attr("thread-count")
    public Integer getThreadCount() {
        return threadCount;
    }

    @Node("@thread-count")
    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    @Attr("auto-start")
    public Boolean getAutoStart() {
        return autoStart == null ? true : autoStart;

    }

    @Node("@auto-start")
    public void setAutoStart(Boolean autoStart) {
        this.autoStart = autoStart;
    }

    @Attr("type")
    public SearchType getType() {
        return type;
    }

    @Node("@type")
    public void setType(String type) {
        this.type = SearchType.getSearchType(type);
    }

    @Attr("max-depth")
    public Integer getMaxDepth() {
        return maxDepth;
    }

    @Node("@max-depth")
    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Tag("page-sequence")
    public List<SearchSequenceUnit> getSearchSequence() {
        return Collections.unmodifiableList(searchSequence);
    }

    @Node("page-sequence/page")
    public void setSearchSequence(SearchSequenceUnit searchSequenceUnit) {
        this.searchSequence.add(searchSequenceUnit);
    }

    @Tag("request")
    public Request getRequest() {
        return request;
    }

    @Node("request")
    public void setRequest(Request request) {
        this.request = request;
    }

    @Attr(value = "plugin-ref", referenced = true)
    public AbstractPlugin getPlugin() {
        return plugin;
    }

    @Node(value = "@plugin-ref", referenced = true)
    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Attr("wait-interval")
    public Integer getWaitIntervalMillis() {
        return waitIntervalMillis;
    }

    @Node("@wait-interval")
    public void setWaitIntervalMillis(Integer waitIntervalMillis) {
        this.waitIntervalMillis = waitIntervalMillis;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SearchTemplateConfig [id=" + getId() + " ,type=" + type + ", maxDepth=" + maxDepth + ", autoStart=" + autoStart + "]";
    }

}
