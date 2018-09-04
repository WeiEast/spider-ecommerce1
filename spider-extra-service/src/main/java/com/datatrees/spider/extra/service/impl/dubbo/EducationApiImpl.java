/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.extra.service.impl.dubbo;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.spider.extra.api.EducationApi;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2017/12/1.
 */
@Service
public class EducationApiImpl implements EducationApi {

    private static final Logger              logger               = LoggerFactory.getLogger(EducationApiImpl.class);

    /**
     * 默认格式格式化成JSON后发送的字符编码
     */
    private static final String              DEFAULT_CHARSET_NAME = "UTF-8";

    @Resource
    private              CommonPluginService commonPluginApi;

    @Resource
    private              MonitorService      monitorService;

    @Resource
    private              MessageService      messageService;


    private static final String WEBSITE_NAME = "chsi.com.cn";

    @Override
    public HttpResult<Object> loginInit(CommonPluginParam param) {
        param.setWebsiteName(WEBSITE_NAME);
        if (param.getTaskId() == null || param.getWebsiteName() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        param.setFormType(FormType.LOGIN);
        //提供username字段，防止初始化出错
        param.setUsername("未知");
        HttpResult<Object> result = commonPluginApi.init(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(taskId, websiteName, "学信网登录-->初始化-->失败");
            logger.error("学信网登录-->初始化-->失败,result={}", result);
            return result;
        }
        monitorService.sendTaskLog(taskId, websiteName, "学信网登录-->初始化-->成功");
        logger.info("学信网登录-->初始化-->成功,result={}", result);
        return result;
    }

    @Override
    public HttpResult<Object> loginSubmit(CommonPluginParam param) {
        param.setWebsiteName(WEBSITE_NAME);
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getUsername() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        Long taskId = param.getTaskId();
        String websiteName = param.getWebsiteName();
        param.setFormType(FormType.LOGIN);
        HttpResult<Object> result = commonPluginApi.submit(param);
        HashMap<String, Object> loginStatus = (HashMap<String, Object>) result.getData();
        if (!result.getStatus() || !(result.getData() != null && "login_success".equals((loginStatus.get("directive"))))) {
            monitorService.sendTaskLog(taskId, websiteName, "学信网登陆-->校验-->失败");
            logger.error("学信网登陆-->校验-->失败,result={}", result);
            return result;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId);
        map.put(AttributeKey.WEBSITE_NAME, websiteName);
        String cookies = TaskUtils.getCookieString(taskId);
        map.put(AttributeKey.COOKIE, cookies);
        messageService.sendMessage(TopicEnum.SPIDER_EXTRA.getCode(), TopicTag.LOGIN_INFO.getTag(), map, DEFAULT_CHARSET_NAME);
        logger.info("学信网，启动爬虫成功,result={}", result);
        return result;
    }

    @Override
    public HttpResult<Object> registerInit(CommonPluginParam param) {
        param.setWebsiteName(WEBSITE_NAME);
        if (param.getTaskId() == null || param.getWebsiteName() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setFormType(FormType.REGISTER);
        HttpResult<Object> result = commonPluginApi.init(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->初始化-->失败");
            logger.error("学信网注册-->初始化-->失败,result={}", result);
            return result;
        }
        monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->初始化-->成功");
        logger.info("学信网注册-->初始化-->成功,result={}", result);
        return result;
    }

    @Override
    public HttpResult<Object> registerRefeshPicCode(CommonPluginParam param) {
        param.setWebsiteName(WEBSITE_NAME);
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setFormType(FormType.REGISTER);
        HttpResult<Object> result = commonPluginApi.refeshPicCode(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->刷新图片验证码-->失败");
            logger.error("学信网注册-->刷新图片验证码-->失败,result={}", result);
            return result;
        }
        monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网注册-->刷新图片验证码-->成功");
        logger.info("学信网注册-->刷新图片验证码-->成功,param={}", param);
        return result;
    }

    @Override
    public HttpResult<Object> registerValidatePicCodeAndSendSmsCode(CommonPluginParam param) {
        param.setWebsiteName(WEBSITE_NAME);
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getPicCode() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setFormType(FormType.REGISTER);
        HttpResult<Object> result = commonPluginApi.refeshSmsCode(param);
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
    public HttpResult<Object> registerSubmit(CommonPluginParam param) {
        param.setWebsiteName(WEBSITE_NAME);
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null || param.getSmsCode() == null ||
                param.getPassword() == null || param.getRealName() == null || param.getIdCard() == null || param.getIdCardType() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setFormType(FormType.REGISTER);
        HttpResult<Object> result = commonPluginApi.submit(param);
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
