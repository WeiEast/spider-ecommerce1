/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.dao;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;

/**
 *
 * Created by zhouxinghai on 2017/6/29
 */
@Resource
public interface MyWebsiteDAO {

    public WebsiteConfig getWebsiteByName(String websiteName);

    public WebsiteConfig getWebsiteById(int id);

    public int updateWebsiteConfig(WebsiteConfig website);

    public WebsiteConfig getWebsiteNoConfByName(String websiteName);

    public int insertWebsiteConfig(WebsiteConfig website);

    public int countWebsiteConfigByWebsiteId(int websiteId);
}
