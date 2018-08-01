package com.datatrees.spider.share.service.impl;

import javax.annotation.Resource;
import java.util.Map;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.share.service.WebsiteHolder;
import com.datatrees.spider.share.service.WebsiteHolderService;
import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class WebsiteHolderServiceImpl implements WebsiteHolderService {

    @Resource
    private ApplicationContext         context;

    private Map<String, WebsiteHolder> holderMap;

    @Override
    public Website getWebsite(long taskId, String websiteName) {
        if (MapUtils.isEmpty(holderMap)) {
            holderMap = context.getBeansOfType(WebsiteHolder.class);
        }
        if (MapUtils.isEmpty(holderMap)) {
            return null;
        }
        for (Map.Entry<String, WebsiteHolder> holder : holderMap.entrySet()) {
            if (holder.getValue().support(taskId, websiteName)) {
                return holder.getValue().getWebsite(taskId, websiteName);
            }
        }
        return null;
    }

}
