package com.datatrees.rawdatacentral.core.dubbo;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.common.utils.BooleanUtils;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.datatrees.rawdatacentral.service.OperatorPluginService.RETURN_FIELD_PIC_CODE;

/**
 * Created by zhouxinghai on 2017/7/17.
 */
@Service
public class CrawlerOperatorServiceImpl implements CrawlerOperatorService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CrawlerOperatorServiceImpl.class);

    @Resource
    private CrawlerService                crawlerService;

    @Resource
    private ClassLoaderService            classLoaderService;

    @Resource
    private RedisService                  redisService;

    @Resource
    private MessageService                messageService;

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
            redisService.addTaskShare(param.getTaskId(), AttributeKey.MOBILE, param.getMobile().toString());
            redisService.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());
            logger.info("初始化运营商插件taskId={},websiteName={}", param.getTaskId(), param.getWebsiteName());
        }
        messageService.sendTaskLog(param.getTaskId(), "准备登陆");
        return getLoginService(param).init(param);
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
            map.put(RETURN_FIELD_PIC_CODE, picResult.getData());
            result.success(map);
        }
        messageService.sendTaskLog(param.getTaskId(), log);
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        HttpResult<Map<String, Object>> result = checkParams(param);
        if (!result.getStatus()) {
            logger.warn("check param error,result={}", result);
            return result;
        }
        //刷新短信间隔时间
        int sendSmsInterval = PropertiesConfiguration.getInstance()
            .getInt(RedisKeyPrefixEnum.SEND_SMS_INTERVAL.getRedisKey(param.getWebsiteName()), 0);
        String latestSendSmsTime = redisService.getTaskShare(param.getTaskId(), AttributeKey.LATEST_SEND_SMS_TIME);
        if (StringUtils.isNoneBlank(latestSendSmsTime) && sendSmsInterval > 0) {
            long endTime = Long.valueOf(latestSendSmsTime) + TimeUnit.SECONDS.toMillis(sendSmsInterval);
            if (System.currentTimeMillis() < endTime) {
                try {
                    logger.info("刷新短信有间隔时间限制,latestSendSmsTime={},将等待{}秒",
                        DateUtils.formatYmdhms(Long.valueOf(latestSendSmsTime)),
                        DateUtils.getUsedTime(System.currentTimeMillis(), endTime));
                    TimeUnit.MILLISECONDS.sleep(endTime - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException("refeshSmsCode error", e);
                }
            }
        }
        //失败重试1次
        AtomicBoolean retry = new AtomicBoolean(false);
        do {
            result = getLoginService(param).refeshSmsCode(param);
            if (result.getStatus()) {
                redisService.addTaskShare(param.getTaskId(), AttributeKey.LATEST_SEND_SMS_TIME,
                    System.currentTimeMillis() + "");
            }
            messageService.sendTaskLog(param.getTaskId(), TemplateUtils.format("{}-->短信验证码-->刷新{}!",
                FormType.getName(param.getFormType()), result.getStatus() ? "成功" : "失败"));
        } while (retry.compareAndSet(false, true) && !result.getStatus());
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        if (null != param && null != param.getTaskId()) {
            redisService.removeTaskShare(param.getTaskId(), AttributeKey.LOGIN_PIC_CODE);
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
        if (result.getStatus()) {
            switch (param.getFormType()) {
                case FormType.LOGIN:
                    redisService.addTaskShare(param.getTaskId(), AttributeKey.LOGIN_PIC_CODE, param.getPicCode());
                    break;
                default:
                    break;
            }
        }
        messageService.sendTaskLog(param.getTaskId(), TemplateUtils.format("{}-->图片验证码-->校验{}!",
            FormType.getName(param.getFormType()), result.getStatus() ? "成功" : "失败"));
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
                //登录成功
                if (StringUtils.isNoneBlank(param.getPassword())) {
                    redisService.addTaskShare(param.getTaskId(), AttributeKey.PASSWORD, param.getPassword());
                }
                if (StringUtils.isNoneBlank(param.getIdCard())) {
                    redisService.addTaskShare(param.getTaskId(), AttributeKey.ID_CARD, param.getPassword());
                }
                if (StringUtils.isNoneBlank(param.getRealName())) {
                    redisService.addTaskShare(param.getTaskId(), AttributeKey.REAL_NAME, param.getPassword());
                }
            }
        }
        messageService.sendTaskLog(param.getTaskId(), TemplateUtils.format("{}-->校验{}!",
            FormType.getName(param.getFormType()), result.getStatus() ? "成功" : "失败"));
        sendLoginSuccessMessage(result, param);
        return result;
    }

    @Override
    public HttpResult<List<OperatorCatalogue>> queryAllConfig() {
        return crawlerService.queryAllOperatorConfig();
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
        Map<String, String> map = redisService.getTaskShares(param.getTaskId());
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
            if (StringUtils.equals(FormType.LOGIN, param.getFormType()) && StringUtils.isBlank(param.getPicCode())
                && map.containsKey(AttributeKey.LOGIN_PIC_CODE)) {
                param.setPicCode(map.get(AttributeKey.LOGIN_PIC_CODE));
            }
            if (StringUtils.equals(FormType.VALIDATE_BILL_DETAIL, param.getFormType())
                && StringUtils.isBlank(param.getPicCode()) && map.containsKey(AttributeKey.BILL_DETAIL_PIC_CODE)) {
                param.setPicCode(map.get(AttributeKey.BILL_DETAIL_PIC_CODE));
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
        if (BooleanUtils.isNotPositiveNumber(param.getMobile())) {
            return result.failure(ErrorCode.EMPTY_MOBILE);
        }
        return result.success();
    }

    /**
     * 发送消息,启动爬虫
     * @param param
     */
    private void sendLoginSuccessMessage(HttpResult result, OperatorParam param) {
        if (null != result && result.getStatus()) {
            String sendLoginStage = PropertiesConfiguration.getInstance().get(
                RedisKeyPrefixEnum.SEND_LOGIN_MSG_STAGE.getRedisKey(param.getWebsiteName()), "VALIDATE_BILL_DETAIL");
            if (StringUtils.equals(sendLoginStage, param.getFormType())) {
                messageService.sendLoginSuccessMessage(param.getTaskId(), param.getWebsiteName());
                logger.info("发送消息,启动爬虫,taskId={},websiteName={}", param.getTaskId(), param.getWebsiteName());
            }
        }

    }
}
