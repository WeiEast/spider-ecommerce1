package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.Date;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.AbstractWebsiteConfig;
import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigParser;
import com.datatrees.crawler.core.util.xml.ParentConfigHandler;
import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.WebsiteUtils;
import com.datatrees.rawdatacentral.dao.WebsiteInfoDAO;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoWithBLOBs;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;
import com.datatrees.rawdatacentral.service.BankService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.service.WebsiteInfoService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import com.datatrees.rawdatacentral.service.proxy.SimpleProxyManager;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.treefinance.crawler.framework.extension.manager.PluginManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/6/30.
 */
@Service
public class WebsiteConfigServiceImpl implements WebsiteConfigService {

    private static final Logger                 logger = LoggerFactory.getLogger(WebsiteConfigServiceImpl.class);

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

    @Resource
    private              WebsiteInfoService     websiteInfoService;

    @Resource
    private              WebsiteInfoDAO         websiteInfoDAO;

    @Resource
    private              PluginManager          pluginManager;

    @Resource
    private              BankService            bankService;

    @Resource
    private              RedisService           redisService;

    @Resource
    private              ProxyService           proxyService;

    private              ParentConfigHandler    parentConfigHandler;

    public WebsiteConfigServiceImpl() {
        parentConfigHandler = new ParentConfigHandler() {
            @Override
            public <T> T parse(T type) throws Exception {
                if (type instanceof AbstractWebsiteConfig && StringUtils.isNotBlank(((AbstractWebsiteConfig) type).getParentWebsiteName())) {
                    String parentWebsiteName = ((AbstractWebsiteConfig) type).getParentWebsiteName();
                    logger.info("do parentConfigHandler for parentWebsiteName named: " + parentWebsiteName + " for class " + type.getClass());
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
        };
    }

    @Override
    public WebsiteConfig getWebsiteConfigByWebsiteId(Integer websiteId) {
        CheckUtils.checkNotNull(websiteId, "websiteId is null");
        WebsiteInfoWithBLOBs websiteInfo = websiteInfoDAO.selectByPrimaryKey(websiteId);
        if (null == websiteInfo) {
            logger.warn("WebsiteConfig not found websiteId={}", websiteId);
            return null;
        }
        return buildWebsiteConfigFromWebsiteInfo(websiteInfo);
    }

    @Override
    public WebsiteConfig getWebsiteConfigByWebsiteName(String websiteName) {
        CheckUtils.checkNotNull(websiteName, "websiteName is null");
        WebsiteInfoWithBLOBs websiteInfo = websiteInfoService.getByWebsiteNameFromInfo(websiteName);
        if (null == websiteInfo) {
            logger.warn("WebsiteConfig not found websiteId={}", websiteName);
            return null;
        }
        return buildWebsiteConfigFromWebsiteInfo(websiteInfo);
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
        return buildWebsite(websiteConfig);
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
    public boolean updateWebsiteConf(String websiteName, String searchConfig, String extractConfig) {
        CheckUtils.checkNotBlank(websiteName, "websiteName is blank");
        //        CheckUtils.checkNotBlank(searchConfig, "searchConfig is blank");
        CheckUtils.checkNotBlank(extractConfig, "extractConfig is blank");

        WebsiteConfig websiteConfig = getWebsiteConfigByWebsiteName(websiteName);
        CheckUtils.checkNotNull(websiteConfig, "website not found websiteName=" + websiteName);
        com.datatrees.rawdatacentral.domain.model2.WebsiteConf confUpdate = new com.datatrees.rawdatacentral.domain.model2.WebsiteConf();
        confUpdate.setWebsiteConfId(websiteConfig.getWebsiteConfId());
        confUpdate.setExtractorConfig(extractConfig);
        confUpdate.setSearchConfig(searchConfig);
        confUpdate.setUpdatedAt(new Date());
        WebsiteInfoWithBLOBs websiteInfo = new WebsiteInfoWithBLOBs();
        websiteInfo.setWebsiteId(websiteConfig.getWebsiteId());
        websiteInfo.setSearchConfig(searchConfig);
        websiteInfo.setExtractorConfig(extractConfig);
        websiteInfo.setUpdatedAt(new Date());
        int i = websiteInfoDAO.updateByPrimaryKeySelective(websiteInfo);
        if (i == 0) {
            logger.warn("updateWebsiteInfo error websiteName={}", websiteName);
            return false;
        }
        logger.info("updateWebsiteInfo success websiteName={}", websiteName);
        return true;
    }

    @Override
    public SearchProcessorContext getSearchProcessorContext(Long taskId) {
        Website website = getWebsiteFromCache(taskId);
        if (website != null) {
            SearchProcessorContext searchProcessorContext = new SearchProcessorContext(website, taskId);
            searchProcessorContext.setPluginManager(pluginManager);
            searchProcessorContext.setProxyManager(new SimpleProxyManager(taskId, website.getWebsiteName(), proxyService));
            searchProcessorContext.init();
            return searchProcessorContext;
        }
        return null;
    }

    @Override
    public SearchProcessorContext getSearchProcessorContext(Long taskId, String websiteName) {
        Website website;
        if (WebsiteUtils.isOperator(websiteName)) {
            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
            //保存taskId对应的website,因为运营过程中用的是
            website = buildWebsite(websiteOperator);
        } else {
            WebsiteInfoWithBLOBs websiteInfo = websiteInfoService.getByWebsiteNameFromInfo(websiteName);
            website = buildWebsiteFromWebsiteInfo(websiteInfo);
            //            website = getWebsiteByWebsiteName(websiteName);
        }
        if (website != null) {
            SearchProcessorContext searchProcessorContext = new SearchProcessorContext(website, taskId);
            searchProcessorContext.setPluginManager(pluginManager);
            searchProcessorContext.setProxyManager(new SimpleProxyManager(taskId, website.getWebsiteName(), proxyService));
            searchProcessorContext.init();
            logger.info("getSearchProcessorContext success,taskId={},websiteName={}", taskId, websiteName);
            return searchProcessorContext;
        }
        return null;
    }

    @Override
    public ExtractorProcessorContext getExtractorProcessorContext(Long taskId, String websiteName) {
        logger.info("getExtractorProcessorContext start,taskId={},websiteName={}", taskId, websiteName);
        Website website = null;
        if (WebsiteUtils.isOperator(websiteName)) {
            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
            //保存taskId对应的website,因为运营过程中用的是
            website = buildWebsite(websiteOperator);
        } else {
            WebsiteInfoWithBLOBs websiteInfo = websiteInfoService.getByWebsiteNameFromInfo(websiteName);
            website = buildWebsiteFromWebsiteInfo(websiteInfo);
            //            website = getWebsiteByWebsiteName(websiteName);
        }
        if (website != null) {
            ExtractorProcessorContext extractorProcessorContext = new ExtractorProcessorContext(website, taskId);
            extractorProcessorContext.setPluginManager(pluginManager);
            extractorProcessorContext.init();
            logger.info("getExtractorProcessorContext success,taskId={},websiteName={}", taskId, websiteName);
            return extractorProcessorContext;
        }
        return null;
    }

    @Override
    public ExtractorProcessorContext getExtractorProcessorContextWithBankId(int bankId, Long taskId) {
        Bank bank = bankService.getByBankIdFromCache(bankId);
        if (bank != null) {
            Website website = getWebsiteByWebsiteName(bank.getWebsiteName());
            if (website != null) {
                ExtractorProcessorContext extractorProcessorContext = new ExtractorProcessorContext(website, taskId);
                extractorProcessorContext.setPluginManager(pluginManager);
                extractorProcessorContext.init();
                return extractorProcessorContext;
            }
        }
        return null;
    }

    @Override
    public Website buildWebsite(WebsiteConfig websiteConfig) {
        if (websiteConfig == null) {
            return null;
        }
        Website website = new Website();
        if (StringUtils.isNotEmpty(websiteConfig.getSearchConfig())) {
            try {
                SearchConfig searchConfig = XmlConfigParser.getInstance()
                        .parse(websiteConfig.getSearchConfig(), SearchConfig.class, parentConfigHandler);
                website.setSearchConfig(searchConfig);
                website.setSearchConfigSource(websiteConfig.getSearchConfig());
            } catch (Exception e) {
                logger.error("parse searchConfig  error websiteId={},websiteName={}", websiteConfig.getWebsiteId(), websiteConfig.getWebsiteName(),
                        e);
            }
        }
        if (StringUtils.isNotEmpty(websiteConfig.getExtractorConfig())) {
            try {
                ExtractorConfig extractorConfig = XmlConfigParser.getInstance()
                        .parse(websiteConfig.getExtractorConfig(), ExtractorConfig.class, parentConfigHandler);
                website.setExtractorConfig(extractorConfig);
                website.setExtractorConfigSource(websiteConfig.getExtractorConfig());
            } catch (Exception e) {
                logger.error("parse extractorConfig  error websiteId={},websiteName={}", websiteConfig.getWebsiteId(), websiteConfig.getWebsiteName(),
                        e);
            }
        }
        website.setId(websiteConfig.getWebsiteId());
        website.setIsEnabled(websiteConfig.getIsenabled());
        website.setWebsiteName(websiteConfig.getWebsiteName());
        website.setWebsiteType(websiteConfig.getWebsiteType());
        website.setSearchConfigSource(websiteConfig.getSearchConfig());
        website.setExtractorConfigSource(websiteConfig.getExtractorConfig());
        if (StringUtils.isBlank(websiteConfig.getGroupCode())) {
            GroupEnum group = GroupEnum.getByWebsiteName(websiteConfig.getWebsiteName());
            if (null == group) {
                logger.warn("not found group code for webisteName={}", websiteConfig.getWebsiteName());
                //throw new RuntimeException("not found group code for webisteName=" + websiteConfig.getWebsiteName());
            } else {
                website.setGroupCode(group.getGroupCode());
                website.setGroupName(group.getGroupName());
                website.setWebsiteTitle(group.getGroupName());
            }
        } else {
            website.setGroupCode(websiteConfig.getGroupCode());
            website.setGroupName(websiteConfig.getGroupName());
            website.setWebsiteTitle(websiteConfig.getWebsiteTitle());
        }
        return website;
    }

    @Override
    public Website buildWebsite(WebsiteOperator websiteOperator) {
        WebsiteConfig config = buildWebsiteConfig(websiteOperator);
        return buildWebsite(config);
    }

    @Override
    public Website buildWebsiteFromWebsiteInfo(WebsiteInfoWithBLOBs websiteInfo) {
        WebsiteConfig config = buildWebsiteConfigFromWebsiteInfo(websiteInfo);
        return buildWebsite(config);
    }

    @Override
    public Website getWebsiteFromCache(Long taskId) {
        Website website = redisService.getCache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, new TypeReference<Website>() {});
        if (website != null) {
            if (StringUtils.isNotEmpty(website.getSearchConfigSource())) {
                try {
                    SearchConfig searchConfig = XmlConfigParser.getInstance()
                            .parse(website.getSearchConfigSource(), SearchConfig.class, parentConfigHandler);
                    website.setSearchConfig(searchConfig);
                } catch (Exception e) {
                    logger.error("parse searchConfig  error websiteId={},websiteName={}", website.getId(), website.getWebsiteName(), e);
                }
            }
            if (StringUtils.isNotEmpty(website.getExtractorConfigSource())) {
                try {
                    ExtractorConfig extractorConfig = XmlConfigParser.getInstance()
                            .parse(website.getExtractorConfigSource(), ExtractorConfig.class, parentConfigHandler);
                    website.setExtractorConfig(extractorConfig);
                } catch (Exception e) {
                    logger.error("parse extractorConfig  error websiteId={},websiteName={}", website.getId(), website.getWebsiteName(), e);
                }
            }
        }
        return website;
    }

    private WebsiteConfig buildWebsiteConfig(WebsiteOperator operator) {
        CheckUtils.checkNotNull(operator, "operator is null");
        WebsiteConfig config = new WebsiteConfig();
        config.setWebsiteId(operator.getWebsiteId());
        config.setWebsiteName(operator.getWebsiteName());
        config.setWebsiteType("2");
        config.setIsenabled(true);
        config.setLoginTip(operator.getLoginTip());
        config.setVerifyTip(operator.getVerifyTip());
        config.setResetType(operator.getResetType());
        config.setSmsReceiver(operator.getSmsReceiver());
        config.setSmsTemplate(operator.getSmsTemplate());
        config.setResetTip(operator.getResetTip());
        config.setResetURL(operator.getResetUrl());
        config.setInitSetting(operator.getLoginConfig());
        config.setSearchConfig(operator.getSearchConfig());
        config.setExtractorConfig(operator.getExtractorConfig());
        config.setSimulate(operator.getSimulate());
        config.setWebsiteTitle(operator.getWebsiteTitle());
        config.setGroupCode(operator.getGroupCode());
        config.setGroupName(GroupEnum.getByGroupCode(operator.getGroupCode()).getGroupName());
        return config;
    }

    private WebsiteConfig buildWebsiteConfigFromWebsiteInfo(WebsiteInfoWithBLOBs info) {
        CheckUtils.checkNotNull(info, "info is null");
        WebsiteConfig config = new WebsiteConfig();
        config.setWebsiteId(info.getWebsiteId());
        config.setWebsiteName(info.getWebsiteName());
        config.setWebsiteType(info.getWebsiteType().toString());
        config.setIsenabled(true);
        config.setLoginTip(info.getLoginTip());
        config.setVerifyTip(info.getVerifyTip());
        config.setResetType(info.getResetType());
        config.setSmsReceiver(info.getSmsReceiver());
        config.setSmsTemplate(info.getSmsTemplate());
        config.setResetTip(info.getResetTip());
        config.setResetURL(info.getResetUrl());
        config.setInitSetting(info.getLoginConfig());
        config.setSearchConfig(info.getSearchConfig());
        config.setExtractorConfig(info.getExtractorConfig());
        config.setWebsiteTitle(info.getWebsiteTitle());
        config.setGroupCode(info.getGroupCode());
        if (info.getGroupCode() != null && !("".equals(info.getGroupCode()))) {
            config.setGroupName(GroupEnum.getByGroupCode(info.getGroupCode()).getGroupName());
        }
        return config;
    }

}
