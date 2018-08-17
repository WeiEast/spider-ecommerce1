package com.datatrees.spider.bank.api;

import java.util.List;

import com.datatrees.spider.share.domain.model.WebsiteConf;

public interface BankApi {

    List<WebsiteConf> getWebsiteConf(List<String> websiteNameList);
}
