/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com;

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
public abstract class BaseConfigTest {

    protected static SearchConfig getSearchConfig(String fileName) throws ParseException {
        String content = ResourceUtil.getContent(fileName, null);

        return XmlConfigParser.getInstance().parse(content, SearchConfig.class);
    }

    protected static SearchProcessorContext getProcessorContext(String fileName, String website) throws ParseException {
        Objects.requireNonNull(fileName);
        SearchConfig websiteConfig = getConfig(fileName, SearchConfig.class);

        Website web = new Website();
        web.setWebsiteName(website);
        web.setWebsiteDomain(website);
        web.setSearchConfig(websiteConfig);

        SearchProcessorContext context = new SearchProcessorContext(web,0L);

        context.init();

        return context;
    }

    protected static ExtractorProcessorContext getExtractorProcessorContext(String fileName, String website) throws ParseException {
        Objects.requireNonNull(fileName);
        ExtractorConfig extractorConfig = getConfig(fileName, ExtractorConfig.class);

        Website web = new Website();
        web.setWebsiteName(website);
        web.setWebsiteDomain(website);
        web.setExtractorConfig(extractorConfig);

        ExtractorProcessorContext context = new ExtractorProcessorContext(web,0L);
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
