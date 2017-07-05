package com.datatrees.rawdatacentral.service.impl;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.AbstractWebsiteConfig;
import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigParser;
import com.datatrees.crawler.core.util.xml.ParentConfigHandler;
import com.datatrees.databoss.api.client.common.SimpleProxyManager;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.dao.WebsiteConfigDAO;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.service.BankService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by zhouxinghai on 2017/6/30.
 */
@Service
public class WebsiteConfigServiceImpl implements WebsiteConfigService {

    private static final Logger        logger              = LoggerFactory.getLogger(WebsiteConfigServiceImpl.class);

    @Resource
    private WebsiteConfigDAO           websiteConfigDAO;

    @Resource
    private PluginManager              pluginManager;

    @Resource
    private BankService                bankService;

    @Resource
    private RedisService               redisService;

    private WebsiteParentConfigHandler parentConfigHandler = new WebsiteParentConfigHandler();

    @Override
    public WebsiteConfig getWebsiteConfigByWebsiteId(Integer websiteId) {
        CheckUtils.checkNotNull(websiteId, "websiteId is null");
        WebsiteConfig config = websiteConfigDAO.getWebsiteConfig(websiteId, null);
        if (null == config) {
            logger.warn("WebsiteConfig not found websiteId={}", websiteId);
        }
        return config;
    }

    @Override
    public WebsiteConfig getWebsiteConfigByWebsiteName(String websiteName) {
        CheckUtils.checkNotNull(websiteName, "websiteName is null");
        WebsiteConfig config = websiteConfigDAO.getWebsiteConfig(null, websiteName);
        if (null == config) {
            logger.warn("WebsiteConfig not found websiteId={}", websiteName);
        }
        return config;
    }

    @Override
    public Website getWebsiteByWebsiteId(Integer websiteId) {
        WebsiteConfig config = getWebsiteConfigByWebsiteId(websiteId);
        if (null != config) {
            return getFromWebsiteConfig(config);
        }
        return null;
    }

    @Override
    public Website getWebsiteByWebsiteName(String websiteName) {
        WebsiteConfig config = getWebsiteConfigByWebsiteName(websiteName);
        if (null != config) {
            return getFromWebsiteConfig(config);
        }
        return null;
    }

    @Override
    public Website getFromWebsiteConfig(WebsiteConfig websiteConfig) {
        CheckUtils.checkNotNull(websiteConfig, "websiteConfig is null");
        Website website = websiteContextBuild(websiteConfig);
        website.setId(websiteConfig.getWebsiteId());
        website.setIsEnabled(websiteConfig.getIsenabled());
        website.setWebsiteName(websiteConfig.getWebsiteName());
        website.setWebsiteType(websiteConfig.getWebsiteType());
        website.setSearchConfigSource(websiteConfig.getSearchConfig());
        website.setExtractorConfigSource(websiteConfig.getExtractorConfig());
        return website;
    }

    @Override
    public WebsiteConf getWebsiteConf(String websiteName) {
        CheckUtils.checkNotNull(websiteName, "websiteName is null");
        WebsiteConf conf = null;
        WebsiteConfig config = getWebsiteConfigByWebsiteName(websiteName);
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

    @Override
    public WebsiteConf getWebsiteConfFromCache(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, "websiteName is blank");
        WebsiteConf conf = redisService.getCache(RedisKeyPrefixEnum.WEBSITE_CONF_WEBSITENAME, websiteName,
            WebsiteConf.class);
        if (null != conf) {
            logger.info("find WebsiteConf from cache websiteName={}", websiteName);
            return conf;
        }
        conf = getWebsiteConf(websiteName);
        if (null != conf) {
            logger.info("find WebsiteConf from db websiteName={}", websiteName);
            redisService.cache(RedisKeyPrefixEnum.WEBSITE_CONF_WEBSITENAME, websiteName, conf);
            return conf;
        }
        logger.info("conf not found from db websiteName={}", websiteName);
        return null;
    }

    @Override
    public boolean updateWebsiteConf(String websiteName, String searchConfig, String extractConfig) {
        CheckUtils.checkNotBlank(websiteName, "websiteName is blank");
        CheckUtils.checkNotBlank(searchConfig, "searchConfig is blank");
        CheckUtils.checkNotBlank(extractConfig, "extractConfig is blank");

        WebsiteConfig websiteConfig = getWebsiteConfigByWebsiteName(websiteName);
        CheckUtils.checkNotNull(websiteConfig, "website not found websiteName=" + websiteName);
        com.datatrees.rawdatacentral.domain.model2.WebsiteConf confUpdate = new com.datatrees.rawdatacentral.domain.model2.WebsiteConf();
        confUpdate.setWebsiteConfId(websiteConfig.getWebsiteConfId());
        confUpdate.setExtractorConfig(extractConfig);
        confUpdate.setSearchConfig(searchConfig);
        confUpdate.setUpdatedAt(new Date());
        int i = websiteConfigDAO.updateWebsiteConf(websiteConfig.getWebsiteId(), searchConfig, extractConfig);
        if (i == 0) {
            logger.warn("updateWebsiteConf error websiteName={}", websiteName);
            return false;
        }
        logger.info("updateWebsiteConf success websiteName={}", websiteName);
        return true;
    }

    @Override
    public SearchProcessorContext getSearchProcessorContext(String websiteName) {
        Website website = this.getWebsiteByWebsiteName(websiteName);
        if (website != null) {
            SearchProcessorContext searchProcessorContext = new SearchProcessorContext(website);
            searchProcessorContext.setPluginManager(pluginManager);
            searchProcessorContext.setProxyManager(new SimpleProxyManager());
            searchProcessorContext.init();
            return searchProcessorContext;
        }
        return null;
    }

    @Override
    public ExtractorProcessorContext getExtractorProcessorContext(int websiteId) {
        Website website = this.getWebsiteByWebsiteId(websiteId);
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

    private Website websiteContextBuild(WebsiteConfig websiteConfig) {
        Website website = new Website();
        if (websiteConfig != null) {
            if (StringUtils.isNotEmpty(websiteConfig.getSearchConfig())) {
                try {
                    SearchConfig searchConfig = XmlConfigParser.getInstance().parse(websiteConfig.getSearchConfig(),
                        SearchConfig.class, parentConfigHandler);
                    website.setSearchConfig(searchConfig);
                    website.setSearchConfigSource(null);
                } catch (Exception e) {
                    logger.error("parse searchConfig  error websiteId={},websiteName={}", websiteConfig.getWebsiteId(),
                        websiteConfig.getWebsiteName(), e);
                }
            }
            if (StringUtils.isNotEmpty(websiteConfig.getExtractorConfig())) {
                try {
                    ExtractorConfig extractorConfig = XmlConfigParser.getInstance()
                        .parse(websiteConfig.getExtractorConfig(), ExtractorConfig.class, parentConfigHandler);
                    website.setExtractorConfig(extractorConfig);
                    website.setExtractorConfigSource(null);
                } catch (Exception e) {
                    logger.error("parse extractorConfig  error websiteId={},websiteName={}",
                        websiteConfig.getWebsiteId(), websiteConfig.getWebsiteName(), e);
                }
            }
        }
        return website;
    }

    class WebsiteParentConfigHandler implements ParentConfigHandler {

        @Override
        public <T> T parse(T type) throws Exception {
            if (type != null && type instanceof AbstractWebsiteConfig
                && StringUtils.isNotBlank(((AbstractWebsiteConfig) type).getParentWebsiteName())) {
                String parentWebsiteName = ((AbstractWebsiteConfig) type).getParentWebsiteName();
                logger.info("do parentConfigHandler for parentWebsiteName named: " + parentWebsiteName + " for class "
                            + type.getClass());
                Website website = getWebsiteByWebsiteName(parentWebsiteName);
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

}
