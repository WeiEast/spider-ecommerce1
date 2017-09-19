package com.datatrees.rawdatacentral.core.dubbo;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BooleanUtils;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.TaskStageEnum;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/7/17.
 */
@Service
public class CrawlerOperatorServiceImpl implements CrawlerOperatorService {

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

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
            //清理共享信息
            redisService.deleteKey(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId()));
            redisService.deleteKey(RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(param.getTaskId()));
            redisService.deleteKey(RedisKeyPrefixEnum.TASK_PROXY.getRedisKey(param.getTaskId()));
            //保存mobile和websiteName
            if (null != param.getMobile()) {
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.MOBILE, param.getMobile().toString());
            }
            TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());
            logger.info("初始化运营商插件taskId={},websiteName={}", param.getTaskId(), param.getWebsiteName());
        }
        monitorService.sendTaskLog(param.getTaskId(), "运营商清理环境数据,准备登陆");
        result = getLoginService(param).init(param);
        monitorService.sendTaskLog(param.getTaskId(), "初始化完成", result);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> refeshPicCode(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        HttpResult<String> picResult = getLoginService(param).refeshPicCode(param);
        String log = null;
        if (!picResult.getStatus()) {
            log = TemplateUtils.format("{}-->图片验证码-->刷新失败!", FormType.getName(param.getFormType()));
            result.failure(picResult.getResponseCode(), picResult.getMessage());
        } else {
            log = TemplateUtils.format("{}-->图片验证码-->刷新成功!", FormType.getName(param.getFormType()));
            Map<String, Object> map = new HashMap<>();
            map.put(AttributeKey.PIC_CODE, picResult.getData());
            result.success(map);
        }
        if (result.getStatus() && StringUtils.equals(FormType.LOGIN, param.getFormType())) {
            messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码");
        }
        monitorService.sendTaskLog(param.getTaskId(), log, result);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        WebsiteOperator website = websiteOperatorService.getByWebsiteName(param.getWebsiteName());
        //刷新短信间隔时间
        int sendSmsInterval = website.getSmsInterval();
        String latestSendSmsTime = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.LATEST_SEND_SMS_TIME);
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
        result = getLoginService(param).refeshSmsCode(param);
        if (result.getStatus()) {
            TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.LATEST_SEND_SMS_TIME, System.currentTimeMillis() + "");
            if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
                messageService.sendTaskLog(param.getTaskId(), "向手机发送短信验证码");
            }
        }
        String log = TemplateUtils.format("{}-->短信验证码-->刷新{}!", FormType.getName(param.getFormType()), result.getStatus() ? "成功" : "失败");
        monitorService.sendTaskLog(param.getTaskId(), log, result);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        if (null != param && null != param.getTaskId()) {
            TaskUtils.removeTaskShare(param.getTaskId(), AttributeKey.LOGIN_PIC_CODE);
        }
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        if (StringUtils.isBlank(param.getPicCode())) {
            return result.failure(ErrorCode.EMPTY_PIC_CODE);
        }
        result = getLoginService(param).validatePicCode(param);
        String log = TemplateUtils.format("{}-->图片验证码-->校验{}!", FormType.getName(param.getFormType()), result.getStatus() ? "成功" : "失败");
        monitorService.sendTaskLog(param.getTaskId(), log, result);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        result = getLoginService(param).submit(param);
        if (null != result && result.getStatus()) {
            if (StringUtils.equals(FormType.LOGIN, param.getFormType())) {
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.MOBILE, param.getMobile().toString());
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.USERNAME, param.getMobile().toString());
                //登录成功
                if (StringUtils.isNoneBlank(param.getPassword())) {
                    TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.PASSWORD, param.getPassword());
                }
                if (StringUtils.isNoneBlank(param.getIdCard())) {
                    TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.ID_CARD, param.getPassword());
                }
                if (StringUtils.isNoneBlank(param.getRealName())) {
                    TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.REAL_NAME, param.getPassword());
                }
            } else if (StringUtils.isNoneBlank(param.getSmsCode())) {
                TaskUtils.addTaskShare(param.getTaskId(), RedisKeyPrefixEnum.TASK_SMS_CODE.getRedisKey(param.getFormType()), param.getSmsCode());
            }
        }
        if (!result.getStatus() && StringUtils.equals(FormType.LOGIN, param.getFormType())) {
            messageService.sendTaskLog(param.getTaskId(), "登陆失败");
        }
        String log = TemplateUtils.format("{}-->校验{}!", FormType.getName(param.getFormType()), result.getStatus() ? "成功" : "失败");
        monitorService.sendTaskLog(param.getTaskId(), log, result);
        sendLoginSuccessMessage(result, param);
        return result;
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
        HttpResult result = getLoginService(param).defineProcess(param);
        String log = TemplateUtils.format("自定义插件({})-->处理{}!", param.getFormType(), result.getStatus() ? "成功" : "失败");
        monitorService.sendTaskLog(param.getTaskId(), log, result);
        return result;
    }

    private OperatorPluginService getLoginService(OperatorParam param) {
        return classLoaderService.getOperatorPluginService(param.getWebsiteName());
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
            //插件先进行图片验证码,再进行短信校验
            if (StringUtils.equals(FormType.VALIDATE_BILL_DETAIL, param.getFormType()) && StringUtils.isBlank(param.getPicCode()) &&
                    map.containsKey(RedisKeyPrefixEnum.TASK_PIC_CODE.getRedisKey(param.getFormType()))) {
                param.setPicCode(map.get(RedisKeyPrefixEnum.TASK_PIC_CODE.getRedisKey(param.getFormType())));
            }
        }
    }

    /**
     * 校验基本参数
     * taskId,websiteName,mobile
     * @param param
     * @return
     */
    private HttpResult<Map<String, Object>> checkParams(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
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
        //暂时手机号不强制
        //if (BooleanUtils.isNotPositiveNumber(param.getMobile())) {
        //    return result.failure(ErrorCode.EMPTY_MOBILE);
        //}
        return result.success();
    }

    /**
     * 发送消息,启动爬虫
     * @param param
     */
    private void sendLoginSuccessMessage(HttpResult result, OperatorParam param) {
        if (null != result && result.getStatus()) {
            WebsiteOperator operator = websiteOperatorService.getByWebsiteName(param.getWebsiteName());
            String sendLoginStage = operator.getStartStage();
            if (StringUtils.equals(sendLoginStage, param.getFormType())) {
                redisService.saveString(RedisKeyPrefixEnum.TASK_RUN_STAGE, param.getTaskId(), TaskStageEnum.CRAWLER_START.getStatus());
                messageService.sendLoginSuccessMessage(param.getTaskId(), param.getWebsiteName());
                logger.info("发送消息,启动爬虫,taskId={},websiteName={}", param.getTaskId(), param.getWebsiteName());
            }
        }

    }
}
