package com.datatrees.crawler.plugin.login;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.crawler.plugin.AbstractRawdataPlugin;
import com.datatrees.crawler.plugin.login.AbstractLoginPlugin.ContentType;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 抓取过程中如果出现图片验证码发给APP端识别,并返回
 * 
 * 1.程序阻塞
 * 2.不支持刷新图片验证码,但是失败后会重新发送指令给前端
 * 3.APP端输错可以重试
 * Created by zhouxinghai on 2017/5/24
 */
public abstract class AbstractPicPlugin extends AbstractRawdataPlugin {

    private Logger logger = LoggerFactory.getLogger(AbstractPicPlugin.class);

    private String tips;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Map<String, String> doProcess(Map<String, String> paramMap) throws Exception {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        logger.info("start run pic plugin!taskId={},websiteName={}", taskId, websiteName);

        Map<String, String> resultMap = new HashMap<>();

        //这里很重要,插件可能会用
        Map<String, String> paramsMap = perpareParam(paramMap);

        if (null == taskId || StringUtils.isBlank(websiteName)) {
            logger.error("pic plugin's taskId or websitename is empty! taskId={},websiteName={}", taskId, websiteName);
            resultMap.put(AttributeKey.ERROR_CODE, "-1");
            //貌似没用
            resultMap.put(AttributeKey.ERROR_MESSAGE, "taskId or websitename is empty");
            return resultMap;
        }

        String resultMessage = null;
        //当前重试次数
        int retry = 0;
        //用户输入图片验证码次数
        int inputPicCount = 0;
        // 发送任务日志
        getMessageService().sendTaskLog(taskId, "等待用户输入图片验证码");
        //5分钟超时
        long maxInterval = TimeUnit.MINUTES.toMillis(5) + System.currentTimeMillis();
        do {
            String picCode = requestPicCode(paramsMap);
            if (StringUtils.isEmpty(picCode)) {
                logger.error("plugin request picCode error! taskId={},websiteName={}", taskId, websiteName);
                TimeUnit.SECONDS.sleep(60);
                continue;
            }
            logger.info("plugin request picCode success! taskId={},websiteName={}", taskId, websiteName);

            //发送MQ指令
            Map<String, String> data = new HashMap<>();
            data.put(AttributeKey.REMARK, picCode);
            preSendMessageToApp(data);
            if (StringUtils.isNotBlank(tips)) {
                data.put(AttributeKey.TIPS, tips);
            }

            String directiveId = getMessageService().sendDirective(taskId, DirectiveEnum.REQUIRE_PICTURE.getCode(),
                GsonUtils.toJson(data));
            DirectiveResult<Map<String, Object>> receiveDirective = getRedisService().getDirectiveResult(directiveId,
                getMaxInterval(websiteName), TimeUnit.MILLISECONDS);
            if (null == receiveDirective) {
                logger.error("wait user input piccode timeout,taskId={},websiteName={},directiveId={}", taskId,
                    websiteName, directiveId);
                continue;
            }
            if (null == receiveDirective.getData() || !receiveDirective.getData().containsKey(AttributeKey.CODE)) {
                logger.error("invalid receiveDirective,taskId={},websiteName={},directiveId={},receiveDirective={}",
                    taskId, websiteName, directiveId, GsonUtils.toJson(receiveDirective));
                continue;
            }
            inputPicCount++;
            //返回不为空就"认为是正确",实际大概就是图片验证码不为空,就返回图片验证码,诡异的代码,踩坑了......
            String inputCode = receiveDirective.getData().get(AttributeKey.CODE).toString();
            resultMessage = vaildPicCode(paramsMap, inputCode);
            if (StringUtils.isNotEmpty(resultMessage)) {
                logger.info("code vaild success! taskId={},websiteName={},code={},retry={}", taskId, websiteName,
                    receiveDirective.getData(), retry);
                //将结果返回给插件调用的地方,作为field的值,一般返回的就是图片验证码,有的和短信验证码一起验证,有的会设置not-empty=true属性
                resultMap.put(PluginConstants.FIELD, resultMessage);
                getMessageService().sendTaskLog(taskId, "图片验证码校验成功");
                return resultMap;
            }
            logger.error("code vaild failed! taskId={},websiteName={},code={},retry={},inputPicCount={}", taskId,
                websiteName, receiveDirective.getData(), retry, inputPicCount);
        } while (System.currentTimeMillis() < maxInterval);
        getMessageService().sendTaskLog(taskId, inputPicCount == 0 ? "图片验证码校验超时" : "图片验证码校验失败");
        throw new ResultEmptyException("get pic code error, inputPicCount:" + inputPicCount);
    }

    @Override
    public String process(String... args) throws Exception {
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils.fromJson(args[0],
            new TypeToken<LinkedHashMap<String, String>>() {
            }.getType());
        return GsonUtils.toJson(doProcess(paramMap));
    }

    public Map<String, String> perpareParam(Map<String, String> parms) {
        logger.debug("default perpareParam directly return!");
        return parms;
    }

    public abstract String requestPicCode(Map<String, String> parms);

    /**
     * 验证图形验证码是否有效
     * @param parms 附加参数
     * @param pidCode 图形验证码
     * @return true:不为空 false:空
     */
    public abstract String vaildPicCode(Map<String, String> parms, String pidCode);

    protected int getMaxInterval(String websiteName) {
        return PropertiesConfiguration.getInstance().getInt(websiteName + ".picCode.max.waittime", 2 * 60 * 1000);
    }

    protected Object getResponseByWebRequest(LinkNode linkNode, ContentType contentType, Integer retyrcount) {
        boolean flag = false;
        Object result = null;
        Request newRequest = new Request();
        RequestUtil.setProcessorContext(newRequest, PluginFactory.getProcessorContext());
        RequestUtil.setConf(newRequest, PropertiesConfiguration.getInstance());
        RequestUtil.setContext(newRequest, PluginFactory.getProcessorContext().getContext());
        RequestUtil.setRetryCount(newRequest, retyrcount);
        Response newResponse = new Response();
        try {
            RequestUtil.setCurrentUrl(newRequest, linkNode);
            ServiceBase serviceProcessor = ProcessorFactory.getService(null);
            serviceProcessor.invoke(newRequest, newResponse);
        } catch (Exception e) {
            logger.error("execute request error! " + e.getMessage(), e);
            flag = true;
        }
        switch (contentType) {
            case ValidCode:
                result = flag ? new byte[0] : ResponseUtil.getProtocolResponse(newResponse).getContent().getContent();
                break;
            case Content:
                result = flag ? StringUtils.EMPTY : StringUtils.defaultString(RequestUtil.getContent(newRequest));
                break;
            default:
                break;
        }
        return result;
    }

}