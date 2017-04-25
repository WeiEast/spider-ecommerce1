/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.service;

import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.rawdatacentral.core.model.Website;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午3:27:49
 */
public interface WebsiteService {
    public SearchProcessorContext getSearchProcessorContext(String websiteName);

    public ExtractorProcessorContext getExtractorProcessorContext(int websiteId);

    public ExtractorProcessorContext getExtractorProcessorContextWithBankId(int bankId);

    public Website getWebsiteByName(String websiteName);

    public Website getCachedWebsiteByID(int websiteId);

    public Website getCachedWebsiteByName(String websiteName);

    public int updateWebsiteConfig(Website website);

    public Website getWebsiteNoConfByName(String websiteName);

    public int insertWebsiteConfig(Website website);
    
    public int countWebsiteConfigByWebsiteId(int websiteId);


}
