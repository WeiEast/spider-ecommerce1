/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.ExtractOperation;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.html.HTMLParser;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.treefinance.crawler.framework.util.UrlExtractor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * handle codec operation decode/encode etc..
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 27, 2014 12:30:43 PM
 */
public class ExtractOperationImpl extends Operation<ExtractOperation> {

    public ExtractOperationImpl(@Nonnull ExtractOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    private String extractHtmlLink(String content, String baseURL) {
        // parser url from html
        HTMLParser htmlParser = new HTMLParser();
        htmlParser.parse(content, baseURL);
        if (MapUtils.isNotEmpty(htmlParser.getLinks())) {
            Iterator<Entry<String, String>> urlIterator = htmlParser.getLinks().entrySet().iterator();
            Entry<String, String> fl = urlIterator.next();
            return fl.getKey();
        } else {
            return null;
        }
    }

    @Override
    protected void doOperation(@Nonnull ExtractOperation operation, @Nonnull Object operatingData, @Nonnull Request request, @Nonnull Response response) throws Exception {
        // get input
        String content = (String) operatingData;
        String baseURL = RequestUtil.getCurrentUrl(request).getUrl();
        String url = this.extractHtmlLink(content, baseURL);
        if (StringUtils.isBlank(url)) {
            List<String> textUrls = UrlExtractor.extract(content);
            url = CollectionUtils.isNotEmpty(textUrls) ? textUrls.get(0) : "";
        }

        logger.debug("Extracted result: {}, content: {}", url, content);

        response.setOutPut(url);
    }
}
