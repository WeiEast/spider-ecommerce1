package com.datatrees.rawdatacentral.core.dubbo;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.retry.RetryHandler;
import com.datatrees.rawdatacentral.common.utils.*;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.StepEnum;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/17.
 */
@Service
public class CrawlerOperatorServiceImpl implements CrawlerOperatorService, InitializingBean {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CrawlerOperatorServiceImpl.class);
    @Resource
    private ClassLoaderService     classLoaderService;
    @Resource
    private RedisService           redisService;
    @Resource
    private MessageService         messageService;
    @Resource
    private MonitorService         monitorService;
    @Resource
    private WebsiteConfigService   websiteConfigService;
    @Resource
    private WebsiteOperatorService websiteOperatorService;
    private ThreadPoolExecutor     initExecutor;

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> httpResult = checkParams(param);
        if (!httpResult.getStatus()) {
            logger.warn("check param error,result={}", httpResult);
            return httpResult;
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        TaskUtils.addStep(param.getTaskId(), StepEnum.REC_INIT_MSG);
        initExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
                        TaskUtils.addStep(taskId, StepEnum.INIT);
                        //清理共享信息
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId));
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId));
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_PROXY.getRedisKey(taskId));
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_PROXY_ENABLE.getRedisKey(taskId));
                        try {
                            BackRedisUtils.del(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(taskId));
                            BackRedisUtils.del(RedisKeyPrefixEnum.TASK_PAGE_CONTENT.getRedisKey(taskId));
                        } catch (Throwable e) {
                            logger.error("save to back redis error taskId={}", taskId, e);
                        }
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_CONTEXT.getRedisKey(taskId));
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_WEBSITE.getRedisKey(taskId));

                        //从新的运营商表读取配置
                        WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
                        Website website = websiteConfigService.buildWebsite(websiteOperator);
                        redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);

                        //缓存task基本信息
                        TaskUtils.initTaskShare(taskId, websiteName);
                        TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, param.getMobile().toString());
                        TaskUtils.addTaskShare(taskId, AttributeKey.MOBILE, param.getMobile().toString());
                        TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_CODE, website.getGroupCode());
                        TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_NAME, website.getGroupName());
                        TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TITLE, website.getWebsiteTitle());
                        TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TYPE, website.getWebsiteType());
                        //设置代理
                        ProxyUtils.setProxyEnable(taskId, websiteOperator.getProxyEnable());

                        //记录登陆开始时间
                        TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.START_TIMESTAMP.getRedisKey(param.getFormType()),
                                System.currentTimeMillis() + "");
                        monitorService.initTask(taskId, websiteName, param.getMobile());
                        TaskUtils.addStep(taskId, StepEnum.INIT_SUCCESS);

                        if (null != param.getExtral() && !param.getExtral().isEmpty()) {
                            for (Map.Entry<String, Object> entry : param.getExtral().entrySet()) {
                                TaskUtils.addTaskShare(taskId, entry.getKey(), String.valueOf(entry.getValue()));
                            }
                        }

                        //执行运营商插件初始化操作
                        //运营商独立部分第一次初始化后不启动爬虫
                        HttpResult<Map<String, Object>> result = getPluginService(websiteName, taskId).init(param);
                        //爬虫状态
                        if (!result.getStatus()) {
                            TaskUtils.addStep(taskId, StepEnum.INIT_FAIL);
                            monitorService.sendTaskLog(taskId, websiteName, "登录-->初始化-->失败");
                            logger.warn("登录-->初始化-->失败");
                            return;
                        }
                        TaskUtils.addStep(taskId, StepEnum.INIT_SUCCESS);
                        monitorService.sendTaskLog(taskId, websiteName, "登录-->初始化-->成功");
                        logger.info("登录-->初始化-->成功");
                        return;
                    }
                    HttpResult<Map<String, Object>> result = getPluginService(websiteName, taskId).init(param);
                    if (!result.getStatus()) {
                        monitorService.sendTaskLog(taskId, websiteName, TemplateUtils.format("{}-->初始化-->失败", param.getActionName()));
                        logger.warn("{}-->初始化-->失败", param.getActionName());
                        return;
                    }
                    monitorService.sendTaskLog(taskId, websiteName, TemplateUtils.format("{}-->初始化-->成功", param.getActionName()));
                    logger.info("{}-->初始化-->成功", param.getActionName());
                } catch (Throwable e) {
                    logger.error("operator init fail,param:{}", param.toString(), e);
                }

            }
        });
        logger.info("revice operator init reques,param:{}", param.toString());
        return httpResult.success();
    }

    @Override
    public HttpResult<Map<String, Object>> refeshPicCode(OperatorParam param) {
        TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.STEP, param.getFormType());
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        if (!waitInitSuccess(param.getTaskId())) {
            logger.warn("task is not init ,param=[}", param.toString());
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        HttpResult<String> picResult = getPluginService(websiteName, taskId).refeshPicCode(param);
        String log = null;
        if (!picResult.getStatus()) {
            log = TemplateUtils.format("{}-->刷新图片验证码-->失败", param.getActionName());
            result.failure(picResult.getResponseCode(), picResult.getMessage());
        } else {
            log = TemplateUtils.format("{}-->刷新图片验证码-->成功", param.getActionName());
            Map<String, Object> map = new HashMap<>();
            map.put(AttributeKey.PIC_CODE, picResult.getData());
            result.success(map);
        }
        if (result.getStatus() && StringUtils.equals(FormType.LOGIN, param.getFormType())) {
            messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码");
        }
        monitorService.sendTaskLog(taskId, websiteName, log, result);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        if (!waitInitSuccess(param.getTaskId())) {
            logger.warn("task is not init ,param=[}", param.toString());
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
        WebsiteOperator website = websiteOperatorService.getByWebsiteName(param.getWebsiteName());
        //刷新短信间隔时间
        int sendSmsInterval = website.getSmsInterval();
        Long taskId = param.getTaskId();
        String latestSendSmsTime = TaskUtils.getTaskShare(taskId, AttributeKey.LATEST_SEND_SMS_TIME);
        if (StringUtils.isNoneBlank(latestSendSmsTime) && sendSmsInterval > 0) {
            long endTime = Long.valueOf(latestSendSmsTime) + TimeUnit.SECONDS.toMillis(sendSmsInterval);
            if (System.currentTimeMillis() < endTime) {
                try {
                    logger.info("刷新短信有间隔时间限制,latestSendSmsTime={},将等待{}秒", DateUtils.formatYmdhms(Long.valueOf(latestSendSmsTime)),
                            DateUtils.getUsedTime(System.currentTimeMillis(), endTime));
                    TimeUnit.MILLISECONDS.sleep(endTime - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException("refeshSmsCode error", e);
                }
            }
        }
        result = getPluginService(param.getWebsiteName(), taskId).refeshSmsCode(param);
        if (result.getStatus()) {
            TaskUtils.addTaskShare(taskId, AttributeKey.LATEST_SEND_SMS_TIME, System.currentTimeMillis() + "");
            if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
                messageService.sendTaskLog(taskId, "向手机发送短信验证码");
            }
        }
        String log = TemplateUtils.format("{}-->发送短信验证码-->{}", param.getActionName(), result.getStatus() ? "成功" : "失败");
        monitorService.sendTaskLog(taskId, param.getWebsiteName(), log, result);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        if (null != param && null != param.getTaskId()) {
            TaskUtils.removeTaskShare(param.getTaskId(), RedisKeyPrefixEnum.TASK_PIC_CODE.getRedisKey(param.getFormType()));
        }
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        if (StringUtils.isBlank(param.getPicCode())) {
            return result.failure(ErrorCode.EMPTY_PIC_CODE);
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        result = getPluginService(param.getWebsiteName(), taskId).validatePicCode(param);
        if (result.getStatus() || result.getResponseCode() == ErrorCode.NOT_SUPORT_METHOD.getErrorCode()) {
            TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.TASK_PIC_CODE.getRedisKey(param.getFormType()), param.getPicCode());
        }
        String log = TemplateUtils.format("{}-->校验图片验证码-->{}", param.getActionName(), result.getStatus() ? "成功" : "失败");
        monitorService.sendTaskLog(taskId, websiteName, log, result);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        long startTime = System.currentTimeMillis();
        HttpResult<Map<String, Object>> result = null;
        try {
            result = checkParams(param);
            if (!result.getStatus()) {
                logger.warn("check param error,result={}", result);
                return result;
            }
            if (!waitInitSuccess(param.getTaskId())) {
                logger.warn("task is not init ,param=[}", param.toString());
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            Long taskId = param.getTaskId();
            OperatorPluginService pluginService = getPluginService(param.getWebsiteName(), taskId);
            result = pluginService.submit(param);
            TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.FINISH_TIMESTAMP.getRedisKey(param.getFormType()), System.currentTimeMillis() + "");
            if (null != result && result.getStatus()) {
                if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
                    TaskUtils.addTaskShare(taskId, AttributeKey.MOBILE, param.getMobile().toString());
                    TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, param.getMobile().toString());
                    //登录成功
                    if (StringUtils.isNoneBlank(param.getPassword())) {
                        TaskUtils.addTaskShare(taskId, AttributeKey.PASSWORD, param.getPassword());
                    }
                    if (StringUtils.isNoneBlank(param.getIdCard())) {
                        TaskUtils.addTaskShare(taskId, AttributeKey.ID_CARD, param.getIdCard());
                    }
                    if (StringUtils.isNoneBlank(param.getRealName())) {
                        TaskUtils.addTaskShare(taskId, AttributeKey.REAL_NAME, param.getRealName());
                    }
                } else if (StringUtils.isNoneBlank(param.getSmsCode())) {
                    TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.TASK_SMS_CODE.getRedisKey(param.getFormType()), param.getSmsCode());
                }
            }
            if (!result.getStatus() && StringUtils.equals(FormType.LOGIN, param.getFormType())) {
                messageService.sendTaskLog(taskId, "登陆失败");
            }
            TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.STATUS.getRedisKey(param.getFormType()), result.getStatus() + "");
            String log = TemplateUtils.format("{}-->校验-->{}", param.getActionName(), result.getStatus() ? "成功" : "失败");
            monitorService.sendTaskLog(taskId, param.getWebsiteName(), log, result);
            sendSubmitSuccessMessage(pluginService, result, param, startTime);
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            monitorService.sendMethodUseTime(param.getTaskId(), param.getWebsiteName(), param.getFormType(), this.getClass().getName(), "submit",
                    Arrays.asList(param), result, startTime, endTime);
        }
    }

    @Override
    public HttpResult<List<OperatorCatalogue>> queryAllConfig() {
        HttpResult<List<OperatorCatalogue>> result = new HttpResult<>();
        try {
            List<OperatorCatalogue> list = redisService
                    .getCache(RedisKeyPrefixEnum.ALL_OPERATOR_CONFIG, new TypeReference<List<OperatorCatalogue>>() {});
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

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        HttpResult<Map<String, Object>> checkParams = checkParams(param);
        if (!checkParams.getStatus()) {
            logger.warn("check param error,result={}", checkParams);
            return new HttpResult<Object>().failure(checkParams.getResponseCode(), checkParams.getMessage());
        }
        HttpResult result = getPluginService(param.getWebsiteName(), param.getTaskId()).defineProcess(param);
        String log = TemplateUtils.format("自定义插件{}-->处理-->{}", param.getFormType(), result.getStatus() ? "成功" : "失败");
        monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), log, result);
        return result;
    }

    private OperatorPluginService getPluginService(String websiteName, Long taskId) {
        return classLoaderService.getOperatorPluginService(websiteName, taskId);
    }

    /**
     * 从前面的共享的信息中提取参数
     * @param param
     * @return
     */
    private void fillParamFromShares(OperatorParam param) {
        Map<String, String> map = TaskUtils.getTaskShares(param.getTaskId());
        if (null != map) {
            if (StringUtils.isBlank(param.getWebsiteName()) && map.containsKey(AttributeKey.WEBSITE_NAME)) {
                param.setWebsiteName(map.get(AttributeKey.WEBSITE_NAME));
            }
            if (null == param.getMobile() && map.containsKey(AttributeKey.MOBILE)) {
                param.setMobile(Long.valueOf(map.get(AttributeKey.MOBILE)));
            }
            if (StringUtils.isBlank(param.getPassword()) && map.containsKey(AttributeKey.PASSWORD)) {
                param.setPassword(map.get(AttributeKey.PASSWORD));
            }
            if (StringUtils.isBlank(param.getIdCard()) && map.containsKey(AttributeKey.ID_CARD)) {
                param.setIdCard(map.get(AttributeKey.ID_CARD));
            }
            if (StringUtils.isBlank(param.getRealName()) && map.containsKey(AttributeKey.REAL_NAME)) {
                param.setRealName(map.get(AttributeKey.REAL_NAME));
            }
            if (StringUtils.isBlank(param.getPicCode())) {
                String picCode = TaskUtils.getTaskShare(param.getTaskId(), RedisKeyPrefixEnum.TASK_PIC_CODE.getRedisKey(param.getFormType()));
                if (StringUtils.isNotBlank(picCode)) {
                    param.setPicCode(picCode);
                }
            }
        }
    }

    /**
     * 校验基本参数
     * taskId,websiteName,mobile
     * @param param
     * @return
     */
    @Override
    public HttpResult<Map<String, Object>> checkParams(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            if (null == param || BooleanUtils.isNotPositiveNumber(param.getTaskId())) {
                return result.failure(ErrorCode.EMPTY_TASK_ID);
            }
            if (StringUtils.isBlank(param.getFormType())) {
                return result.failure(ErrorCode.EMPTY_FORM_TYPE);
            }
            fillParamFromShares(param);
            if (StringUtils.isBlank(param.getWebsiteName())) {
                return result.failure(ErrorCode.EMPTY_WEBSITE_NAME);
            }
            if (BooleanUtils.isNotPositiveNumber(param.getMobile())) {
                return result.failure(ErrorCode.EMPTY_MOBILE);
            }
            return result.success();
        } catch (Throwable e) {
            logger.error("checkParams error,param={}", JSON.toJSONString(param), e);
            return result.failure();
        }
    }

    /**
     * 发送消息,启动爬虫
     * 超过20秒不启动爬虫
     * @param param
     */
    private void sendSubmitSuccessMessage(OperatorPluginService pluginService, HttpResult result, OperatorParam param, long startTime) {
        if (null != result && result.getStatus()) {
            WebsiteOperator operator = websiteOperatorService.getByWebsiteName(param.getWebsiteName());
            String sendLoginStage = operator.getStartStage();
            if (StringUtils.equals(sendLoginStage, param.getFormType())) {
                long endTime = System.currentTimeMillis();
                long useTime = endTime - startTime;
                if (useTime > TimeUnit.SECONDS.toMillis(20)) {
                    logger.info("登陆时间超过20秒,不启动爬虫,param={},useTime={}", param, DateUtils.getUsedTime(startTime, endTime));
                    return;
                }
                TaskUtils.addStep(param.getTaskId(), StepEnum.LOGIN_SUCCESS);
                if (pluginService instanceof OperatorPluginPostService) {
                    messageService.sendOperatorLoginPostMessage(param.getTaskId(), param.getWebsiteName());
                } else {
                    messageService.sendOperatorCrawlerStartMessage(param.getTaskId(), param.getWebsiteName());
                }
                logger.info("发送消息,启动爬虫,taskId={},websiteName={}", param.getTaskId(), param.getWebsiteName());
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        int corePoolSize = PropertiesConfiguration.getInstance().getInt("operator.init.thread.min", 10);
        int maximumPoolSize = PropertiesConfiguration.getInstance().getInt("operator.init.thread.max", 100);
        initExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(300),
                new ThreadFactory() {
                    private AtomicInteger count = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        String threadName = "operator_init_thread_" + count.addAndGet(1);
                        t.setName(threadName);
                        logger.info("create operator init thread :{}", threadName);
                        return t;
                    }
                });

    }

    private boolean waitInitSuccess(Long taskId) {
        try {
            String stepCode = RetryUtils.execute(new RetryHandler<String>() {
                private String stepCode;

                @Override
                public String execute() {
                    stepCode = TaskUtils.getTaskShare(taskId, AttributeKey.STEP_CODE);
                    return stepCode;
                }

                @Override
                public boolean check() {
                    return StringUtils.isNotBlank(stepCode) && Integer.valueOf(stepCode) >= StepEnum.INIT_SUCCESS.getStepCode();
                }
            }, 6, 500L);
            return StringUtils.isNotBlank(stepCode);
        } catch (Throwable e) {
            logger.info("waitInitSuccess error taskId={}", taskId, e);
            return false;
        }
    }
}
