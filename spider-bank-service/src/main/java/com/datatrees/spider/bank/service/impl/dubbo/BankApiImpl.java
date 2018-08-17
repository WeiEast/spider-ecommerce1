package com.datatrees.spider.bank.service.impl.dubbo;

import javax.annotation.Resource;
import java.util.List;

import com.datatrees.spider.bank.api.BankApi;
import com.datatrees.spider.share.domain.model.WebsiteConf;
import com.datatrees.spider.share.service.WebsiteConfigService;
import org.springframework.stereotype.Service;

@Service
public class BankApiImpl implements BankApi {

    @Resource
    private WebsiteConfigService websiteConfigService;

    @Override
    public List<WebsiteConf> getWebsiteConf(List<String> websiteNameList) {
        return websiteConfigService.getWebsiteConf(websiteNameList);
    }
}
