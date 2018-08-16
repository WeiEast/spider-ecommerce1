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

package com.datatrees.spider.operator.plugin.check;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.util.ThreadInterruptedUtil;
import com.treefinance.crawler.framework.context.AbstractProcessorContext;
import com.treefinance.crawler.framework.context.ProcessorContextUtil;
import com.treefinance.crawler.framework.exception.ResultEmptyException;
import com.treefinance.crawler.framework.extension.plugin.AbstractClientPlugin;
import com.treefinance.crawler.framework.extension.plugin.PluginConstants;
import com.treefinance.crawler.framework.extension.plugin.PluginFactory;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.directive.DirectiveEnum;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.exception.CommonException;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import com.datatrees.spider.operator.api.OperatorApi;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 爬取过程中校验-->短信表单
 * 步骤:短信验证码-->提交校验
 * Created by zhouxinghai on 2017/7/31
 */
public class SmsCheckPlugin extends AbstractClientPlugin {

    private static final Logger                   logger       = LoggerFactory.getLogger(SmsCheckPlugin.class);

    private              OperatorApi              pluginService;

    private              MessageService           messageService;

    private              RedisService             redisService;

    //超时时间120秒
    private              long                     timeOut      = 120;

    private              AbstractProcessorContext context;

    private              String                   fromType;

    private              Map<String, String>      pluginResult = new HashMap<>();

    private              MonitorService           monitorService;

    @Override
    public String process(String... args) throws Exception {
        pluginService = BeanFactoryUtils.getBean(OperatorApi.class);
        messageService = BeanFactoryUtils.getBean(MessageService.class);
        redisService = BeanFactoryUtils.getBean(RedisService.class);
        context = PluginFactory.getProcessorContext();
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);

        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);

        TaskUtils.updateCookies(taskId, ProcessorContextUtil.getCookieMap(context));

        TaskUtils.initTaskContext(taskId, context.getContext());
        Map<String, String> map = JSON.parseObject(args[1], new TypeReference<Map<String, String>>() {});
        fromType = map.get(AttributeKey.FORM_TYPE);
        logger.info("短信校验插件启动,taskId={},websiteName={},fromType={}", taskId, websiteName, fromType);
        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->短信校验启动-->成功", FormType.getName(fromType)));
        //验证失败直接抛出异常
        validateSmsCode(taskId, websiteName);

        String cookieString = TaskUtils.getCookieString(taskId);
        ProcessorContextUtil.setCookieString(context, cookieString);

        Map<String, String> shares = TaskUtils.getTaskShares(taskId);
        for (Map.Entry<String, String> entry : shares.entrySet()) {
            context.setString(entry.getKey(), entry.getValue());
        }

        return JSON.toJSONString(pluginResult);
    }

    /**
     * 短信验证码最大次数5次,
     * 用户输入短信验证码超时时间120秒
     * @param taskId
     * @param websiteName
     */
    public void validateSmsCode(Long taskId, String websiteName) throws Exception {
        int retry = 0, maxRetry = 5;
        do {
            OperatorParam param = new OperatorParam(fromType, taskId, websiteName);

            HttpResult<Map<String, Object>> result = pluginService.refeshSmsCode(param);
            if (!result.getStatus()) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    logger.error("validateSmsCode error taskId={},websiteName={}", taskId, websiteName, e);
                }
                continue;
            }

            //发送MQ指令(要求输入短信验证码)
            Map<String, String> data = new HashMap<>();
            data.put(AttributeKey.REMARK, "");
            String directiveId = messageService.sendDirective(taskId, DirectiveEnum.REQUIRE_SMS.getCode(), JSON.toJSONString(data), fromType);
            //等待用户输入短信验证码,等待120秒
            messageService.sendTaskLog(taskId, "等待用户输入短信验证码");

            DirectiveResult<Map<String, Object>> receiveDirective = redisService.getDirectiveResult(directiveId, timeOut, TimeUnit.SECONDS);
            if (null == receiveDirective) {
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->等待用户输入短信验证码-->失败", FormType.getName(fromType)),
                        ErrorCode.VALIDATE_PIC_CODE_TIMEOUT, "用户输入短信验证码超时,任务即将失败!超时时间(单位:秒):" + timeOut);
                logger.error("等待用户输入短信验证码超时({}秒),taskId={},websiteName={},directiveId={}", timeOut, taskId, websiteName, directiveId);
                //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("等待用户输入短信验证码超时({}秒)", timeOut));
                messageService.sendTaskLog(taskId, "短信验证码校验超时");
                throw new CommonException(ErrorCode.VALIDATE_SMS_TIMEOUT);
            }

            String smsCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
            param.setSmsCode(smsCode);
            result = pluginService.submit(param);
            if (result.getStatus() || result.getResponseCode() == ErrorCode.NOT_SUPORT_METHOD.getErrorCode()) {
                context.setString(AttributeKey.SMS_CODE, smsCode);
                TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.TASK_SMS_CODE.getRedisKey(fromType), smsCode);
                pluginResult.put(PluginConstants.FIELD, smsCode);
                messageService.sendTaskLog(taskId, "短信验证码校验成功");
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验短信-->成功", FormType.getName(fromType)));
                return;
            }
            if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->线程-->失败", FormType.getName(fromType)), ErrorCode.TASK_CANCEL);
                logger.error("验证短信验证码-->用户刷新/取消任务. threadId={},taskId={},websiteName={}", Thread.currentThread().getId(), taskId, websiteName);
                throw new CommonException(ErrorCode.TASK_INTERRUPTED_ERROR);
            }
            messageService.sendTaskLog(taskId, "短信验证码校验失败");
        } while (retry++ < maxRetry);
        //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("短信验证码校验失败,最大重试次数{}", maxRetry));
        throw new ResultEmptyException(ErrorCode.VALIDATE_SMS_TIMEOUT.getErrorMsg());
    }

}
