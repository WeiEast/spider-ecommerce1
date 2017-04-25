package com.datatrees.rawdatacentral.core.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.databoss.api.client.watcher.AbstractPluginEventWatcher;
import com.datatrees.rawdatacentral.core.service.WebsiteService;

@Service
public class DefaultPluginEventWatcher extends AbstractPluginEventWatcher {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPluginEventWatcher.class);
    @Resource
    private WebsiteService websiteService;
    @Resource
    private ZooKeeperClient zooKeeperClient;

    @Override
    public List<AbstractPlugin> getAllPlugins(String websiteName) {
        List<AbstractPlugin> resultList = new ArrayList<AbstractPlugin>();
        if (StringUtils.isBlank(websiteName)) {
            logger.error("get plugin ids error! websiteName: " + websiteName);
            return resultList;
        }
        Website website = websiteService.getWebsiteByName(websiteName);
        if (website != null) {
            SearchConfig searchConfig = website.getSearchConfig();
            if (searchConfig != null) {
                resultList.addAll(searchConfig.getPluginList());
            }
            ExtractorConfig extractorConfig = website.getExtractorConfig();
            if (extractorConfig != null) {
                resultList.addAll(extractorConfig.getPluginList());
            }
        }
        return resultList;
    }

    @PostConstruct
    public void register() {
        zooKeeperClient.registerWatcher(this);
        init();
    }
}
