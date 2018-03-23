package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.WebsiteUtils;
import com.datatrees.rawdatacentral.dao.WebsiteInfoDAO;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.WebsiteType;
import com.datatrees.rawdatacentral.domain.model.*;
import com.datatrees.rawdatacentral.domain.operator.*;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;
import com.datatrees.rawdatacentral.service.BankService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.service.WebsiteInfoService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import com.datatrees.rawdatacentral.service.proxy.SimpleProxyManager;
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

    private static final Logger logger = LoggerFactory.getLogger(WebsiteConfigServiceImpl.class);
    @Resource
    private WebsiteOperatorService websiteOperatorService;
    @Resource
    private WebsiteInfoService websiteInfoService;
    @Resource
    private WebsiteInfoDAO websiteInfoDAO;
    @Resource
    private PluginManager pluginManager;
    @Resource
    private BankService bankService;
    @Resource
    private RedisService redisService;
    @Resource
    private ProxyService proxyService;
    private ParentConfigHandler parentConfigHandler;

    public WebsiteConfigServiceImpl() {
        parentConfigHandler = new ParentConfigHandler() {
            @Override
            public <T> T parse(T type) throws Exception {
                if (type != null && type instanceof AbstractWebsiteConfig &&
                        StringUtils.isNotBlank(((AbstractWebsiteConfig) type).getParentWebsiteName())) {
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
        WebsiteInfo websiteInfo = websiteInfoDAO.selectByPrimaryKey(websiteId);
        if (null == websiteInfo) {
            logger.warn("WebsiteConfig not found websiteId={}", websiteId);
            return null;
        }
        return buildWebsiteConfigFromWebsiteInfo(websiteInfo);
    }

    @Override
    public WebsiteConfig getWebsiteConfigByWebsiteName(String websiteName) {
        CheckUtils.checkNotNull(websiteName, "websiteName is null");
        String env = TaskUtils.getSassEnv();
        WebsiteInfo websiteInfo = websiteInfoService.getByWebsiteNameAndEnv(websiteName, env);
        if (null == websiteInfo) {
            logger.warn("WebsiteConfig not found websiteId={}，env={}", websiteName, env);
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
        Website website = buildWebsite(websiteConfig);
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
        WebsiteInfo websiteInfo = new WebsiteInfo();
        websiteInfo.setWebsiteId(websiteConfig.getWebsiteId());
        websiteInfo.setSearchConfig(searchConfig);
        websiteInfo.setExtractorConfig(extractConfig);
        websiteInfo.setUpdatedAt(new Date());
        int i = websiteInfoDAO.updateByPrimaryKeySelective(websiteInfo);
        if (i == 0) {
            logger.warn("updateWebsiteInfo error websiteName={}", websiteName);
            return false;
        }
        deleteCacheByWebsiteName(websiteName);
        logger.info("updateWebsiteInfo success websiteName={}", websiteName);
        return true;
    }

    @Override
    public void deleteCacheByWebsiteName(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, "websiteName is blank");
        //redisService.deleteKey(RedisKeyPrefixEnum.WEBSITE_CONF_WEBSITENAME.getRedisKey(websiteName));
        redisService.deleteKey(RedisKeyPrefixEnum.ALL_OPERATOR_CONFIG.getRedisKey());
    }

    @Override
    public List<OperatorCatalogue> queryAllOperatorConfig() {
        List<OperatorCatalogue> list = new ArrayList<>();
        List<OperatorConfig> map10086 = new ArrayList<>();
        List<OperatorConfig> map10000 = new ArrayList<>();
        List<OperatorConfig> map10010 = new ArrayList<>();
        list.add(new OperatorCatalogue("移动", map10086));
        list.add(new OperatorCatalogue("联通", map10010));
        list.add(new OperatorCatalogue("电信", map10000));
        for (GroupEnum group : GroupEnum.values()) {
            if (group.getWebsiteType() != WebsiteType.OPERATOR | group == GroupEnum.CHINA_10000 || group == GroupEnum.CHINA_10086) {
                continue;
            }
            OperatorConfig config = new OperatorConfig();
            config.setGroupCode(group.getGroupCode());
            config.setGroupName(group.getGroupName());

            String websiteName = redisService.getString(RedisKeyPrefixEnum.MAX_WEIGHT_OPERATOR.getRedisKey(group.getGroupCode()));
            if (StringUtils.isBlank(websiteName)) {
                logger.error("严重错误,group没有配置,websiteName is blank,groupCode={}", group.getGroupCode());
                continue;
            }
            String env = TaskUtils.getSassEnv();
            CheckUtils.checkNotNull(env, "env is null");
//            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteNameAndEnv(websiteName, env);
            if (null == websiteOperator) {
                logger.error("website not found ,webisteName={}，env={}", websiteName, env);
                continue;
            }
            WebsiteConfig websiteConfig = buildWebsiteConfig(websiteOperator);
            //设置别名
            websiteConfig.setWebsiteName(websiteName);
            String initSetting = websiteConfig.getInitSetting();
            if (org.apache.commons.lang3.StringUtils.isBlank(initSetting)) {
                throw new RuntimeException("initSetting is blank websiteName=" + websiteName + ",env=" + env);
            }
            JSONObject json = JSON.parseObject(initSetting);
            if (!json.containsKey("fields")) {
                throw new RuntimeException("initSetting fields is blank websiteName=" + websiteName + ",env=" + env);
            }
            List<FieldInitSetting> fieldInitSettings = JSON.parseArray(json.getString("fields"), FieldInitSetting.class);
            if (null == fieldInitSettings) {
                throw new RuntimeException("initSetting fields is blank websiteName=" + websiteName + ",env=" + env);
            }
            config.setWebsiteName(websiteConfig.getWebsiteName());
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
                if (org.apache.commons.lang3.StringUtils.equals(FieldBizType.PIC_CODE.getCode(), fieldInitSetting.getType())) {
                    config.setHasPicCode(true);
                }
                if (org.apache.commons.lang3.StringUtils.equals(FieldBizType.SMS_CODE.getCode(), fieldInitSetting.getType())) {
                    config.setHasSmsCode(true);
                }
                config.getFields().add(field);
            }
            config.setEnable(true);
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
    public SearchProcessorContext getSearchProcessorContext(Long taskId) {
        Website website = getWebsiteFromCache(taskId);
        if (website != null) {
            SearchProcessorContext searchProcessorContext = new SearchProcessorContext(website);
            searchProcessorContext.setPluginManager(pluginManager);
            searchProcessorContext.setProxyManager(new SimpleProxyManager(taskId, website.getWebsiteName(), proxyService));
            searchProcessorContext.init();
            return searchProcessorContext;
        }
        return null;
    }

    @Override
    public SearchProcessorContext getSearchProcessorContext(Long taskId, String websiteName) {
        Website website = null;
        Boolean isOperator = WebsiteUtils.isOperator(websiteName);
        String env = TaskUtils.getSassEnv();
        if (isOperator) {
//            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteNameAndEnv(websiteName, env);
            //保存taskId对应的website,因为运营过程中用的是
            website = buildWebsite(websiteOperator);
        } else {
            WebsiteInfo websiteInfo = websiteInfoService.getByWebsiteNameAndEnv(websiteName, env);
            website = buildWebsiteFromWebsiteInfo(websiteInfo);
//            website = getWebsiteByWebsiteName(websiteName);
        }
        if (website != null) {
            SearchProcessorContext searchProcessorContext = new SearchProcessorContext(website);
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
        Boolean isOperator = WebsiteUtils.isOperator(websiteName);
        String env = TaskUtils.getSassEnv();
        if (isOperator) {
//            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteNameAndEnv(websiteName, env);
            //保存taskId对应的website,因为运营过程中用的是
            website = buildWebsite(websiteOperator);
        } else {
            WebsiteInfo websiteInfo = websiteInfoService.getByWebsiteNameAndEnv(websiteName, env);
            website = buildWebsiteFromWebsiteInfo(websiteInfo);
//            website = getWebsiteByWebsiteName(websiteName);
        }
        if (website != null) {
            ExtractorProcessorContext extractorProcessorContext = new ExtractorProcessorContext(website);
            extractorProcessorContext.setPluginManager(pluginManager);
            extractorProcessorContext.init();
            logger.info("getExtractorProcessorContext success,taskId={},websiteName={}", taskId, websiteName);
            return extractorProcessorContext;
        }
        return null;
    }

    @Override
    public ExtractorProcessorContext getExtractorProcessorContextWithBankId(int bankId) {
        Bank bank = bankService.getByBankIdFromCache(bankId);
        if (bank != null) {
            Website website = getWebsiteByWebsiteName(bank.getWebsiteName());
            if (website != null) {
                ExtractorProcessorContext extractorProcessorContext = new ExtractorProcessorContext(website);
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
                logger.error("not found group code for webisteName={}", websiteConfig.getWebsiteName());
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
        Website website = buildWebsite(config);
        return website;
    }

    @Override
    public Website buildWebsiteFromWebsiteInfo(WebsiteInfo websiteInfo) {
        WebsiteConfig config = buildWebsiteConfigFromWebsiteInfo(websiteInfo);
        Website website = buildWebsite(config);
        return website;
    }

    @Override
    public Website getWebsiteFromCache(Long taskId) {
        Website website = redisService.getCache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, new TypeReference<Website>() {
        });
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

    private WebsiteConfig buildWebsiteConfigFromWebsiteInfo(WebsiteInfo info) {
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
        if (info.getGroupCode() != null && "".equals(info.getGroupCode())) {
            config.setGroupName(GroupEnum.getByGroupCode(info.getGroupCode()).getGroupName());
        }
        return config;
    }

}
