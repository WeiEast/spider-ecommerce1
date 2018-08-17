package com.datatrees.spider.ecommerce.api;

import java.util.List;

import com.datatrees.spider.share.domain.model.WebsiteConf;

public interface EconomicApi {

    List<WebsiteConf> getWebsiteConf(List<String> websiteNameList);
}
