package com.datatrees.rawdatacentral.core.dubbo;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.protocol.util.CookieFormater;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RpcEducationService;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.domain.enums.TopicTag;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.EducationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyanjia on 2017/12/1.
 */
@Service
public class RpcEducationServiceImpl implements RpcEducationService {

    private static final Logger logger = LoggerFactory.getLogger(RpcEducationServiceImpl.class);
    /**
     * 默认格式格式化成JSON后发送的字符编码
     */
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    @Resource
    private EducationService educationService;
    @Resource
    private MonitorService monitorService;
    @Resource
    private MessageService messageService;

    @Override
    public HttpResult<Map<String, Object>> loginInit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        HttpResult<Map<String, Object>> result = educationService.loginInit(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(taskId, websiteName, "学信网登录-->初始化-->失败");
            logger.error("学信网登录-->初始化-->失败,param={}",JSON.toJSONString(param));
            return result;
        }
        monitorService.sendTaskLog(taskId, websiteName, "学信网登录-->初始化-->成功");
        logger.info("学信网登录-->初始化-->成功,param={}", JSON.toJSONString(param));
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> loginSubmit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getLoginName() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        HttpResult<Map<String, Object>> result = educationService.loginSubmit(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(taskId, websiteName, "学信网登陆-->校验-->失败");
            logger.error("学信网登陆-->校验-->失败,param={}",JSON.toJSONString(param));
            return result;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        String cookies = TaskUtils.getCookieString(taskId);
        map.put(AttributeKey.COOKIE, cookies);
        messageService.sendMessage(TopicEnum.RAWDATA_INPUT.getCode(), TopicTag.LOGIN_INFO.getTag(), map, DEFAULT_CHARSET_NAME);
        logger.info("学信网，启动爬虫成功,param={}",JSON.toJSONString(param));
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> registerRefeshPicCode(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }

        HttpResult<Map<String, Object>> result = educationService.registerRefeshPicCode(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->刷新图片验证码-->失败");
            logger.error("学信网注册-->刷新图片验证码-->失败,param={}",JSON.toJSONString(param));
            return result;
        }
        monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->刷新图片验证码-->成功");
        logger.info("学信网注册-->刷新图片验证码-->成功,param={}",JSON.toJSONString(param));
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> registerValidatePicCodeAndSendSmsCode(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getPicCode() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = educationService.registerValidatePicCodeAndSendSmsCode(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->校验图片验证码-->失败");
            logger.error("学信网注册-->校验图片验证码-->失败");
            return result;
        }
        monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->校验图片验证码-->成功");
        logger.info("学信网注册-->校验图片验证码-->成功");
        return result;
    }

    @Override
    public HttpResult<Map<String, Object>> registerSubmit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null || param.getSmsCode() == null || param.getPwd() == null
                || param.getSurePwd() == null || param.getRealName() == null || param.getIdCard() == null || param.getIdCardType() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = educationService.registerSubmit(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->校验信息-->失败");
            logger.error("学信网注册-->校验信息-->失败");
            return result;
        }
        monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->校验信息-->注册成功");
        logger.info("学信网注册-->校验信息-->注册成功");
        return result;
    }


}
