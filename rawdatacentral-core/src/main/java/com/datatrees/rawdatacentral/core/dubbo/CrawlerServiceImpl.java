/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.core.dubbo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.ProxyService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.spider.share.common.TaskUtils;
import com.datatrees.spider.share.common.utils.ProcessResultUtils;
import com.datatrees.rawdatacentral.core.common.ActorLockEventWatcher;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.directive.DirectiveRedisCode;
import com.datatrees.spider.share.domain.directive.DirectiveType;
import com.datatrees.spider.share.domain.ProcessStatus;
import com.datatrees.spider.share.domain.QRStatus;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import com.datatrees.spider.share.domain.ProcessResult;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月5日 上午11:33:19
 */
@Service
public class CrawlerServiceImpl implements CrawlerService {

    private static final Logger               logger = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    @Resource
    private              WebsiteConfigService websiteConfigService;

    @Resource
    private              RedisService         redisService;

    @Resource
    private              ZooKeeperClient      zooKeeperClient;

    @Resource
    private              MonitorService       monitorService;

    @Autowired
    private              ProxyService         proxyService;

    @Override
    public WebsiteConf getWebsiteConf(String websiteName) {
        logger.info("getWebsiteConf start websiteName={}", websiteName);
        Map<String, String> map = redisService.getCache(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP, new TypeReference<Map<String, String>>() {});
        String newWebsiteName;
        if (null == map || !map.containsKey(websiteName)) {
            String property = PropertiesConfiguration.getInstance().get(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP.getRedisKey());
            map = JSON.parseObject(property, Map.class);
            redisService.cache(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP, map);
        }
        newWebsiteName = map.get(websiteName);
        if (StringUtils.isBlank(newWebsiteName)) {
            logger.warn("no this websiteName in properties, websiteName is {}", websiteName);
            return null;
        }
        WebsiteConf conf = websiteConfigService.getWebsiteConf(newWebsiteName);
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
    public HttpResult<Boolean> importCrawlCode(String directiveId, long taskId, int type, String code, Map<String, String> extra) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            if (null == extra) {
                extra = new HashMap<>();
            }
            if (taskId <= 0 || type < 0 || StringUtils.isAnyBlank(directiveId, code)) {
                logger.warn("invalid param taskId={},type={},directiveId={},code={},extra={}", taskId, type, directiveId, code,
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
                case 3:
                    directiveType = DirectiveType.LOGIN_SECOND_PASSWORD;
                    Long processId = Long.parseLong(extra.get("processId"));
                    ProcessResult<Object> processResult = ProcessResultUtils.queryProcessResult(processId);
                    if (StringUtils.equals(processResult.getProcessStatus(), ProcessStatus.REQUIRE_SECOND_PASSWORD)) {
                        processResult.setProcessStatus(ProcessStatus.PROCESSING);
                        ProcessResultUtils.saveProcessResult(processResult);
                        TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.WAITING);
                    }
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
            logger.info("import success taskId={},directiveId={},code={},extra={}", taskId, directiveId, code, JSON.toJSONString(extra));
            return result.success(true);
        } catch (Exception e) {
            logger.error("import error taskId={},directiveId={},code={},extra={}", taskId, directiveId, code, JSON.toJSONString(extra), e);
            return result.failure();
        }
    }

    @Override
    public HttpResult<String> fetchLoginCode(long taskId, int type, String username, String password, Map<String, String> extra) {
        HttpResult<String> result = new HttpResult<>();
        if (taskId <= 0 || type < 0) {
            logger.warn("fetchLoginCode invalid param taskId={},type={}", taskId, type);
            return result.failure("参数为空或者参数不完整");
        }
        if (StringUtils.isNoneBlank(username)) {
            TaskUtils.addTaskShare(taskId, AttributeKey.MOBILE, username);
            TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, username);
        }
        try {
            long timeout = 30;
            if (null == extra) {
                extra = new HashMap<>();
            }
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN, taskId);
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
                    logger.warn("fetchLoginCode invalid param taskId={},type={},username={},extra={}", taskId, type, username,
                            JSON.toJSONString(extra));
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

            DirectiveResult<String> receiveResult = redisService.getDirectiveResult(directiveId, timeout, TimeUnit.SECONDS);
            if (null == receiveResult) {
                logger.warn("fetchLoginCode get result timeout taskId={},directiveId={},timeout={},timeUnit={}", taskId, directiveId, timeout,
                        TimeUnit.SECONDS);
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
    public HttpResult<String> login(long taskId, String username, String password, String code, String randomPassword, Map<String, String> extra) {
        HttpResult<String> result = new HttpResult<>();
        if (taskId <= 0 || StringUtils.isBlank(username)) {
            logger.warn("fetchLoginCode invalid param taskId={},username={}", taskId, username);
            return result.failure("invalid params taskId or username");
        }
        TaskUtils.addTaskShare(taskId, AttributeKey.MOBILE, username);
        TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, username);
        if (StringUtils.isBlank(password)) {
            logger.warn("fetchLoginCode  empty password, taskId={},username={}", taskId, username);
            return result.failure("invalid params,empty password");
        }
        long timeout = 30;
        String directiveId = null;
        try {
            if (null == extra) {
                extra = new HashMap<>();
            }
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN, taskId);
            extra.put(AttributeKey.USERNAME, username);
            extra.put(AttributeKey.PASSWORD, password);
            extra.put(AttributeKey.CODE, code);
            extra.put(AttributeKey.RANDOM_PASSWORD, randomPassword);

            //保存交互指令到redis
            sendDirective.fill(DirectiveRedisCode.START_LOGIN, extra);

            //相同命令枷锁,加锁成功:发送指令,清除结果key,进入等待;加锁失败:进入等待结果
            directiveId = redisService.saveDirectiveResult(sendDirective);

            DirectiveResult<String> receiveResult = redisService.getDirectiveResult(directiveId, timeout, TimeUnit.SECONDS);
            if (null == receiveResult) {
                logger.warn("login get result timeout taskId={},directiveId={},timeout={},timeUnit={},username={}", taskId, directiveId, timeout,
                        TimeUnit.SECONDS, username);
                return result.failure("登陆超时");
            }
            if (StringUtils.equals(DirectiveRedisCode.SERVER_FAIL, receiveResult.getStatus())) {
                logger.error("login failtaskId={},directiveId={},status={},errorMsg={},username={}", taskId, directiveId, receiveResult.getErrorMsg(),
                        username);
                return result.failure(receiveResult.getData());
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
            logger.info("verifyQr result taskId={},directiveId={},qrStatus={}", taskId, directiveId, directiveResult.getStatus());
            return result.success(directiveResult.getStatus());
        } catch (Exception e) {
            logger.error("verifyQr error taskId={},directiveId={}", taskId, directiveId);
            return result.success(DirectiveRedisCode.FAILED);
        }
    }

    @Override
    public HttpResult<Boolean> cancel(long taskId, Map<String, String> extra) {
        HttpResult<Boolean> result = new HttpResult<Boolean>();
        String websiteName = TaskUtils.getTaskShare(taskId, AttributeKey.WEBSITE_NAME);
        if (StringUtils.equals(websiteName, "alipay.com") || StringUtils.equals(websiteName, "taobao.com") ||
                StringUtils.equals(websiteName, "taobao.com.h5")) {
            logger.info("电商拒绝取消,哈哈.......taskId={},websiteName={}", taskId, websiteName);
            return result.success();
        }

        DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN, taskId);
        String directiveId = redisService.createDirectiveId();
        sendDirective.setDirectiveId(directiveId);
        Map<String, String> directiveData = new HashMap<>();
        sendDirective.fill(DirectiveRedisCode.CANCEL, directiveData);
        redisService.saveDirectiveResult(sendDirective);

        // 清理与任务绑定的代理
        proxyService.clear(taskId);

        ActorLockEventWatcher watcher = new ActorLockEventWatcher("CollectorActor", taskId + "", null, zooKeeperClient);
        logger.info("cancel taskId={}", taskId);
        result.setData(false);
        if (watcher.cancel()) {
            logger.info("cancel task success,taskId={}", taskId);
            result.setData(true);
            result.success();
        }
        String reason = null;
        if (null != extra && extra.containsKey("reason")) {
            reason = extra.get("reason");
        }
        ErrorCode errorCode = ErrorCode.TASK_CANCEL;
        if (StringUtils.equals("timeout", reason)) {
            errorCode = ErrorCode.TASK_CANCEL_BY_SYSTEM;
        } else if (StringUtils.equals("user", reason)) {
            errorCode = ErrorCode.TASK_CANCEL_BY_USER;
        }
        monitorService.sendTaskCompleteMsg(taskId, null, errorCode.getErrorCode(), errorCode.getErrorMsg());
        return result.failure();
    }

    @Override
    public HttpResult<Boolean> importAppCrawlResult(String directiveId, long taskId, String html, String cookies, Map<String, String> extra) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            if (taskId <= 0 || StringUtils.isAnyBlank(directiveId, html, cookies)) {
                logger.warn("importAppCrawlResult invalid param taskId={},directiveId={},html={},cookies={}", taskId, directiveId, html, cookies);
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

}
