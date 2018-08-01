package com.datatrees.rawdatacentral.service;

import com.datatrees.spider.share.domain.model.WebsiteInfo;

/**
 * Created by zhangyanjia on 2018/3/20.
 */
public interface WebsiteInfoService {

    /**
     * 根据环境和站点名获取运营商配置
     * @param websiteName
     * @return
     */
    WebsiteInfo getByWebsiteName(String websiteName);
}
