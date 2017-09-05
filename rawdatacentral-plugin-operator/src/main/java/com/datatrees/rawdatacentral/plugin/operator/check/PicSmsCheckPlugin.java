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
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 爬取过程中校验-->图片和短信表单
 * 步骤:图片验证码-->短信验证码-->提交校验
 * Created by zhouxinghai on 2017/7/31
 */
public class PicSmsCheckPlugin extends AbstractClientPlugin {

    private static final Logger                   logger         = LoggerFactory.getLogger(PicSmsCheckPlugin.class);
    private              CrawlerOperatorService   pluginService  = BeanFactoryUtils.getBean(CrawlerOperatorService.class);
    private              MessageService           messageService = BeanFactoryUtils.getBean(MessageService.class);
    private              RedisService             redisService   = BeanFactoryUtils.getBean(RedisService.class);
    //超时时间120秒
    private              long                     timeOut        = 120;
    private              AbstractProcessorContext context        = PluginFactory.getProcessorContext();
    private String fromType;
    private Map<String, String> pluginResult = new HashMap<>();

    @Override
    public String process(String... args) throws Exception {
        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        Map<String, String> map = JSON.parseObject(args[args.length - 1], new TypeReference<Map<String, String>>() {});
        fromType = map.get(AttributeKey.FORM_TYPE);
        CheckUtils.checkNotBlank(fromType, "fromType is empty");
        logger.info("图片和短信校验插件启动,taskId={},websiteName={},fromType={}", taskId, websiteName, fromType);
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
            String directiveId = messageService.sendDirective(taskId, DirectiveEnum.REQUIRE_PICTURE.getCode(), JSON.toJSONString(data));
            //等待用户输入图片验证码,等待120秒
            messageService.sendTaskLog(taskId, "等待用户输入图片验证码");
            DirectiveResult<Map<String, Object>> receiveDirective = redisService.getDirectiveResult(directiveId, timeOut, TimeUnit.SECONDS);
            if (null == receiveDirective) {
                messageService.sendTaskLog(taskId, "图片验证码校验超时");
                logger.error("等待用户输入图片验证码超时({}秒),taskId={},websiteName={},directiveId={}", timeOut, taskId, websiteName, directiveId);
                //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("等待用户输入图片验证码超时({}秒)", timeOut));
                throw new ResultEmptyException(ErrorCode.VALIDATE_PIC_CODE_TIMEOUT.getErrorMsg());
            }

            picCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
            param.setPicCode(picCode);
            result = pluginService.validatePicCode(param);
            if (result.getStatus() || result.getResponseCode() == ErrorCode.NOT_SUPORT_METHOD.getErrorCode()) {
                messageService.sendTaskLog(taskId, "图片验证码校验成功");
                TaskUtils.addTaskShare(taskId, AttributeKey.PIC_CODE, picCode);
                //图片验证码结束,进入短信验证
                submit(taskId, websiteName);
                return;
            }
            if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                logger.error("验证图片验证码-->用户刷新/取消任务. threadId={},taskId={},websiteName={}", Thread.currentThread().getId(), taskId, websiteName);
                throw new CommonException(ErrorCode.TASK_INTERRUPTED_ERROR);
            }
            messageService.sendTaskLog(taskId, "图片验证码校验失败");
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
            logger.error("验证短信验证码-->用户刷新/取消任务. threadId={},taskId={},websiteName={}", Thread.currentThread().getId(), taskId, websiteName);
            throw new CommonException(ErrorCode.TASK_INTERRUPTED_ERROR);
        }
        OperatorParam param = new OperatorParam(fromType, taskId, websiteName);
        HttpResult<Map<String, Object>> result = pluginService.refeshSmsCode(param);
        if (!result.getStatus()) {
            throw new CommonException(ErrorCode.VALIDATE_PIC_CODE_TIMEOUT);
        }

        //发送MQ指令(要求输入短信验证码)
        Map<String, String> data = new HashMap<>();
        data.put(AttributeKey.REMARK, "");
        String directiveId = messageService.sendDirective(taskId, DirectiveEnum.REQUIRE_SMS.getCode(), JSON.toJSONString(data));
        messageService.sendTaskLog(taskId, "等待用户输入短信验证码");
        //等待用户输入图片验证码,等待120秒
        DirectiveResult<Map<String, Object>> receiveDirective = redisService.getDirectiveResult(directiveId, timeOut, TimeUnit.SECONDS);
        if (null == receiveDirective) {
            logger.error("等待用户输入短信验证码超时({}秒),taskId={},websiteName={},directiveId={}", timeOut, taskId, websiteName, directiveId);
            //messageService.sendTaskLog(taskId, websiteName, TemplateUtils.format("等待用户输入短信验证码超时({}秒)", timeOut));
            messageService.sendTaskLog(taskId, "短信验证码校验超时");
            throw new ResultEmptyException(ErrorCode.VALIDATE_SMS_TIMEOUT.getErrorMsg());
        }
        String smsCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
        String picCode = TaskUtils.getTaskShare(taskId, AttributeKey.PIC_CODE);
        param.setSmsCode(smsCode);
        param.setPicCode(picCode);
        result = pluginService.submit(param);
        if (!result.getStatus()) {
            messageService.sendTaskLog(taskId, "短信验证码校验失败");
            //短信验证码验证失败重新验证图片验证码
            validatePicCode(taskId, websiteName);
        }
        messageService.sendTaskLog(taskId, "短信验证码校验成功");
        pluginResult.put(PluginConstants.FIELD, smsCode);
    }
}
