package com.datatrees.rawdatacentral.plugin.operator.check.validate_bill_detail;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.share.MessageService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 详单校验-->图片和短信表单
 * 步骤:图片验证码-->短信验证码-->提交校验
 * Created by zhouxinghai on 2017/7/31
 */
public class PicSmsCheckPlugin extends AbstractClientPlugin {

    private static final Logger    logger   = LoggerFactory.getLogger(PicSmsCheckPlugin.class);

    private CrawlerOperatorService pluginService;

    private MessageService         messageService;

    private RedisService           redisService;

    private static final String    formType = FormType.VALIDATE_BILL_DETAIL;

    //超时时间120秒
    private long                   timeOut  = 120;

    private long                   endTime  = 0;

    {
        pluginService = BeanFactoryUtils.getBean(CrawlerOperatorService.class);
        messageService = BeanFactoryUtils.getBean(MessageService.class);
        redisService = BeanFactoryUtils.getBean(RedisService.class);
    }

    @Override
    public String process(String... args) throws Exception {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        logger.info("详单-->插件启动,taskId={},websiteName={}", taskId, websiteName);

        return null;
    }

    /**
     * 图片验证码最大次数5次,
     * 用户输入图片验证码超时时间120秒
     * @param taskId
     * @param websiteName
     * @throws InterruptedException
     */
    public void validatePicCode(Long taskId, String websiteName) throws InterruptedException {
        int retry = 0, maxRetry = 5;
        do {
            OperatorParam param = new OperatorParam(formType, taskId, websiteName);
            HttpResult<Map<String, Object>> result = pluginService.refeshPicCode(param);
            String picCode = result.getData().get(AttributeKey.CODE).toString();
            //发送MQ指令(要求输入图片验证码)
            Map<String, String> data = new HashMap<>();
            data.put(AttributeKey.REMARK, picCode);
            String directiveId = messageService.sendDirective(taskId, DirectiveEnum.REQUIRE_PICTURE.getCode(),
                GsonUtils.toJson(data));
            //等待用户输入图片验证码,等待120秒
            DirectiveResult<Map<String, Object>> receiveDirective = redisService.getDirectiveResult(directiveId,
                timeOut, TimeUnit.MILLISECONDS);
            if (null == receiveDirective) {
                logger.error("详单-->等待用户输入图片验证码超时({}秒),taskId={},websiteName={},directiveId={}", timeOut, taskId,
                    websiteName, directiveId);
                throw new RuntimeException(ErrorCode.VALIDATE_PIC_CODE_FAIL_TIMEOUT.getErrorMessage());
            }
            picCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
            param.setPicCode(picCode);
            result = pluginService.validatePicCode(param);
            if(result.getStatus()){

            }
            if(result)
        } while (retry++ < 5);
    }

    private void updateEndTime() {
        //5分钟不操作,自动超时
        endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(300);
    }

}
