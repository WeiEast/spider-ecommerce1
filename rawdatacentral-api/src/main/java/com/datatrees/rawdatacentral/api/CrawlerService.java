/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.api;


import java.util.List;

import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
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

    /*
     * APP向rawdata传入短信验证码、图片验证码等状态 type 0:短信验证码 1:图片验证码 
     * 
     */
    public HttpResult<String> importStatus(long taskId, int type, String code);

    // APP模拟登录前获取或者刷新短信、二维码、验证码等 type 0:短信验证码 1:图片验证码 2:二维码
    public HttpResult<String> fetchStatus(long taskId, int type, String attrJson);

    // APP检测二维码扫描是否成功
    public HttpResult<Boolean> verifyQr(long taskId, String attrJson);


}
