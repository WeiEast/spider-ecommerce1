package com.datatrees.rawdatacentral.service;

import com.datatrees.spider.share.domain.model.Ecommerce;

/**
 * 电商配置
 * Created by zhouxinghai on 2017/6/29.
 */
public interface EcommerceService {

    /**
     * 根据websiteId获取
     * @param websiteId
     * @return
     */
    public Ecommerce getByWebsiteId(Integer websiteId);
}
