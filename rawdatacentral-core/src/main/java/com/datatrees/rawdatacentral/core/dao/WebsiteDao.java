/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.dao;

import com.datatrees.rawdatacentral.domain.common.Website;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年6月9日 下午11:28:15
 */
public interface WebsiteDao {
    public Website getWebsiteByName(String websiteName);

    public Website getWebsiteById(int id);

    public int updateWebsiteConfig(Website website);

    public Website getWebsiteNoConfByName(String websiteName);

    public int insertWebsiteConfig(Website website);

    public int countWebsiteConfigByWebsiteId(int websiteId);
}
