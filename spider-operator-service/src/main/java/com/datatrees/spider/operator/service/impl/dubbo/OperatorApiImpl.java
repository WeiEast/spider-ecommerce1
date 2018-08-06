package com.datatrees.spider.operator.service.impl.dubbo;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.spider.operator.api.OperatorApi;
import com.datatrees.spider.operator.domain.OperatorGroup;
import com.datatrees.spider.operator.domain.OperatorLoginConfig;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;
import com.datatrees.spider.operator.service.WebsiteGroupService;
import com.datatrees.spider.operator.service.WebsiteOperatorService;
import com.datatrees.spider.operator.service.plugin.OperatorLoginPostPlugin;
import com.datatrees.spider.operator.service.plugin.OperatorPlugin;
import com.datatrees.spider.share.common.http.ProxyUtils;
import com.datatrees.spider.share.common.share.service.ProxyService;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.*;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.website.WebsiteType;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.WebsiteHolderService;
import com.datatrees.spider.share.common.utils.WebsiteUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/17.
 */
@Service
public class OperatorApiImpl implements OperatorApi, InitializingBean {

    private static final org.slf4j.Logger       logger                 = LoggerFactory.getLogger(OperatorApiImpl.class);

    private static final String                 OPERATOR_FAIL_USER_MAX = "operator.fail.usercount.max";

    @Resource
    private              RedisService           redisService;

    @Resource
    private              MessageService         messageService;

    @Resource
    private              MonitorService         monitorService;

    @Resource
    private              WebsiteGroupService    websiteGroupService;

    @Resource
    private              WebsiteOperatorService websiteOperatorService;

    private              ThreadPoolExecutor     operatorInitExecutors;

    @Resource
    private              ProxyService           proxyService;

    @Resource
    private              WebsiteHolderService   websiteHolderService;

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> httpResult = checkParams(param);
        if (!httpResult.getStatus()) {
            logger.warn("check param error,result={}", httpResult);
            return httpResult;
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        operatorInitExecutors.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
                        TaskUtils.addStep(param.getTaskId(), StepEnum.REC_INIT_MSG);
                        //清理共享信息
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId));
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId));

                        // 清理与任务绑定的代理
                        proxyService.clear(taskId);

                        try {
                            BackRedisUtils.del(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(taskId));
                            BackRedisUtils.del(RedisKeyPrefixEnum.TASK_PAGE_CONTENT.getRedisKey(taskId));
                        } catch (Throwable e) {
                            logger.error("delete task info from back redis error taskId={}", taskId, e);
                        }
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_CONTEXT.getRedisKey(taskId));
                        RedisUtils.del(RedisKeyPrefixEnum.TASK_WEBSITE.getRedisKey(taskId));

                        //从新的运营商表读取配置
                        WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(websiteName);
                        Website website = websiteHolderService.getWebsite(websiteName);
                        redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);

                        //缓存task基本信息
                        TaskUtils.initTaskShare(taskId, websiteName);
                        TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, param.getMobile().toString());
                        TaskUtils.addTaskShare(taskId, AttributeKey.MOBILE, param.getMobile().toString());
                        TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_CODE, website.getGroupCode());
                        TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_NAME, website.getGroupName());
                        TaskUtils.addTaskShare(taskId, AttributeKey.NICK_GROUP_CODE, param.getGroupCode());
                        TaskUtils.addTaskShare(taskId, AttributeKey.NICK_GROUP_NAME, param.getGroupName());
                        TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TITLE, website.getWebsiteTitle());
                        TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TYPE, website.getWebsiteType());

                        if (!StringUtils.isAnyBlank(websiteName, param.getGroupCode())) {
                            WebsiteUtils.cacheNickGroupCodeWebsites(param.getGroupCode(), websiteName);
                        }

                        //设置代理
                        ProxyUtils.setProxyEnable(taskId, websiteOperator.getProxyEnable());

                        //记录登陆开始时间
                        TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.START_TIMESTAMP.getRedisKey(param.getFormType()),
                                System.currentTimeMillis() + "");
                        monitorService.initTask(taskId, websiteName, param.getMobile());

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

        result = checkHttpResult(result, param);
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

        result = checkHttpResult(result, param);
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

        result = checkHttpResult(result, param);
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
            OperatorPlugin pluginService = getPluginService(param.getWebsiteName(), taskId);
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
            TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.SUBMIT_RESULT.getRedisKey(param.getFormType()), JSON.toJSONString(result));
            String log = TemplateUtils.format("{}-->校验-->{}", param.getActionName(), result.getStatus() ? "成功" : "失败");
            monitorService.sendTaskLog(taskId, param.getWebsiteName(), log, result);
            sendSubmitSuccessMessage(pluginService, result, param, startTime);

            result = checkHttpResult(result, param);
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            monitorService.sendMethodUseTime(param.getTaskId(), param.getWebsiteName(), param.getFormType(), this.getClass().getName(), "submit",
                    Arrays.asList(param), result, startTime, endTime);
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

    private OperatorPlugin getPluginService(String websiteName, Long taskId) {
        return websiteOperatorService.getOperatorPluginService(websiteName, taskId);
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

    @Override
    public HttpResult<OperatorLoginConfig> preLogin(OperatorParam param) {
        HttpResult<OperatorLoginConfig> result = new HttpResult<>();
        try {
            if (null == param || BooleanUtils.isNotPositiveNumber(param.getTaskId())) {
                return result.failure(ErrorCode.EMPTY_TASK_ID);
            }
            if (StringUtils.isBlank(param.getGroupCode())) {
                return result.failure(ErrorCode.EMPTY_GROUP_CODE);
            }
            if (BooleanUtils.isNotPositiveNumber(param.getMobile())) {
                return result.failure(ErrorCode.EMPTY_MOBILE);
            }

            String websiteName = websiteGroupService.selectOperator(param.getGroupCode());
            logger.info("select website : {} for taskId : {}", websiteName, param.getTaskId());

            param.setWebsiteName(websiteName);
            param.setFormType(FormType.LOGIN);
            param.setGroupName(GroupEnum.getGroupName(param.getGroupCode()));
            init(param);

            OperatorLoginConfig config = websiteOperatorService.getLoginConfig(websiteName);
            config.setTaskId(param.getTaskId());
            config.setMobile(param.getMobile());
            config.setGroupCode(param.getGroupCode());
            config.setGroupName(param.getGroupName());

            return result.success(config);
        } catch (Throwable e) {
            logger.error("checkParams error,param={}", JSON.toJSONString(param), e);
            return result.failure();
        }
    }

    @Override
    public HttpResult<List<Map<String, List<OperatorGroup>>>> queryGroups() {
        List<Map<String, List<OperatorGroup>>> list = new ArrayList<>();
        List<OperatorGroup> group10086 = new ArrayList<>();
        List<OperatorGroup> group10000 = new ArrayList<>();
        List<OperatorGroup> group10010 = new ArrayList<>();
        for (GroupEnum group : GroupEnum.values()) {
            if (group.getWebsiteType() != WebsiteType.OPERATOR | group == GroupEnum.CHINA_10000 || group == GroupEnum.CHINA_10086) {
                continue;
            }
            OperatorGroup config = new OperatorGroup();
            config.setGroupCode(group.getGroupCode());
            config.setGroupName(group.getGroupName());

            if (group.getGroupName().contains("移动")) {
                group10086.add(config);
                continue;
            }
            if (group.getGroupName().contains("联通")) {
                group10010.add(config);
                continue;
            }
            if (group.getGroupName().contains("电信")) {
                group10000.add(config);
                continue;
            }
        }
        Map<String, List<OperatorGroup>> map10086 = new HashMap<>();
        map10086.put("移动", group10086);
        list.add(map10086);

        Map<String, List<OperatorGroup>> map10010 = new HashMap<>();
        map10010.put("联通", group10010);
        list.add(map10010);

        Map<String, List<OperatorGroup>> map10000 = new HashMap<>();
        map10000.put("电信", group10000);
        list.add(map10000);

        return new HttpResult<List<Map<String, List<OperatorGroup>>>>().success(list);
    }

    /**
     * 发送消息,启动爬虫
     * 超过20秒不启动爬虫
     * @param param
     */
    private void sendSubmitSuccessMessage(OperatorPlugin pluginService, HttpResult result, OperatorParam param, long startTime) {
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
                if (pluginService instanceof OperatorLoginPostPlugin) {
                    websiteOperatorService.sendOperatorLoginPostMessage(param.getTaskId(), param.getWebsiteName());
                } else {
                    websiteOperatorService.sendOperatorCrawlerStartMessage(param.getTaskId(), param.getWebsiteName());
                }
                logger.info("发送消息,启动爬虫,taskId={},websiteName={}", param.getTaskId(), param.getWebsiteName());
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        int corePoolSize = PropertiesConfiguration.getInstance().getInt("operator.init.thread.min", 10);
        int maximumPoolSize = PropertiesConfiguration.getInstance().getInt("operator.init.thread.max", 100);
        operatorInitExecutors = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(300),
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

    private HttpResult<Map<String, Object>> checkHttpResult(HttpResult<Map<String, Object>> result, OperatorParam param) {
        HttpResult<Map<String, Object>> newResult = result;
        try {
            if (!newResult.getStatus() && !(newResult.getResponseCode() == ErrorCode.NOT_SUPORT_METHOD.getErrorCode())) {
                String groupCode = param.getGroupCode();
                String property = PropertiesConfiguration.getInstance().get(OPERATOR_FAIL_USER_MAX);
                int maxFailUser = 5;
                if (StringUtils.isNotBlank(property)) {
                    maxFailUser = Integer.parseInt(property);
                }
                boolean b = WebsiteUtils.isNormal(groupCode, maxFailUser);
                if (!b) {
                    if (StringUtils.equals(param.getFormType(), FormType.LOGIN)) {
                        newResult.setResponseCode(ErrorCode.UNDER_MAINTENANCE.getErrorCode());
                    }
                    newResult.setMessage(ErrorCode.UNDER_MAINTENANCE.getErrorMsg());
                }
            }
        } catch (Exception e) {
            logger.error("检查HttpResult出现异常，返回原result", e);
            return result;
        }
        return newResult;

    }

}
