/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.core.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.CacheUtil;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.core.common.ActorLockEventWatcher;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.service.WebsiteService;
import com.datatrees.rawdatacentral.domain.common.Website;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.DirectiveRedisCode;
import com.datatrees.rawdatacentral.domain.constant.DirectiveType;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.operator.*;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.share.RedisService;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月5日 上午11:33:19
 */
@Service
public class CrawlerServiceImpl implements CrawlerService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    @Resource
    private WebsiteService      websiteService;

    @Resource
    private RedisDao            redisDao;

    @Resource
    private RedisService        redisService;

    @Resource
    private ZooKeeperClient     zooKeeperClient;

    @Override
    public WebsiteConf getWebsiteConf(String websiteName) {
        logger.info("websiteName:{} getWebsiteConf Start", websiteName);
        Map<String, String> map = (Map<String, String>) CacheUtil.INSTANCE.getObject("websitename_transform_map");
        String newWebsiteName;
        if (map != null) {
            newWebsiteName = map.get(websiteName);
        } else {
            map = (Map<String, String>) GsonUtils.fromJson(
                PropertiesConfiguration.getInstance().get("websitename_transform_map"),
                new TypeToken<HashMap<String, String>>() {
                }.getType());
            CacheUtil.INSTANCE.insertObject("websitename_transform_map", map);
            newWebsiteName = map.get(websiteName);
        }
        if (StringUtils.isNotBlank(newWebsiteName)) {
            Website website = websiteService.getCachedWebsiteByName(newWebsiteName);
            WebsiteConf conf = null;
            if (website != null && website.getWebsiteConf() != null) {
                conf = website.getWebsiteConf();
                conf.setName(websiteName);
                logger.info("websiteName:{},return conf :{}", websiteName, conf.toString());
                return conf;
            } else {
                logger.warn("no active website named {}", newWebsiteName);
            }
        } else {
            logger.warn("no this websiteName in properties, websiteName is {}", websiteName);
        }
        return null;

    }

    @Override
    public List<WebsiteConf> getWebsiteConf(List<String> websiteNameList) {
        List<WebsiteConf> confList = new ArrayList<>();
        for (String websiteName : websiteNameList) {
            WebsiteConf websiteConf = getWebsiteConf(websiteName);
            if (null != websiteConf) {
                confList.add(websiteConf);
            }
        }
        return confList;
    }

    @Override
    public boolean updateWebsiteConfig(String websiteName, String searchConfigSource, String extractConfigSource) {
        logger.info("crawlerService start update webiste:" + websiteName);
        try {
            synchronized (websiteName) {
                Website website = websiteService.getWebsiteNoConfByName(websiteName);
                if (website != null) {
                    website.setSearchConfigSource(searchConfigSource);
                    website.setExtractorConfigSource(extractConfigSource);
                    if (websiteService.countWebsiteConfigByWebsiteId(website.getId()) > 0) {
                        websiteService.updateWebsiteConfig(website);
                        logger.info("update websiteConfig success,webiste:" + websiteName);
                    } else {
                        websiteService.insertWebsiteConfig(website);
                        logger.info("insert websiteConfig success,webiste:" + websiteName);
                    }
                } else {
                    logger.warn("can't find website by websiteName:" + websiteName);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public HttpResult<Boolean> importCrawlCode(String directiveId, long taskId, int type, String code,
                                               Map<String, String> extra) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            if (null == extra) {
                extra = new HashMap<>();
            }
            if (taskId <= 0 || type < 0 || StringUtils.isAnyBlank(directiveId, code)) {
                logger.warn("invalid param taskId={},type={},directiveId={},code={},extra={}", taskId, type,
                    directiveId, code, JSON.toJSONString(extra));
                return result.failure("参数为空或者参数不完整");
            }
            String status = DirectiveRedisCode.WAIT_SERVER_PROCESS;
            String directiveType = null;
            switch (type) {
                case 0:
                    directiveType = DirectiveType.CRAWL_SMS;
                    break;
                case 1:
                    directiveType = DirectiveType.CRAWL_CODE;
                    break;
                default:
                    logger.warn("invalid param taskId={},type={}", taskId, type);
                    return result.failure("未知参数type");
            }

            extra.put(AttributeKey.CODE, code);
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(directiveType, taskId);
            //保存交互指令到redis
            sendDirective.fill(status, extra);
            redisService.saveDirectiveResult(directiveId, sendDirective);
            logger.info("import success taskId={},directiveId={},code={},extra={}", taskId, directiveId, code,
                JSON.toJSONString(extra));
            return result.success(true);
        } catch (Exception e) {
            logger.error("import error taskId={},directiveId={},code={},extra={}", taskId, directiveId, code,
                JSON.toJSONString(extra));
            return result.failure();
        }
    }

    @Override
    public HttpResult<String> fetchLoginCode(long taskId, int type, String username, String password,
                                             Map<String, String> extra) {
        HttpResult<String> result = new HttpResult<>();
        long timeout = 30;
        try {
            if (taskId <= 0 || type < 0) {
                logger.warn("fetchLoginCode invalid param taskId={},type={}", taskId, type);
                return result.failure("参数为空或者参数不完整");
            }
            if (null == extra) {
                extra = new HashMap<>();
            }
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN,
                taskId);
            String status = null;
            switch (type) {
                case 0:
                    status = DirectiveRedisCode.REFRESH_LOGIN_RANDOMPASSWORD;
                    break;
                case 1:
                    status = DirectiveRedisCode.REFRESH_LOGIN_CODE;
                    break;
                case 2:
                    status = DirectiveRedisCode.REFRESH_LOGIN_QR_CODE;
                    break;
                default:
                    logger.warn("fetchLoginCode invalid param taskId={},type={},username={},extra={}", taskId, type,
                        username, JSON.toJSONString(extra));
                    return result.failure("未知参数type");
            }
            if (StringUtils.isNoneBlank(username)) {
                extra.put(AttributeKey.USERNAME, username);
            }
            if (StringUtils.isNoneBlank(password)) {
                extra.put(AttributeKey.PASSWORD, password);
            }

            //保存交互指令到redis
            sendDirective.fill(status, extra);

            //相同命令枷锁,加锁成功:发送指令,清除结果key,进入等待;加锁失败:进入等待结果
            String directiveId = redisService.saveDirectiveResult(sendDirective);

            DirectiveResult<String> receiveResult = redisService.getDirectiveResult(directiveId, timeout,
                TimeUnit.SECONDS);
            if (null == receiveResult) {
                logger.warn("fetchLoginCode get result timeout taskId={},directiveId={},timeout={},timeUnit={}", taskId,
                    directiveId, timeout, TimeUnit.SECONDS);
                return result.failure("get data from plugin timeout");
            }
            logger.info("fetchLoginCode success taskId={},directiveId={},status={}", taskId, directiveId, status);
            return result.success(receiveResult.getData());
        } catch (Exception e) {
            logger.error("fetchLoginCode error taskId={}", taskId, e);
            return result.failure();
        }

    }

    @Override
    public HttpResult<String> login(long taskId, String username, String password, String code, String randomPassword,
                                    Map<String, String> extra) {

        HttpResult<String> result = new HttpResult<>();
        long timeout = 30;
        String directiveId = null;
        try {
            if (taskId <= 0 || StringUtils.isBlank(username)) {
                logger.warn("fetchLoginCode invalid param taskId={},username={}", taskId, username);
                return result.failure("invalid params taskId or username");
            }
            if (StringUtils.isBlank(password)) {
                logger.warn("fetchLoginCode  empty password, taskId={},username={}", taskId, username);
                return result.failure("invalid params,empty password");
            }
            if (null == extra) {
                extra = new HashMap<>();
            }
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN,
                taskId);
            extra.put(AttributeKey.USERNAME, username);
            extra.put(AttributeKey.PASSWORD, password);
            extra.put(AttributeKey.CODE, code);
            extra.put(AttributeKey.RANDOM_PASSWORD, randomPassword);

            //保存交互指令到redis
            sendDirective.fill(DirectiveRedisCode.START_LOGIN, extra);

            //相同命令枷锁,加锁成功:发送指令,清除结果key,进入等待;加锁失败:进入等待结果
            directiveId = redisService.saveDirectiveResult(sendDirective);

            DirectiveResult<String> receiveResult = redisService.getDirectiveResult(directiveId, timeout,
                TimeUnit.SECONDS);
            if (null == receiveResult) {
                logger.warn("login get result timeout taskId={},directiveId={},timeout={},timeUnit={},username={}",
                    taskId, directiveId, timeout, TimeUnit.SECONDS, username);
                return result.failure("登陆超时");
            }
            if (StringUtils.equals(DirectiveRedisCode.SERVER_FAIL, receiveResult.getStatus())) {
                logger.error("login failtaskId={},directiveId={},status={},errorMsg={},username={}", taskId,
                    directiveId, receiveResult.getErrorMsg(), username);
                return result.failure(receiveResult.getErrorMsg());
            }
            logger.info("login success taskId={},directiveId={},username={}", taskId, directiveId, username);
            return result.success("登陆成功!");
        } catch (Exception e) {
            logger.error("login error taskId={},directiveId={},username={}", taskId, directiveId, username, e);
            return result.failure("登陆失败!");
        }
    }

    @Override
    public HttpResult<String> verifyQr(String directiveId, long taskId, Map<String, String> extra) {
        HttpResult<String> result = new HttpResult<>();
        try {
            if (taskId <= 0 || StringUtils.isBlank(directiveId)) {
                logger.warn("verifyQr invalid param taskId={},directiveId={}", taskId, directiveId);
                return result.success(DirectiveRedisCode.FAILED);
            }
            DirectiveResult<String> directiveResult = redisService.getDirectiveResult(directiveId, 2, TimeUnit.SECONDS);
            if (null == directiveResult) {
                logger.warn("verifyQr timeout taskId={},directiveId={}", taskId, directiveId);
                return result.success(DirectiveRedisCode.FAILED);
            }
            TimeUnit.MILLISECONDS.sleep(500);//不能让前端一直轮询
            logger.info("verifyQr result taskId={},directiveId={},qrStatus={}", taskId, directiveId,
                directiveResult.getStatus());
            return result.success(directiveResult.getStatus());
        } catch (Exception e) {
            logger.error("verifyQr error taskId={},directiveId={}", taskId, directiveId);
            return result.success(DirectiveRedisCode.FAILED);
        }
    }

    private boolean isTimeOut(long startTime, String websiteName) throws Exception {
        long now = System.currentTimeMillis();
        int maxInterval = PropertiesConfiguration.getInstance().getInt(websiteName + ".default.max.waittime",
            25 * 1000);
        if (now <= startTime + maxInterval) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResult<Boolean> cancel(long taskId, Map<String, String> extra) {
        ActorLockEventWatcher watcher = new ActorLockEventWatcher("CollectorActor", taskId + "", null, zooKeeperClient);
        logger.info("cancel taskId={}", taskId);
        HttpResult<Boolean> result = new HttpResult<Boolean>();
        result.setData(false);
        if (watcher.cancel()) {
            logger.info("cancel task success,taskId={}", taskId);
            result.setData(true);
            result.success();
        }
        return result.failure();
    }

    @Override
    public HttpResult<Boolean> importAppCrawlResult(String directiveId, long taskId, String html, String cookies,
                                                    Map<String, String> extra) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            if (taskId <= 0 || StringUtils.isAnyBlank(directiveId, html, cookies)) {
                logger.warn("importAppCrawlResult invalid param taskId={},directiveId={},html={},cookies={}", taskId,
                    directiveId, html, cookies);
                return result.failure("参数为空或者参数不完整");
            }
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.GRAB_URL, taskId);
            Map<String, String> data = new HashMap<>();
            data.put(AttributeKey.HTML, html);
            data.put(AttributeKey.COOKIES, cookies);
            //保存交互指令到redis
            sendDirective.fill(DirectiveRedisCode.WAIT_SERVER_PROCESS, data);
            redisService.saveDirectiveResult(directiveId, sendDirective);
            logger.info("importAppCrawlResult success taskId={},directiveId={}", taskId, directiveId);
            return result.success();
        } catch (Exception e) {
            logger.error("importAppCrawlResult error taskId={},directiveId={}", taskId, directiveId);
            return result.failure();
        }
    }

    @Override
    public HttpResult<List<OperatorCatalogue>> queryAllOperatorConfig() {
        HttpResult<List<OperatorCatalogue>> result = new HttpResult<>();
        try {
            List<OperatorCatalogue> list = new ArrayList<>();
            Map<String, List<OperatorConfig>> map = new HashMap<>();
            List<OperatorConfig> map10086 = new ArrayList<>();
            List<OperatorConfig> map10000 = new ArrayList<>();
            List<OperatorConfig> map10010 = new ArrayList<>();
            list.add(new OperatorCatalogue("移动", map10086));
            list.add(new OperatorCatalogue("联通", map10010));
            list.add(new OperatorCatalogue("电信", map10000));
            for (GroupEnum group : GroupEnum.values()) {
                Website website = websiteService.getCachedWebsiteByName(group.getWebsiteName());
                if (null == website) {
                    throw new RuntimeException("website not found websiteName=" + group.getWebsiteName());
                }
                WebsiteConf websiteConf = website.getWebsiteConf();
                String initSetting = websiteConf.getInitSetting();
                if (StringUtils.isBlank(initSetting)) {
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
                config.setGroopCode(group.getGroopCode());
                config.setGroupName(group.getGroupName());
                config.setWebsiteName(group.getWebsiteName());
                config.setLoginTip(websiteConf.getLoginTip());
                config.setResetTip(websiteConf.getResetTip());
                config.setResetType(websiteConf.getResetType());
                config.setResetURL(websiteConf.getResetURL());
                config.setSmsReceiver(websiteConf.getSmsReceiver());
                config.setSmsTemplate(websiteConf.getSmsTemplate());
                config.setVerifyTip(websiteConf.getVerifyTip());

                for (FieldInitSetting fieldInitSetting : fieldInitSettings) {
                    InputField field = FieldBizType.fields.get(fieldInitSetting.getType());
                    if (null != field.getDependencies()) {
                        for (String dependency : field.getDependencies()) {
                            field.getDependencies().add(FieldBizType.fields.get(dependency).getName());
                        }
                    }
                    if (StringUtils.equals(FieldBizType.PIC_CODE.getCode(), fieldInitSetting.getType())) {
                        config.setHasPicCode(true);
                    }
                    if (StringUtils.equals(FieldBizType.SMS_CODE.getCode(), fieldInitSetting.getType())) {
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
            return result.success(list);
        } catch (Exception e) {
            logger.error("queryAllOperatorConfig error", e);
            return result.failure();
        }

    }

}
