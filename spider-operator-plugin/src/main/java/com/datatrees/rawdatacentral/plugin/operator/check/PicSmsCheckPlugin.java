package com.datatrees.rawdatacentral.plugin.operator.check;

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
import com.datatrees.spider.operator.api.OperatorApi;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.spider.operator.domain.model.FormType;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.spider.share.domain.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 爬取过程中校验-->图片和短信表单
 * 步骤:图片验证码-->短信验证码-->提交校验
 * Created by zhouxinghai on 2017/7/31
 */
public class PicSmsCheckPlugin extends AbstractClientPlugin {

    private static final Logger                   logger = LoggerFactory.getLogger(PicSmsCheckPlugin.class);
    private              OperatorApi              pluginService;
    private              MessageService           messageService;
    private              RedisService             redisService;
    private              MonitorService           monitorService;
    //超时时间120秒
    private              long                     timeOut = 120;
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

        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);

        TaskUtils.updateCookies(taskId, ProcessorContextUtil.getCookieMap(context));

        TaskUtils.initTaskContext(taskId, context.getContext());
        Map<String, String> map = JSON.parseObject(args[args.length - 1], new TypeReference<Map<String, String>>() {});
        fromType = map.get(AttributeKey.FORM_TYPE);
        CheckUtils.checkNotBlank(fromType, "fromType is empty");
        logger.info("图片和短信校验插件启动,taskId={},websiteName={},fromType={}", taskId, websiteName, fromType);

        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->图片和短信校验启动-->成功", FormType.getName(fromType)));
        //验证失败直接抛出异常
        validatePicCode(taskId, websiteName);

        String cookieString = TaskUtils.getCookieString(taskId);
        ProcessorContextUtil.setCookieString(context, cookieString);

        Map<String, String> shares = TaskUtils.getTaskShares(taskId);
        for (Map.Entry<String, String> entry : shares.entrySet()) {
            context.setString(entry.getKey(), entry.getValue());
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
            String directiveId = messageService.sendDirective(taskId, DirectiveEnum.REQUIRE_PICTURE.getCode(), JSON.toJSONString(data),fromType);
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
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验图片验证码-->成功", FormType.getName(fromType)));
                messageService.sendTaskLog(taskId, "图片验证码校验成功,下一步校验短信验证码");
                //图片验证码结束,进入短信验证
                submit(taskId, websiteName);
                return;
            }
            if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->线程-->失败", FormType.getName(fromType)), ErrorCode.TASK_CANCEL);
                logger.error("验证图片验证码-->用户刷新/取消任务. threadId={},taskId={},websiteName={}", Thread.currentThread().getId(), taskId, websiteName);
                throw new CommonException(ErrorCode.TASK_INTERRUPTED_ERROR);
            }
            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验-->失败", FormType.getName(fromType)));
        } while (retry++ < maxRetry);
        //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("图片验证码校验失败,最大重试次数{}", maxRetry));
        throw new ResultEmptyException(ErrorCode.VALIDATE_PIC_CODE_TIMEOUT.getErrorMsg());
    }

    /**
     * 用户输入图片验证码后提交
     * 用户输入图片验证码超时时间120秒
     * @param taskId
     * @param websiteName
     */
    public void submit(Long taskId, String websiteName) throws ResultEmptyException {
        if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->线程-->失败", FormType.getName(fromType)), ErrorCode.TASK_CANCEL);
            logger.error("验证短信验证码-->用户刷新/取消任务. threadId={},taskId={},websiteName={}", Thread.currentThread().getId(), taskId, websiteName);
            throw new CommonException(ErrorCode.TASK_INTERRUPTED_ERROR);
        }
        OperatorParam param = new OperatorParam(fromType, taskId, websiteName);
        HttpResult<Map<String, Object>> result = pluginService.refeshSmsCode(param);
        if (!result.getStatus()) {
            throw new CommonException(ErrorCode.REFESH_SMS_FAIL);
        }

        //发送MQ指令(要求输入短信验证码)
        Map<String, String> data = new HashMap<>();
        data.put(AttributeKey.REMARK, "");
        String directiveId = messageService.sendDirective(taskId, DirectiveEnum.REQUIRE_SMS.getCode(), JSON.toJSONString(data),fromType);
        messageService.sendTaskLog(taskId, "等待用户输入短信验证码");
        //等待用户输入图片验证码,等待120秒
        DirectiveResult<Map<String, Object>> receiveDirective = redisService.getDirectiveResult(directiveId, timeOut, TimeUnit.SECONDS);
        if (null == receiveDirective) {
            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->等待用户输入短信验证码-->失败", FormType.getName(fromType)),
                    ErrorCode.VALIDATE_PIC_CODE_TIMEOUT, "用户输入短信验证码超时,任务即将失败!超时时间(单位:秒):" + timeOut);
            logger.error("等待用户输入短信验证码超时({}秒),taskId={},websiteName={},directiveId={}", timeOut, taskId, websiteName, directiveId);
            //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("等待用户输入短信验证码超时({}秒)", timeOut));
            messageService.sendTaskLog(taskId, "短信验证码校验超时");
            throw new ResultEmptyException(ErrorCode.VALIDATE_SMS_TIMEOUT.getErrorMsg());
        }
        String smsCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
        param.setSmsCode(smsCode);
        result = pluginService.submit(param);
        if (!result.getStatus()) {
            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验短信-->失败", FormType.getName(fromType)));
            messageService.sendTaskLog(taskId, "短信验证码校验失败");
            //短信验证码验证失败重新验证图片验证码
            validatePicCode(taskId, websiteName);
        }
        messageService.sendTaskLog(taskId, "短信验证码校验成功");
        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->校验短信-->成功", FormType.getName(fromType)));
        pluginResult.put(PluginConstants.FIELD, smsCode);
        TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.TASK_SMS_CODE.getRedisKey(fromType), smsCode);
    }
}
