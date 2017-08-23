package com.datatrees.crawler.plugin.login;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.crawler.plugin.AbstractRawdataPlugin;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSMSPlugin extends AbstractRawdataPlugin {

    private Logger logger = LoggerFactory.getLogger(AbstractSMSPlugin.class);

    public Map<String, String> doProcess(Map<String, String> paramMap) throws Exception {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        logger.info("start run sms plugin!taskId={},websiteName={}", taskId, websiteName);

        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        Map<String, String> paramsMap = perpareParam(paramMap);

        paramMap.put("taskId", taskId.toString());

        if (null == taskId || StringUtils.isBlank(websiteName)) {
            logger.error("sms plugin's taskId or websitename is empty! taskId={},websiteName={}", taskId, websiteName);
            resultMap.put(AttributeKey.ERROR_CODE, "-1");
            //貌似没用
            resultMap.put(AttributeKey.ERROR_MESSAGE, "taskId or websitename is empty");
            return resultMap;
        }
        if (!preSMSCode(paramsMap)) {
            logger.warn("no need to send sms ,taskId={},websiteName={}", taskId, websiteName);
            resultMap.putAll(paramMap);
            return resultMap;
        }

        //当前重试次数
        int retry = 0;
        //用户输入短信验证码次数
        int inputSmsCount = 0;
        boolean hasSms = false;
        //5分钟超时
        long maxInterval = TimeUnit.MINUTES.toMillis(5) + System.currentTimeMillis();
        do {
            boolean flag = requestSMSCode(paramsMap);
            if (!flag) {
                logger.error("plugin request smsCode error!taskId={},websiteName={}", taskId, websiteName);
                TimeUnit.SECONDS.sleep(60);
                continue;
            }
            hasSms = true;
            logger.info("plugin request smsCode success!taskId={},websiteName={}", taskId, websiteName);
            // 发送任务日志
            getMessageService().sendTaskLog(taskId, "等待用户输入短信验证码");
            //发送MQ指令
            Map<String, String> data = new HashMap<String, String>();
            data.put(AttributeKey.REMARK, StringUtils.EMPTY);
            preSendMessageToApp(data);
            String directiveId = getMessageService().sendDirective(taskId, DirectiveEnum.REQUIRE_SMS.getCode(), GsonUtils.toJson(data));
            //保存状态到redis
            //等待APP处理完成,并通过dubbo将数据写入redis
            DirectiveResult<Map<String, Object>> receiveDirective = getRedisService().getDirectiveResult(directiveId, getMaxInterval(websiteName), TimeUnit.MILLISECONDS);
            if (null == receiveDirective) {
                logger.error("wait user input smscode timeout,taskId={},websiteName={},directiveId={}", taskId, websiteName, directiveId);
                continue;
            }
            if (null == receiveDirective.getData() || !receiveDirective.getData().containsKey(AttributeKey.CODE)) {
                logger.error("invalid receiveDirective,taskId={},websiteName={},directiveId={},receiveDirective={}", taskId, websiteName, directiveId, GsonUtils.toJson(receiveDirective));
                continue;
            }
            inputSmsCount++;
            PluginFactory.getProcessorContext().getProcessorResult().put("smsCodeCount", inputSmsCount);
            //返回不为空就"认为是正确",实际大概就是短信验证码不为空,就返回短信验证码,诡异的代码,踩坑了......
            String inputCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
            if (vaildSMSCode(paramsMap, inputCode)) {
                logger.info("code vaild success! taskId={},websiteName={},code={},retry={}", taskId, websiteName, receiveDirective.getData(), retry);
                //将结果返回给插件调用的地方,作为field的值,一般返回的就是短信验证码,有的和短信验证码一起验证,有的会设置not-empty=true属性
                resultMap.put(PluginConstants.FIELD, inputCode);
                getMessageService().sendTaskLog(taskId, "短信验证码校验成功");
                return resultMap;
            }
            logger.error("code vaild failed! taskId={},websiteName={},code={},retry={},inputSmsCount={}", taskId, websiteName, inputCode, retry, inputSmsCount);

        } while (System.currentTimeMillis() < maxInterval);
        if (hasSms) {
            getMessageService().sendTaskLog(taskId, inputSmsCount == 0 ? "短信验证码校验超时" : "短信验证码校验失败");
            throw new ResultEmptyException("get pic code error,inputSmsCount:" + inputSmsCount);
        }
        return resultMap;
    }

    public Map<String, String> perpareParam(Map<String, String> parms) {
        logger.debug("default perpareParam directly return!");
        return parms;
    }

    public boolean preSMSCode(Map<String, String> parms) {
        return true;
    }

    public String postSMSCode(Map<String, String> parms, String resultCode) {
        return resultCode;
    }

    protected void setSendMessageTips(Map<String, String> parms, String title, String tips) {
        if (parms != null) {
            parms.put("tips", tips);
            parms.put("title", title);
        }
    }

    public boolean isSmsCodeNotEmpty(Map<String, String> parms) {
        return true;
    }

    public abstract boolean requestSMSCode(Map<String, String> parms);

    public abstract boolean vaildSMSCode(Map<String, String> parms, String smsCode);

    @Override
    protected int getMaxInterval(String websiteName) {
        return PropertiesConfiguration.getInstance().getInt(websiteName + ".smsCode.max.waittime", 2 * 60 * 1000);
    }

    @Override
    public String process(String... args) throws Exception {
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils.fromJson(args[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        return GsonUtils.toJson(doProcess(paramMap));
    }

}
