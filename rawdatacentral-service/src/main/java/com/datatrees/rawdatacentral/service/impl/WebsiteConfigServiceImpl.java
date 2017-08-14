package com.datatrees.rawdatacentral.service.impl;

import java.util.*;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.AbstractWebsiteConfig;
import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.common.resource.PluginManager;
import com.datatrees.crawler.core.util.xml.ParentConfigHandler;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigParser;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.dao.WebsiteConfigDAO;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.operator.*;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;
import com.datatrees.rawdatacentral.service.BankService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.service.proxy.SimpleProxyManager;

/**
 * Created by zhouxinghai on 2017/6/30.
 */
@Service
public class WebsiteConfigServiceImpl implements WebsiteConfigService {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteConfigServiceImpl.class);

    @Resource
    private WebsiteConfigDAO    websiteConfigDAO;

    @Resource
    private PluginManager       pluginManager;

    @Resource
    private BankService         bankService;

    @Resource
    private RedisService        redisService;

    @Resource
    private ProxyService        proxyService;

    private ParentConfigHandler parentConfigHandler;

    public WebsiteConfigServiceImpl() {
        parentConfigHandler = new ParentConfigHandler() {
            @Override
            public <T> T parse(T type) throws Exception {
                if (type != null && type instanceof AbstractWebsiteConfig
                    && StringUtils.isNotBlank(((AbstractWebsiteConfig) type).getParentWebsiteName())) {
                    String parentWebsiteName = ((AbstractWebsiteConfig) type).getParentWebsiteName();
                    logger.info("do parentConfigHandler for parentWebsiteName named: " + parentWebsiteName
                                + " for class " + type.getClass());
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
            new TypeReference<WebsiteConf>() {
            });
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
        deleteCacheByWebsiteName(websiteName);
        logger.info("updateWebsiteConf success websiteName={}", websiteName);
        return true;
    }

    @Override
    public void deleteCacheByWebsiteName(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, "websiteName is blank");
        redisService.deleteKey(RedisKeyPrefixEnum.WEBSITE_CONF_WEBSITENAME.getRedisKey(websiteName));
        redisService.deleteKey(RedisKeyPrefixEnum.ALL_OPERATOR_CONFIG.getRedisKey());
    }

    @Override
    public List<OperatorCatalogue> queryAllOperatorConfig() {
        List<OperatorCatalogue> list = new ArrayList<>();
        Map<String, List<OperatorConfig>> map = new HashMap<>();
        List<OperatorConfig> map10086 = new ArrayList<>();
        List<OperatorConfig> map10000 = new ArrayList<>();
        List<OperatorConfig> map10010 = new ArrayList<>();
        list.add(new OperatorCatalogue("移动", map10086));
        list.add(new OperatorCatalogue("联通", map10010));
        list.add(new OperatorCatalogue("电信", map10000));
        for (GroupEnum group : GroupEnum.values()) {
            WebsiteConfig websiteConfig = getWebsiteConfigByWebsiteName(group.getWebsiteName());
            CheckUtils.checkNotNull(websiteConfig, "website not found websiteName=" + group.getWebsiteName());
            String initSetting = websiteConfig.getInitSetting();
            if (org.apache.commons.lang3.StringUtils.isBlank(initSetting)) {
                throw new RuntimeException("initSetting is blank websiteName=" + group.getWebsiteName());
            }
            JSONObject json = JSON.parseObject(initSetting);
            if (!json.containsKey("fields")) {
                throw new RuntimeException("initSetting fields is blank websiteName=" + group.getWebsiteName());
            }
            List<FieldInitSetting> fieldInitSettings = JSON.parseArray(json.getString("fields"),
                FieldInitSetting.class);
            if (null == fieldInitSettings) {
                throw new RuntimeException("initSetting fields is blank websiteName=" + group.getWebsiteName());
            }

            OperatorConfig config = new OperatorConfig();
            config.setGroupCode(group.getGroupCode());
            config.setGroupName(group.getGroupName());
            config.setWebsiteName(group.getWebsiteName());
            config.setLoginTip(websiteConfig.getLoginTip());
            config.setResetTip(websiteConfig.getResetTip());
            config.setResetType(websiteConfig.getResetType());
            config.setResetURL(websiteConfig.getResetURL());
            config.setSmsReceiver(websiteConfig.getSmsReceiver());
            config.setSmsTemplate(websiteConfig.getSmsTemplate());
            config.setVerifyTip(websiteConfig.getVerifyTip());

            for (FieldInitSetting fieldInitSetting : fieldInitSettings) {
                InputField field = FieldBizType.fields.get(fieldInitSetting.getType());
                if (null != fieldInitSetting.getDependencies()) {
                    for (String dependency : fieldInitSetting.getDependencies()) {
                        field.getDependencies().add(FieldBizType.fields.get(dependency).getName());
                    }
                }
                if (org.apache.commons.lang3.StringUtils.equals(FieldBizType.PIC_CODE.getCode(),
                    fieldInitSetting.getType())) {
                    config.setHasPicCode(true);
                }
                if (org.apache.commons.lang3.StringUtils.equals(FieldBizType.SMS_CODE.getCode(),
                    fieldInitSetting.getType())) {
                    config.setHasSmsCode(true);
                }
                config.getFields().add(field);
            }
            if (group.getGroupName().contains("移动")) {
                map10086.add(config);
                continue;
            }
            if (group.getGroupName().contains("联通")) {
                map10010.add(config);
                continue;
            }
            if (group.getGroupName().contains("电信")) {
                map10000.add(config);
                continue;
            }
        }
        return list;
    }

    @Override
    public SearchProcessorContext getSearchProcessorContext(Long taskId, String websiteName) {
        Website website = this.getWebsiteByWebsiteName(websiteName);
        if (website != null) {
            SearchProcessorContext searchProcessorContext = new SearchProcessorContext(website);
            searchProcessorContext.setPluginManager(pluginManager);
            searchProcessorContext.setProxyManager(new SimpleProxyManager(taskId, websiteName, proxyService));
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

    @Override
    public Website websiteContextBuild(WebsiteConfig websiteConfig) {
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

}
