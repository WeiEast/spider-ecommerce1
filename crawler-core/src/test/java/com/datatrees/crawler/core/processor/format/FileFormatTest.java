/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.format;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.format.impl.FileFormatImpl;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 31, 2014 2:51:31 PM
 */
public class FileFormatTest {

    @Test
    public void testFileFormat() {

        FileFormatImpl fileFormatImpl = new FileFormatImpl();
        fileFormatImpl.setConf(PropertiesConfiguration.getInstance());
        Request req = new Request();
        Website website = new Website();
        website.setSearchConfig(new SearchConfig());
        website.setWebsiteName("itheima.com");
        RequestUtil.setWebsite(req, website);
        RequestUtil.setProcessorContext(req, new SearchProcessorContext(website));
        System.out.println(fileFormatImpl.format(req, null, "http://bbs.itheima.com/thread-138641-1-1.html", ""));

    }

}
