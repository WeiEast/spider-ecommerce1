/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.service.impl;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.CacheUtil;
import com.datatrees.crawler.core.domain.config.AbstractWebsiteConfig;
import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigParser;
import com.datatrees.crawler.core.util.xml.ParentConfigHandler;
import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.core.common.SimpleProxyManager;
import com.datatrees.rawdatacentral.dao.WebsiteDAO;
import com.datatrees.rawdatacentral.core.service.WebsiteService;
import com.datatrees.rawdatacentral.domain.common.Website;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.service.BankService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 下午5:15:50
 */
@Service
public class WebsiteServiceImpl implements WebsiteService {
    private static final Logger log                = LoggerFactory.getLogger(WebsiteServiceImpl.class);
    private boolean             testModeSwitch     = PropertiesConfiguration.getInstance()
        .getBoolean("test.mode.switch", false);
    private String              localConfigPath    = PropertiesConfiguration.getInstance().get("local.config.path", "");

    @Resource
    private WebsiteDAO          websiteDAO;

    @Resource
    private BankService         bankService;
    @Resource
    private PluginManager       pluginManager;

    private long                websiteExpiredTime = PropertiesConfiguration.getInstance()
        .getInt("website.expired.minute", 30) * 60 * 1000;

    class WebsiteParentConfigHandler implements ParentConfigHandler {

        @Override
        public <T> T parse(T type) throws Exception {
            if (type != null && type instanceof AbstractWebsiteConfig
                && StringUtils.isNotBlank(((AbstractWebsiteConfig) type).getParentWebsiteName())) {
                String parentWebsiteName = ((AbstractWebsiteConfig) type).getParentWebsiteName();
                log.info("do parentConfigHandler for parentWebsiteName named: " + parentWebsiteName + " for class "
                         + type.getClass());
                Website website = getCachedWebsiteByName(parentWebsiteName);
                if (website != null) {
                    if (type instanceof SearchConfig) {
                        ((SearchConfig) type).clone(website.getSearchConfig());
                    } else if (type instanceof ExtractorConfig) {
                        ((ExtractorConfig) type).clone(website.getExtractorConfig());
                    }
                }
            }
            return type;
        }
    }

    private WebsiteParentConfigHandler parentConfigHandler = new WebsiteParentConfigHandler();

    private void testModeHandle(Website website) {
        if (website != null && testModeSwitch) {
            File configFile = new File(localConfigPath + website.getWebsiteName() + "_SearchConfig.xml");
            if (configFile.exists()) {
                try {
                    website.setSearchConfigSource(FileUtils.readFileToString(configFile));
                    log.info("load local search config for " + website.getWebsiteName());
                } catch (IOException e) {
                    log.error("load local config error " + e.getMessage(), e);
                }
            }
            configFile = new File(localConfigPath + website.getWebsiteName() + "_ExtractorConfig.xml");
            if (configFile.exists()) {
                try {
                    website.setExtractorConfigSource(FileUtils.readFileToString(configFile));
                    log.info("load local extract config for " + website.getWebsiteName());
                } catch (IOException e) {
                    log.error("load local config error " + e.getMessage(), e);
                }
            }
        }
    }

    public Website getCachedWebsiteByID(int id) {
        Website website = (Website) CacheUtil.INSTANCE.getObject(Constants.WEBSITE_CONTEXT_ID_PREFIX + id,
            websiteExpiredTime);
        if (website == null) {
            website = websiteDAO.getWebsiteById(id);
            this.testModeHandle(website);
            if (website != null) {
                website = this.websiteContextBuild(website);
                CacheUtil.INSTANCE.insertObject(Constants.WEBSITE_CONTEXT_ID_PREFIX + website.getId(), website);
                CacheUtil.INSTANCE.insertObject(Constants.WEBSITE_CONTEXT_NAME_PREFIX + website.getWebsiteName(),
                    website);
            }
        }
        return website;
    }

    public Website getCachedWebsiteByName(String websiteName) {
        Website website = (Website) CacheUtil.INSTANCE.getObject(Constants.WEBSITE_CONTEXT_NAME_PREFIX + websiteName,
            websiteExpiredTime);
        if (website == null) {
            log.info("getCachedWebsiteByName not cache found websiteName={}", websiteName);
            website = websiteDAO.getWebsiteByName(websiteName);
            if (null == website) {
                log.error("getCachedWebsiteByName error website not found from db websiteName={}", websiteName);
                throw new RuntimeException("website not found websiteName=" + websiteName);
            }
            this.testModeHandle(website);
            if (website != null) {
                website = this.websiteContextBuild(website);
                CacheUtil.INSTANCE.insertObject(Constants.WEBSITE_CONTEXT_ID_PREFIX + website.getId(), website);
                CacheUtil.INSTANCE.insertObject(Constants.WEBSITE_CONTEXT_NAME_PREFIX + website.getWebsiteName(),
                    website);
            }
        }
        if (null == website) {
            log.error("getCachedWebsiteByName error website not found websiteName={}", websiteName);
            throw new RuntimeException("website not found websiteName=" + websiteName);
        }
        return website;
    }

    public Website getWebsiteByName(String websiteName) {
        if (StringUtils.isBlank(websiteName)) {
            return null;
        }
        Website website = websiteDAO.getWebsiteByName(websiteName);
        if (website != null) {
            this.testModeHandle(website);
            website = this.websiteContextBuild(website);
            CacheUtil.INSTANCE.insertObject(Constants.WEBSITE_CONTEXT_ID_PREFIX + website.getId(), website);
            CacheUtil.INSTANCE.insertObject(Constants.WEBSITE_CONTEXT_NAME_PREFIX + website.getWebsiteName(), website);
        }
        return website;
    }

    private Website websiteContextBuild(Website website) {
        if (website != null) {
            if (StringUtils.isNotEmpty(website.getSearchConfigSource())) {
                try {
                    SearchConfig searchConfig = XmlConfigParser.getInstance().parse(website.getSearchConfigSource(),
                        SearchConfig.class, parentConfigHandler);
                    website.setSearchConfig(searchConfig);
                    website.setSearchConfigSource(null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (StringUtils.isNotEmpty(website.getExtractorConfigSource())) {
                try {
                    ExtractorConfig extractorConfig = XmlConfigParser.getInstance()
                        .parse(website.getExtractorConfigSource(), ExtractorConfig.class, parentConfigHandler);
                    website.setExtractorConfig(extractorConfig);
                    website.setExtractorConfigSource(null);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return website;
    }

    @Override
    public SearchProcessorContext getSearchProcessorContext(String websiteName) {
        Website website = this.getCachedWebsiteByName(websiteName);
        if (website != null) {
            SearchProcessorContext searchProcessorContext = new SearchProcessorContext(website);
            searchProcessorContext.setPluginManager(pluginManager);
            // not support yet
            searchProcessorContext.setProxyManager(new SimpleProxyManager());
            // searchProcessorContext.setWebServiceUrl("http://localhost:8080");
            // searchProcessorContext.setLoginResource(new SimpleLoginResource());
            searchProcessorContext.init();
            return searchProcessorContext;
        }
        return null;
    }

    @Override
    public ExtractorProcessorContext getExtractorProcessorContext(int websiteId) {
        Website website = this.getCachedWebsiteByID(websiteId);
        if (website != null) {
            ExtractorProcessorContext extractorProcessorContext = new ExtractorProcessorContext(website);
            extractorProcessorContext.setPluginManager(pluginManager);
            extractorProcessorContext.init();
            return extractorProcessorContext;
        }
        return null;
    }

    @Override
    public ExtractorProcessorContext getExtractorProcessorContextWithBankId(int bankId) {
        Bank bank = bankService.getByBankIdFromCache(bankId);
        if (bank != null) {
            return this.getExtractorProcessorContext(bank.getWebsiteId());
        }
        return null;
    }

    @Override
    public int updateWebsiteConfig(Website website) {
        return websiteDAO.updateWebsiteConfig(website);
    }

    @Override
    public Website getWebsiteNoConfByName(String websiteName) {
        return websiteDAO.getWebsiteNoConfByName(websiteName);
    }

    @Override
    public int insertWebsiteConfig(Website website) {
        return websiteDAO.insertWebsiteConfig(website);
    }

    @Override
    public int countWebsiteConfigByWebsiteId(int websiteId) {
        return websiteDAO.countWebsiteConfigByWebsiteId(websiteId);
    }

}
