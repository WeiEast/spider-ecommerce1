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
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.core.common.ActorLockEventWatcher;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.DirectiveRedisCode;
import com.datatrees.rawdatacentral.domain.constant.DirectiveType;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.rawdatacentral.share.RedisService;
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

    private static final Logger    logger = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    @Resource
    private WebsiteConfigService   websiteConfigService;

    @Resource
    private RedisService           redisService;

    @Resource
    private ZooKeeperClient        zooKeeperClient;

    @Resource
    private CrawlerOperatorService crawlerOperatorService;

    @Override
    public WebsiteConf getWebsiteConf(String websiteName) {
        logger.info("websiteName:{} getWebsiteConf Start", websiteName);
        Map<String, String> map = redisService.getCache(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP,
            new TypeReference<Map<String, String>>() {
            });
        String newWebsiteName;
        if (map != null) {
            newWebsiteName = map.get(websiteName);
        } else {
            String property = PropertiesConfiguration.getInstance()
                .get(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP.getRedisKey());
            map = JSON.parseObject(property, Map.class);
            redisService.cache(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP, map);
            newWebsiteName = map.get(websiteName);
        }
        if (StringUtils.isBlank(newWebsiteName)) {
            logger.warn("no this websiteName in properties, websiteName is {}", websiteName);
            return null;
        }
        WebsiteConf conf = websiteConfigService.getWebsiteConfFromCache(newWebsiteName);
        if (null != conf) {
            //中文
            conf.setName(websiteName);
        }
        return conf;
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
    public boolean updateWebsiteConfig(String websiteName, String searchConfig, String extractConfig) {
        return websiteConfigService.updateWebsiteConf(websiteName, searchConfig, extractConfig);
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
        OperatorParam param = new OperatorParam();
        param.setTaskId(taskId);
        param.setFormType(FormType.LOGIN);
        if (StringUtils.isNoneBlank(username)) {
            param.setMobile(Long.valueOf(username));
        }
        param.setPassword(password);

        if (0 == type) {
            HttpResult<Map<String, Object>> pluginResult = crawlerOperatorService.refeshSmsCode(param);
            if (!pluginResult.getStatus()) {
                logger.warn("fetchLoginCode error pluginResult={}", pluginResult);
                return result.failure(pluginResult.getResponseCode(), pluginResult.getMessage());
            }
            logger.info("fetchLoginCode success,pluginResult={}", pluginResult);
            return result.success();
        }
        if (1 == type) {
            HttpResult<Map<String, Object>> pluginResult = crawlerOperatorService.refeshPicCode(param);
            if (!pluginResult.getStatus()) {
                logger.warn("fetchLoginCode error pluginResult={}", pluginResult);
                return result.failure(pluginResult.getResponseCode(), pluginResult.getMessage());
            }
            logger.info("fetchLoginCode success,pluginResult={}", pluginResult);
            return result.success(pluginResult.getData().get(AttributeKey.PIC_CODE).toString());
        }
        logger.info("fetchLoginCode fail,invalid taskId={},type={}", taskId, type);
        return result.failure();
    }

    @Override
    public HttpResult<String> login(long taskId, String username, String password, String code, String randomPassword,
                                    Map<String, String> extra) {
        HttpResult<String> result = new HttpResult<>();
        OperatorParam param = new OperatorParam();
        param.setTaskId(taskId);
        param.setFormType(FormType.LOGIN);
        if (StringUtils.isNoneBlank(username)) {
            param.setMobile(Long.valueOf(username));
        }
        param.setPassword(password);
        param.setPicCode(code);
        param.setSmsCode(randomPassword);
        if (null != extra) {
            if (extra.containsKey(AttributeKey.ID_CARD)) {
                param.setIdCard(extra.get(AttributeKey.ID_CARD));
            }
            if (extra.containsKey(AttributeKey.REAL_NAME)) {
                param.setRealName(extra.get(AttributeKey.REAL_NAME));
            }
        }
        HttpResult<Map<String, Object>> submitResult = crawlerOperatorService.submit(param);
        if (!submitResult.getStatus()) {
            logger.warn("login fail submitResult={},param={}", submitResult, param);
            return result.failure(submitResult.getResponseCode(), submitResult.getMessage());
        }
        logger.info("login success submitResult={},param={}", submitResult, param);
        return result.success("登陆成功!");
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
            List<OperatorCatalogue> list = redisService.getCache(RedisKeyPrefixEnum.ALL_OPERATOR_CONFIG,
                new TypeReference<List<OperatorCatalogue>>() {
                });
            if (null == list) {
                logger.warn("not found OperatorCatalogue from cache");
                list = websiteConfigService.queryAllOperatorConfig();
                redisService.cache(RedisKeyPrefixEnum.ALL_OPERATOR_CONFIG, list);
            }
            return result.success(list);
        } catch (Exception e) {
            logger.error("queryAllOperatorConfig error", e);
            return result.failure();
        }

    }

}
