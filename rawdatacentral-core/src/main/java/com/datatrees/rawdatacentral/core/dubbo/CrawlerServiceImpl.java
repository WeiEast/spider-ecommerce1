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
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.CacheUtil;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.core.common.ActorLockEventWatcher;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.service.WebsiteService;
import com.datatrees.rawdatacentral.domain.common.Website;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.DirectiveRedisCode;
import com.datatrees.rawdatacentral.domain.constant.DirectiveType;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.share.RedisService;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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
            if (website != null && (conf = website.getWebsiteConf()) != null) {
                conf.setName(websiteName);
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
            confList.add(getWebsiteConf(websiteName));
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
    public HttpResult<Boolean> importCrawlCode(long taskId, int type, String code, Map<String, Object> extra) {
        HttpResult<Boolean> result = new HttpResult<>();
        String directiveKey = null;
        try {
            if (null == extra) {
                extra = new HashMap<>();
            }
            if (taskId <= 0 || type < 0 || StringUtils.isBlank(code)) {
                logger.warn("invalid param taskId={},type={},code={},extra={}", taskId, type, code,
                    JSON.toJSONString(extra));
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
            DirectiveResult<Map<String, Object>> sendDirective = new DirectiveResult<>(directiveType, taskId);
            //保存交互指令到redis
            sendDirective.fill(status, extra);
            directiveKey = sendDirective.getDirectiveKey();
            redisService.saveDirectiveResult(sendDirective);
            logger.info("import success taskId={},directiveKey={},code={},extra={}", taskId, directiveKey, code,
                JSON.toJSONString(extra));
            return result.success(true);
        } catch (Exception e) {
            logger.error("import error taskId={},directiveKey={},code={},extra={}", taskId, directiveKey, code,
                JSON.toJSONString(extra));
            return result.failure();
        }
    }

    @Override
    public HttpResult<String> fetchLoginCode(long taskId, int type, Map<String, Object> extra) {
        long timeout = 60;
        HttpResult<String> result = new HttpResult<>();
        try {
            if (taskId <= 0 || type < 0) {
                logger.warn("invalid param taskId={},type={}", taskId, type);
                return result.failure("参数为空或者参数不完整");
            }
            DirectiveResult<Map<String, Object>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN,
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
                    logger.warn("invalid param taskId={},type={}", taskId, type);
                    return result.failure("未知参数type");
            }

            //保存交互指令到redis
            sendDirective.fill(status, extra);
            //todo 错的
            String resultKey = sendDirective.getDirectiveKey();

            //相同命令枷锁,加锁成功:发送指令,清除结果key,进入等待;加锁失败:进入等待结果
            if (redisService.lock(sendDirective.getLockKey(), timeout, TimeUnit.SECONDS)) {
                redisService.deleteKey(resultKey);
                redisService.saveDirectiveResult(sendDirective);
            }
            DirectiveResult<String> receiveResult = redisService.getDirectiveResult(resultKey, timeout,
                TimeUnit.SECONDS);
            if (null == receiveResult) {
                logger.warn("get result timeout key={},timeout={},timeUnit={}", resultKey, timeout, TimeUnit.SECONDS);
                return result.failure("get data from plugin timeout");
            }
            redisService.unlock(sendDirective.getLockKey());
            logger.info("fetchLoginCode success taskId={},status={}", taskId, status);
            return result.success(receiveResult.getData());
        } catch (Exception e) {
            logger.error("fetchLoginCode error taskId={}", taskId, e);
            return result.failure();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public HttpResult<String> verifyQr(long taskId, Map<String, Object> extra) {
        HttpResult<String> result = new HttpResult<String>();
        String getKey = "plugin_remark_" + taskId;
        String pullResult = redisDao.pullResult(getKey);
        if (StringUtils.isNotBlank(pullResult)) {
            Map<String, Object> resultMap = (Map<String, Object>) GsonUtils.fromJson(pullResult,
                new TypeToken<HashMap<String, Object>>() {
                }.getType());
            String qrStatus = "FAILURE";
            if (resultMap != null) {
                qrStatus = StringUtils.defaultIfBlank((String) resultMap.get(PluginConstants.FIELD), "");
            }
            result = result.success();
            result.setData(qrStatus);
            logger.debug(taskId + "verifyQr return" + qrStatus);
            return result;
        } else {
            String key = "verify_result_" + taskId;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("status", "VERIFY_QR_CODE");
            if (redisDao.saveListString(key, Arrays.asList(GsonUtils.toJson(map)))) {
                result = result.success();
                result.setData("WAITTING");
                logger.debug(taskId + "verifyQr return" + "WAITTING");
                return result;
            }
        }
        result = result.failure();
        return result;
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
    public HttpResult<Boolean> cancel(long taskId, Map<String, Object> extra) {
        ActorLockEventWatcher watcher = new ActorLockEventWatcher("CollectorActor", taskId + "", null, zooKeeperClient);
        logger.info("cancel taskId:" + taskId);
        HttpResult<Boolean> result = new HttpResult<Boolean>();
        result.setData(false);
        if (watcher.cancel()) {
            logger.info("cancel task success,taskId :" + taskId);
            result.setData(true);
            result.success();
        }
        return result.failure();
    }

    @Override
    public HttpResult<Boolean> importAppCrawlResult(long taskId, String html, String cookies,
                                                    Map<String, Object> extra) {
        HttpResult<Boolean> result = new HttpResult<>();
        String directiveKey = null;
        try {
            if (taskId <= 0 || StringUtils.isAnyBlank(html, cookies)) {
                logger.warn("invalid param taskId={},html={},cookies={}", taskId, html, cookies);
                return result.failure("参数为空或者参数不完整");
            }
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.GRAB_URL, taskId);
            Map<String, String> data = new HashMap<>();
            data.put("html", html);
            data.put("cookies", cookies);
            //保存交互指令到redis
            sendDirective.fill(DirectiveRedisCode.WAIT_SERVER_PROCESS, data);
            directiveKey = sendDirective.getDirectiveKey();
            redisService.saveDirectiveResult(sendDirective);
            logger.info("import success taskId={},directiveKey={}", taskId, directiveKey);
            return result.success();
        } catch (Exception e) {
            logger.error("import error taskId={},directiveKey={}", taskId, directiveKey);
            return result.failure();
        }
    }

}
