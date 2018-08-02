/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.bean;

import java.util.ArrayList;
import java.util.List;

import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.ResponseUtil;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 1:48:37 PM
 */
public class CrawlResponse extends Response {

    private CrawlResponse() {
        super();
        setStatus(1);
    }

    public static CrawlResponse build() {
        return new CrawlResponse();
    }

    public List<LinkNode> getUrls() {
        List<LinkNode> links = ResponseUtil.getResponseLinkNodes(this);
        if (links == null) {
            links = new ArrayList<LinkNode>();
            setUrls(links);
        }
        return links;
    }

    public CrawlResponse setUrls(List<LinkNode> urls) {
        ResponseUtil.setResponseLinkNodes(this, urls);
        return this;
    }

    public CrawlResponse addUrl(LinkNode url) {
        List<LinkNode> links = getUrls();
        links.add(url);
        return this;
    }

    public int getStatus() {
        return ResponseUtil.getResponseStatus(this);
    }

    public CrawlResponse setStatus(int status) {
        ResponseUtil.setResponseStatus(this, status);
        return this;
    }

    public String getErrorMsg() {
        return ResponseUtil.getResponseErrorMsg(this);
    }

    public CrawlResponse setErrorMsg(String errorMsg) {
        ResponseUtil.setResponseErrorMsg(this, errorMsg);
        return this;
    }

    public String info() {
        StringBuilder info = new StringBuilder();
        info.append("status:").append(getStatus()).append("\n").append("error info:").append(getErrorMsg()).append("\n").append("urls size:")
                .append(getUrls().size());
        return info.toString();
    }

}
