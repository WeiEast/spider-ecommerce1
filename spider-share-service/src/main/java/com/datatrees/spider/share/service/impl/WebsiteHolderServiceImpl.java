package com.datatrees.spider.share.service.impl;

import javax.annotation.Resource;
import java.util.Map;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.domain.model.WebsiteConf;
import com.datatrees.spider.share.domain.website.WebsiteConfig;
import com.datatrees.spider.share.service.WebsiteHolderService;
import com.datatrees.spider.share.service.website.WebsiteHolder;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class WebsiteHolderServiceImpl implements WebsiteHolderService {

    @Resource
    private ApplicationContext         context;

    private Map<String, WebsiteHolder> holderMap;

    @Override
    public Website getWebsite(String websiteName) {
        if (MapUtils.isEmpty(holderMap)) {
            holderMap = context.getBeansOfType(WebsiteHolder.class);
        }
        if (MapUtils.isEmpty(holderMap)) {
            return null;
        }
        for (Map.Entry<String, WebsiteHolder> holder : holderMap.entrySet()) {
            if (holder.getValue().support(websiteName)) {
                return holder.getValue().getWebsite(websiteName);
            }
        }
        return null;
    }

    @Override
    public WebsiteConfig getWebsiteConfig(String websiteName) {
        if (MapUtils.isEmpty(holderMap)) {
            holderMap = context.getBeansOfType(WebsiteHolder.class);
        }
        if (MapUtils.isEmpty(holderMap)) {
            return null;
        }
        for (Map.Entry<String, WebsiteHolder> holder : holderMap.entrySet()) {
            if (holder.getValue().support(websiteName)) {
                return holder.getValue().getWebsiteConfig(websiteName);
            }
        }
        return null;
    }

    @Override
    public WebsiteConf getWebsiteConf(String websiteName) {
        CheckUtils.checkNotNull(websiteName, "websiteName is null");
        WebsiteConf conf = null;
        WebsiteConfig config = getWebsiteConfig(websiteName);
        if (null != config) {
            conf = new WebsiteConf();
            conf.setSimulate(config.getSimulate());
            conf.setWebsiteName(config.getWebsiteName());
            conf.setWebsiteType(config.getWebsiteType());
            conf.setInitSetting(config.getInitSetting());
            conf.setLoginTip(config.getLoginTip());
            conf.setVerifyTip(config.getVerifyTip());
            conf.setResetTip(config.getResetTip());
            conf.setResetType(config.getResetType());
            conf.setResetURL(config.getResetURL());
            conf.setSmsReceiver(config.getSmsReceiver());
            conf.setSmsTemplate(config.getSmsTemplate());
        }
        return conf;
    }

}
