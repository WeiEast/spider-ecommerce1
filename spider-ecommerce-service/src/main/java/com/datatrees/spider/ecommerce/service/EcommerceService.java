package com.datatrees.spider.ecommerce.service;

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
    Ecommerce getByWebsiteId(Integer websiteId);
}
