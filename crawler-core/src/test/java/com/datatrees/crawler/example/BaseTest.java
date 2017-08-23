/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.example;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import com.datatrees.common.util.ResourceUtil;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigParser;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 11:27:49 AM
 */
public abstract class BaseTest {

    protected static SearchProcessorContext getSearchProcessorContext(String website, String fileName) throws ParseException {
        Objects.requireNonNull(fileName);
        SearchConfig websiteConfig = getConfig(fileName, SearchConfig.class);

        Website web = new Website();
        web.setWebsiteName(website);
        web.setWebsiteDomain(website);
        web.setSearchConfig(websiteConfig);

        SearchProcessorContext context = new SearchProcessorContext(web);

        context.init();

        return context;
    }

    protected static ExtractorProcessorContext createExtractorProcessorContext(String website, String fileName) throws ParseException {
        Objects.requireNonNull(fileName);
        ExtractorConfig extractorConfig = getConfig(fileName, ExtractorConfig.class);

        Website web = new Website();
        web.setWebsiteName(website);
        web.setWebsiteDomain(website);
        web.setExtractorConfig(extractorConfig);

        ExtractorProcessorContext context = new ExtractorProcessorContext(web);
        context.init();
        return context;
    }

    private static <T> T getConfig(String filename, Class<T> configClass) throws ParseException {
        String content = ResourceUtil.getContent(filename, null);

        return XmlConfigParser.getInstance().parse(content, configClass);
    }

    protected static String getContent(String fileName) {
        return ResourceUtil.getContent(fileName, null);
    }

    protected File getResource(String path) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(path);

        Objects.requireNonNull(resource);

        return new File(resource.toURI());
    }
}
