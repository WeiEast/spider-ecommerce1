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
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.directive.DirectiveEnum;
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
 * 爬取过程中校验-->图片和短信表单
 * 步骤:图片验证码-->短信验证码-->提交校验
 * Created by zhouxinghai on 2017/7/31
 */
public class PicCheckPlugin extends AbstractClientPlugin {

    private static final Logger                   logger       = LoggerFactory.getLogger(PicSmsCheckPlugin.class);

    private              OperatorApi              pluginService;

    private              MessageService           messageService;

    private              RedisService             redisService;

    private              MonitorService           monitorService;

    //超时时间120秒
    private              long                     timeOut      = 120;

    private              AbstractProcessorContext context;

    private              String                   fromType;

    private              Map<String, String>      pluginResult = new HashMap<>();

    @Override
    public String process(String... args) throws Exception {
        pluginService = BeanFactoryUtils.getBean(OperatorApi.class);
        messageService = BeanFactoryUtils.getBean(MessageService.class);
        redisService = BeanFactoryUtils.getBean(RedisService.class);
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        context = PluginFactory.getProcessorContext();
        pluginResult = new HashMap<>();

        String websiteName = this.context.getWebsiteName();
        Long taskId = this.context.getLong(AttributeKey.TASK_ID);
        TaskUtils.updateCookies(taskId, ProcessorContextUtil.getCookieMap(context));
        TaskUtils.initTaskContext(taskId, context.getContext());
        Map<String, String> map = JSON.parseObject(args[args.length - 1], new TypeReference<Map<String, String>>() {});
        fromType = map.get(AttributeKey.FORM_TYPE);
        CheckUtils.checkNotBlank(fromType, "fromType is empty");
        logger.info("图片和短信校验插件启动,taskId={},websiteName={},fromType={}", taskId, websiteName, fromType);
        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->图片校验启动-->成功", FormType.getName(fromType)));

        //验证失败直接抛出异常
        validatePicCode(taskId, websiteName);

        String cookieString = TaskUtils.getCookieString(taskId);
        ProcessorContextUtil.setCookieString(this.context, cookieString);

        Map<String, String> shares = TaskUtils.getTaskShares(taskId);
        for (Map.Entry<String, String> entry : shares.entrySet()) {
            this.context.setString(entry.getKey(), entry.getValue());
        }

        return JSON.toJSONString(pluginResult);
    }

    /**
     * 图片验证码最大次数5次,
     * 用户输入图片验证码超时时间120秒
     * @param taskId
     * @param websiteName
     */
    public void validatePicCode(Long taskId, String websiteName) throws ResultEmptyException {
        int retry = 0, maxRetry = 5;
        do {
            OperatorParam param = new OperatorParam(fromType, taskId, websiteName);

            HttpResult<Map<String, Object>> result = pluginService.refeshPicCode(param);
            if (!result.getStatus()) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    logger.error("validatePicCode error taskId={},websiteName={}", taskId, websiteName, e);
                }
                continue;
            }

            String picCode = result.getData().get(AttributeKey.PIC_CODE).toString();
            //发送MQ指令(要求输入图片验证码)
            Map<String, String> data = new HashMap<>();
            data.put(AttributeKey.REMARK, picCode);
            String directiveId = messageService.sendDirective(taskId, DirectiveEnum.REQUIRE_PICTURE.getCode(), JSON.toJSONString(data), fromType);
            //等待用户输入图片验证码,等待120秒
            messageService.sendTaskLog(taskId, "等待用户输入图片验证码");
            DirectiveResult<Map<String, Object>> receiveDirective = redisService.getDirectiveResult(directiveId, timeOut, TimeUnit.SECONDS);
            if (null == receiveDirective) {
                messageService.sendTaskLog(taskId, "图片验证码校验超时");
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->等待用户输入图片验证码-->失败", FormType.getName(fromType)),
                        ErrorCode.VALIDATE_PIC_CODE_TIMEOUT, "用户输入图片验证码超时,任务即将失败!超时时间(单位:秒):" + timeOut);
                logger.error("等待用户输入图片验证码超时({}秒),taskId={},websiteName={},directiveId={}", timeOut, taskId, websiteName, directiveId);
                //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("等待用户输入图片验证码超时({}秒)", timeOut));
                throw new ResultEmptyException(ErrorCode.VALIDATE_PIC_CODE_TIMEOUT.getErrorMsg());
            }

            picCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
            param.setPicCode(picCode);
            result = pluginService.validatePicCode(param);
            if (result.getStatus() || result.getResponseCode() == ErrorCode.NOT_SUPORT_METHOD.getErrorCode()) {
                messageService.sendTaskLog(taskId, "图片验证码校验成功");
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验图片验证码-->成功", FormType.getName(fromType)));
                pluginResult.put(PluginConstants.FIELD, picCode);
                return;
            }
            if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->线程-->失败", FormType.getName(fromType)), ErrorCode.TASK_CANCEL);
                logger.error("验证图片验证码-->用户刷新/取消任务. threadId={},taskId={},websiteName={}", Thread.currentThread().getId(), taskId, websiteName);
                throw new CommonException(ErrorCode.TASK_INTERRUPTED_ERROR);
            }
            messageService.sendTaskLog(taskId, "图片验证码校验失败");
        } while (retry++ < maxRetry);
        //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("图片验证码校验失败,最大重试次数{}", maxRetry));
        throw new ResultEmptyException(ErrorCode.VALIDATE_PIC_CODE_TIMEOUT.getErrorMsg());
    }
}
