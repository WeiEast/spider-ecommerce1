/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.segment.AbstractSegment;
import com.datatrees.crawler.core.domain.config.segment.impl.*;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.ChildTag;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 下午2:06:42
 */
public class LoginCheckConfig {

    private String                successPattern;

    private String                failPattern;

    private Integer               checkInterval;// unit m

    private String                checkUrl;// check url

    private String                headers;

    private List<AbstractSegment> segmentList;

    /**
     *
     */
    public LoginCheckConfig() {
        super();
        segmentList = new ArrayList<AbstractSegment>();
    }

    @ChildTag("object-segment")
    public List<AbstractSegment> getSegmentList() {
        return Collections.unmodifiableList(segmentList);
    }

    @Node(value = "object-segment", types = {XpathSegment.class, JsonPathSegment.class, RegexSegment.class, SplitSegment.class,
            CalculateSegment.class, BaseSegment.class})
    public void setSegmentList(AbstractSegment segment) {
        this.segmentList.add(segment);
    }

    @Attr("success-pattern")
    public String getSuccessPattern() {
        return successPattern;
    }

    @Node("@success-pattern")
    public void setSuccessPattern(String successPattern) {
        this.successPattern = successPattern;
    }

    @Attr("fail-pattern")
    public String getFailPattern() {
        return failPattern;
    }

    @Node("@fail-pattern")
    public void setFailPattern(String failPattern) {
        this.failPattern = failPattern;
    }

    @Attr("check-interval")
    public Integer getCheckInterval() {
        return checkInterval;
    }

    @Node("@check-interval")
    public void setCheckInterval(Integer checkInterval) {
        this.checkInterval = checkInterval;
    }

    @Tag("check-url")
    public String getCheckUrl() {
        return checkUrl;
    }

    @Node("check-url/text()")
    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }

    @Tag("headers")
    public String getHeaders() {
        return headers;
    }

    @Node("headers/text()")
    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
