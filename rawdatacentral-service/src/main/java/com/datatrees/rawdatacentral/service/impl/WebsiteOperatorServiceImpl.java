package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.RegexpUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.operator.dao.WebsiteOperatorDAO;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoWithBLOBs;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.domain.model.example.WebsiteOperatorExample;
import com.datatrees.rawdatacentral.domain.operator.FieldBizType;
import com.datatrees.rawdatacentral.domain.operator.FieldInitSetting;
import com.datatrees.rawdatacentral.domain.operator.InputField;
import com.datatrees.rawdatacentral.domain.operator.OperatorLoginConfig;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;
import com.datatrees.rawdatacentral.service.NotifyService;
import com.datatrees.rawdatacentral.service.WebsiteGroupService;
import com.datatrees.rawdatacentral.service.WebsiteInfoService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteOperatorServiceImpl implements WebsiteOperatorService {

    private static final Logger              logger = LoggerFactory.getLogger(WebsiteOperatorServiceImpl.class);

    private static final Map<String, String> hosts  = new HashMap<>();

    static {
        hosts.put("开发", "192.168.5.15:6789");
        hosts.put("测试", "rawdatacentral.saas.test.treefinance.com.cn");
        hosts.put("准生产", "rawdatacentral.approach.saas.treefinance.com.cn");
        hosts.put("预发布", "rawdatecentral.yfb.saas.treefinance.com.cn");
        hosts.put("生产", "rawdatecentral.yfb.saas.treefinance.com.cn");
    }

    @Resource
    private WebsiteOperatorDAO  websiteOperatorDAO;

    @Resource
    private WebsiteGroupService websiteGroupService;

    @Resource
    private NotifyService       notifyService;

    @Resource
    private WebsiteInfoService  websiteInfoService;

    @Override
    public WebsiteOperator getByWebsiteName(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        String env = TaskUtils.getSassEnv();
        WebsiteOperatorExample example = new WebsiteOperatorExample();
        example.createCriteria().andWebsiteNameEqualTo(websiteName).andEnvEqualTo(env);
        List<WebsiteOperator> list = websiteOperatorDAO.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public WebsiteOperator getByWebsiteNameAndEnv(String websiteName, String env) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        WebsiteOperatorExample example = new WebsiteOperatorExample();
        example.createCriteria().andWebsiteNameEqualTo(websiteName).andEnvEqualTo(env);
        List<WebsiteOperator> list = websiteOperatorDAO.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    //    @Override
    //    public List<WebsiteOperator> queryByGroupCode(String groupCode) {
    //        WebsiteOperatorExample example = new WebsiteOperatorExample();
    //        example.createCriteria().andGroupCodeEqualTo(groupCode);
    //        return websiteOperatorDAO.selectByExample(example);
    //    }

    @Override
    public List<WebsiteOperator> queryByGroupCode(String groupCode) {
        WebsiteOperatorExample example = new WebsiteOperatorExample();
        String env = TaskUtils.getSassEnv();
        example.createCriteria().andGroupCodeEqualTo(groupCode).andEnvEqualTo(env);
        return websiteOperatorDAO.selectByExample(example);
    }

    @Override
    public void importWebsite(WebsiteOperator config) {
        CheckUtils.checkNotNull(config, "config is null");
        CheckUtils.checkNotBlank(config.getWebsiteName(), ErrorCode.EMPTY_WEBSITE_NAME);
        WebsiteInfoWithBLOBs info = websiteInfoService.getByWebsiteNameFromInfo(config.getWebsiteName());
        if (null == info) {
            logger.warn("WebsiteConfig not found websiteName={}", config.getWebsiteName());
            throw new CommonException("websiteName config not found");
        }

        WebsiteConfig source = new WebsiteConfig();
        source.setWebsiteId(info.getWebsiteId());
        source.setWebsiteName(info.getWebsiteName());
        source.setWebsiteType(info.getWebsiteType().toString());
        source.setIsenabled(true);
        source.setLoginTip(info.getLoginTip());
        source.setVerifyTip(info.getVerifyTip());
        source.setResetType(info.getResetType());
        source.setSmsReceiver(info.getSmsReceiver());
        source.setSmsTemplate(info.getSmsTemplate());
        source.setResetTip(info.getResetTip());
        source.setResetURL(info.getResetUrl());
        source.setInitSetting(info.getLoginConfig());
        source.setSearchConfig(info.getSearchConfig());
        source.setExtractorConfig(info.getExtractorConfig());
        source.setWebsiteTitle(info.getWebsiteTitle());
        source.setGroupCode(info.getGroupCode());
        if (info.getGroupCode() != null) {
            source.setGroupName(GroupEnum.getByGroupCode(info.getGroupCode()).getGroupName());
        }
        if (StringUtils.isBlank(config.getOperatorType())) {
            String websiteType = RegexpUtils.select(config.getWebsiteName(), "\\d+", 0);
            config.setOperatorType(websiteType);
        }
        String region = config.getWebsiteTitle().split("\\(")[0].replaceAll("移动", "").replaceAll("联通", "").replaceAll("电信", "");
        config.setRegionName(region);

        config.setEnv(TaskUtils.getSassEnv());
        config.setWebsiteId(source.getWebsiteId());
        config.setWebsiteName(source.getWebsiteName());
        config.setSearchConfig(source.getSearchConfig());
        config.setExtractorConfig(source.getExtractorConfig());
        config.setLoginTip(source.getLoginTip());
        config.setVerifyTip(source.getVerifyTip());
        config.setResetType(source.getResetType());
        config.setResetTip(source.getResetTip());
        if (!StringUtils.equals("null", source.getSmsTemplate())) {
            config.setSmsTemplate(source.getSmsTemplate());
        }
        if (!StringUtils.equals("null", source.getResetURL())) {
            config.setResetUrl(source.getResetURL());
        }
        config.setSmsReceiver(source.getSmsReceiver());
        config.setSimulate(source.getSimulate());
        if (StringUtils.isBlank(config.getLoginConfig())) {
            config.setLoginConfig(source.getInitSetting().trim().replaceAll(" ", "").replaceAll("\\n", ""));
        }
        websiteOperatorDAO.insertSelective(config);
    }

    @Override
    public void updateWebsite(WebsiteOperator config) {
        String env = TaskUtils.getSassEnv();
        WebsiteOperator operatorDb = getByWebsiteName(config.getWebsiteName());
        if (null == operatorDb) {
            throw new CommonException("websiteName not found,websiteName=" + config.getWebsiteName());
        }
        CheckUtils.checkNotBlank(config.getSearchConfig(), "searchConfig is empty");
        CheckUtils.checkNotBlank(config.getExtractorConfig(), "extractorConfig is empty");
        WebsiteOperator updateObj = new WebsiteOperator();
        updateObj.setWebsiteId(operatorDb.getWebsiteId());
        updateObj.setSearchConfig(config.getSearchConfig());
        updateObj.setExtractorConfig(config.getExtractorConfig());
        websiteOperatorDAO.updateByPrimaryKeySelective(updateObj);
    }

    @Override
    public void importConfig(String websiteName, String from) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        CheckUtils.checkNotBlank(from, "empty params from");
        if (!hosts.containsKey(from)) {
            throw new RuntimeException("from 配置不存在");
        }
        String env;
        if ("预发布".equals(from)) {
            env = "preproduct";
        } else if ("生产".equals(from)) {
            env = "product";
        } else if ("准生产".equals(from) || "测试".equals(from)) {
            env = "test";
        } else {
            env = "dev";
        }
        String queryUrl = TemplateUtils
                .format("http://{}/website/operator/getByWebsiteNameAndEnv?websiteName={}&env={}", hosts.get(from), websiteName, env);
        String json = TaskHttpClient.create(6L, "", RequestType.POST, "").setFullUrl(queryUrl).setProxyEnable(false).invoke().getPageContent();
        WebsiteOperator config = JSON.parseObject(json, new TypeReference<WebsiteOperator>() {});
        if (null == config || StringUtils.isBlank(config.getWebsiteName())) {
            throw new RuntimeException("website not found");
        }
        saveConfigForImport(config);
        logger.info("迁入运营商配置成功,websiteName={},from={}", websiteName, from);
    }

    public void saveConfigForImport(WebsiteOperator config) {
        CheckUtils.checkNotNull(config, "param is null");
        CheckUtils.checkNotBlank(config.getWebsiteName(), ErrorCode.EMPTY_WEBSITE_NAME);
        String env = TaskUtils.getSassEnv();
        if (!"dev".equals(env)) {
            throw new RuntimeException("请在开发环境下进行操作");
        }
        WebsiteOperator websiteOperatorDb = getByWebsiteName(config.getWebsiteName());
        config.setEnv(env);
        saveOrUpdateOperate(config, websiteOperatorDb);
    }

    private void saveOrUpdateOperate(WebsiteOperator config, WebsiteOperator websiteOperatorDb) {
        if (websiteOperatorDb == null) {
            if (null == config.getWebsiteId()) {
                websiteOperatorDAO.insertSelective(config);
            } else {
                websiteOperatorDb = websiteOperatorDAO.selectByPrimaryKey(config.getWebsiteId());
                if (null != websiteOperatorDb) {
                    //放重复websiteId
                    websiteOperatorDAO.insertSelective(config);
                } else {
                    //保持相同的websiteId
                    websiteOperatorDAO.insertSelectiveWithPrimaryKey(config);
                }
            }
        } else {
            config.setWebsiteId(websiteOperatorDb.getWebsiteId());
            websiteOperatorDAO.updateByPrimaryKeySelective(config);
        }
    }

    @Override
    public void exportConfig(String websiteName, String to) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        CheckUtils.checkNotBlank(to, "empty params to");
        if (!hosts.containsKey(to)) {
            throw new RuntimeException("from 配置不存在");
        }
        String operateEnv = TaskUtils.getSassEnv();
        if (!"dev".equals(operateEnv)) {
            throw new RuntimeException("请在开发环境下进行操作");
        }
        String env;
        if ("开发".equals(to)) {
            env = "dev";
        } else if ("测试".equals(to) || "准生产".equals(to)) {
            env = "test";
        } else if ("预发布".equals(to)) {
            env = "preproduct";
        } else {
            env = "product";
        }
        WebsiteOperator config = getByWebsiteNameAndEnv(websiteName, operateEnv);
        if (null == config || StringUtils.isBlank(config.getWebsiteName())) {
            throw new RuntimeException("website not found");
        }
        config.setEnv(env);
        String queryUrl = TemplateUtils.format("http://{}/website/operator/saveConfigForExport", hosts.get(to));
        String result = TaskHttpClient.create(6L, "china_10000_app", RequestType.POST, "china_10000_app_001").setFullUrl(queryUrl)
                .setProxyEnable(false).setRequestBody(JSON.toJSONString(config), ContentType.APPLICATION_JSON).invoke().getPageContent();
        logger.info("exportConfig websiteName={},to={},result={}", websiteName, to, result);

    }

    @Override
    public void saveConfigForExport(WebsiteOperator config) {
        CheckUtils.checkNotNull(config, "param is null");
        CheckUtils.checkNotBlank(config.getWebsiteName(), ErrorCode.EMPTY_WEBSITE_NAME);
        String env = config.getEnv();
        WebsiteOperator websiteOperatorDb = getByWebsiteNameAndEnv(config.getWebsiteName(), env);
        saveOrUpdateOperate(config, websiteOperatorDb);
    }

    //    @Override
    //    public void saveConfig(WebsiteOperator config) {
    //        CheckUtils.checkNotNull(config, "param is null");
    //        CheckUtils.checkNotBlank(config.getWebsiteName(), ErrorCode.EMPTY_WEBSITE_NAME);
    //        String env=config.getEnv();
    //        WebsiteOperator websiteOperatorDb = getByWebsiteNameAndEnv(config.getWebsiteName(),env);
    //        if (null == websiteOperatorDb) {
    //            if (null == config.getWebsiteId()) {
    //                websiteOperatorDAO.insertSelective(config);
    //            } else {
    //                websiteOperatorDb = websiteOperatorDAO.selectByPrimaryKey(config.getWebsiteId());
    //                if (null != websiteOperatorDb) {
    //                    //放重复websiteId
    //                    config.setEnv(env);
    //                    websiteOperatorDAO.insertSelective(config);
    //                } else {
    //                    //保持相同的websiteId
    //
    //                    websiteOperatorDAO.insertSelectiveWithPrimaryKey(config);
    //                }
    //            }
    //        } else {
    //            config.setWebsiteId(websiteOperatorDb.getWebsiteId());
    //            websiteOperatorDAO.updateByPrimaryKeySelective(config);
    //        }
    //    }

    @Override
    public void updateEnable(String websiteName, Boolean enable) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        CheckUtils.checkNotNull(enable, "enable is null");
        updateEnable(websiteName, enable, false);
    }

    @Override
    public String updateEnable(String websiteName, Boolean enable, Boolean auto) {
        websiteOperatorDAO.updateEnable(websiteName, enable ? 1 : 0);
        return null;
    }

    @Override
    public List<WebsiteOperator> queryDisable() {
        WebsiteOperatorExample example = new WebsiteOperatorExample();
        example.createCriteria().andEnableEqualTo(false);
        return websiteOperatorDAO.selectByExample(example);
    }

    @Override
    public List<WebsiteOperator> queryAll() {
        WebsiteOperatorExample example = new WebsiteOperatorExample();
        return websiteOperatorDAO.selectByExample(example);
    }

    @Override
    public Map<String, WebsiteOperator> updateWebsiteStatus(String websiteName, boolean enable, boolean auto) {
        WebsiteOperator websiteOperatorDb = getByWebsiteName(websiteName);
        String redisKey = RedisKeyPrefixEnum.MAX_WEIGHT_OPERATOR.getRedisKey(websiteOperatorDb.getGroupCode());
        String fromWebsiteName = RedisUtils.get(redisKey);

        WebsiteOperator operatorUpdate = new WebsiteOperator();
        operatorUpdate.setWebsiteId(websiteOperatorDb.getWebsiteId());
        operatorUpdate.setEnable(enable);
        websiteOperatorDAO.updateByPrimaryKeySelective(operatorUpdate);
        websiteGroupService.updateEnable(websiteName, enable);
        websiteGroupService.updateCache();

        WebsiteOperator from = null;
        if (StringUtils.isNotBlank(fromWebsiteName)) {
            from = getByWebsiteName(fromWebsiteName);
        }

        String toWebsiteName = RedisUtils.get(redisKey);
        WebsiteOperator to = null;
        if (StringUtils.isNotBlank(toWebsiteName)) {
            to = getByWebsiteName(toWebsiteName);
        }
        Map<String, WebsiteOperator> map = new HashMap<>();
        map.put(AttributeKey.FROM, from);
        map.put(AttributeKey.TO, to);

        if (null != from && null != to) {
            notifyService.sendMsgForOperatorStatusUpdate(websiteOperatorDb, from, to, enable, auto);
        }
        return map;
    }

    @Override
    public OperatorLoginConfig getLoginConfig(String websiteName) {
        OperatorLoginConfig config = new OperatorLoginConfig();
        WebsiteOperator website = getByWebsiteName(websiteName);

        config.setWebsiteName(website.getWebsiteName());
        config.setLoginTip(website.getLoginTip());
        config.setResetTip(website.getResetTip());
        config.setResetType(website.getResetType());
        config.setResetURL(website.getResetUrl());
        config.setSmsReceiver(website.getSmsReceiver());
        config.setSmsTemplate(website.getSmsTemplate());
        config.setVerifyTip(website.getVerifyTip());

        config.setGroupCode(website.getGroupCode());
        String groupName = GroupEnum.getByGroupCode(website.getGroupCode()).getGroupName();
        config.setGroupName(groupName);

        String initSetting = website.getLoginConfig();
        JSONObject json = JSON.parseObject(initSetting);
        List<FieldInitSetting> fieldInitSettings = JSON.parseArray(json.getString("fields"), FieldInitSetting.class);
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
        return config;
    }
}
