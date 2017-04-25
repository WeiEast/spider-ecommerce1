/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.api;

import java.util.Date;
import java.util.List;

import com.datatrees.rawdatacentral.api.model.StatusCode;
import com.datatrees.rawdatacentral.api.model.TaskStatus;
import com.datatrees.rawdatacentral.api.model.WebsiteConf;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月5日 上午10:41:22
 */
public interface CrawlerService {

    // 未来可以增加每个网页显示什么字段，给什么提示，有多少tab，点击每个tab访问什么连接的配置
    public WebsiteConf getWebsiteConf(String websiteName);

    // 未来可以增加每个网页显示什么字段，给什么提示，有多少tab，点击每个tab访问什么连接的配置
    public List<WebsiteConf> getWebsiteConf(List<String> websiteNameList);

    // 修改配置
    public boolean updateWebsiteConfig(String websiteName, String searchConfigSource, String extractConfigSource);

    public Date getSystemTime();
    
    public int initLogin(int userid, String websiteName, Date initDate);

    public boolean importStatus(int userid, String websiteName, StatusCode status, String attrJson);

    public List<TaskStatus> fetchStatus(int userid, String websiteName, int sequenceId, Integer statusId);

}
