package com.datatrees.crawler.plugin.login;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.ThreadInterruptedUtil;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.*;
import com.datatrees.crawler.core.processor.common.resource.DataResource;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.crawler.plugin.AbstractRawdataPlugin;
import com.datatrees.crawler.plugin.qrcode.QRCodeVerification;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.DirectiveRedisCode;
import com.datatrees.rawdatacentral.domain.constant.DirectiveType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.exception.LoginFailException;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoginPlugin extends AbstractRawdataPlugin implements Login {

    private static Logger logger                = LoggerFactory.getLogger(AbstractLoginPlugin.class);
    private static String REDIS_PREFIX          = PropertiesConfiguration.getInstance().get("core.redis.redis.prefix", "rawdata_");
    private static long   defaultTimeToLiveTime = PropertiesConfiguration.getInstance().getLong("data.default.ttl.time", 3600 * 24 * 2);
    protected QRCodeVerification qRCodeVerification;
    int refreshCodeCount = 0;
    int loginCount       = 0;
    int qrCodeCount      = 0;
    int verifyCodeCount  = 0;
    private MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);

    /**
     * @return the qRCodeVerification
     */
    public QRCodeVerification getqRCodeVerification() {
        return qRCodeVerification;
    }

    /**
     * @param qRCodeVerification the qRCodeVerification to set
     */
    public void setqRCodeVerification(QRCodeVerification qRCodeVerification) {
        this.qRCodeVerification = qRCodeVerification;
    }

    public Map<String, Object> preLogin(Map<String, String> preLoginParams) {
        if (logger.isDebugEnabled()) logger.debug("run default preLogin!");
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.putAll(preLoginParams);
        return paramsMap;
    }

    public Map<String, Object> postLogin(Map<String, Object> postLoginParams) {
        if (logger.isDebugEnabled()) logger.debug("run default postLogin!");
        return postLoginParams;
    }

    public abstract String getVeryCode(Map<String, Object> params);

    /**
     * 返回
     * @param params {"username":手机号}
     *               有的要密码
     * @return 短信发送成功/短信验证码发送失败
     */
    public String sendRamdomPassword(Map<String, Object> params) {
        return null;
    }

    @Override
    public String process(String... args) throws Exception {
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        logger.info("start run Login plugin!taskId={},websiteName={}", taskId, websiteName);
        monitorService.sendTaskLog(taskId, "模拟登录开始,等待指令");

        initLoginStatus();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(args[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        // pre login param
        Map<String, Object> preParamMap = preLogin(paramMap);
        if (MapUtils.isNotEmpty(preParamMap) && preParamMap.get("errorCode") != null) {
            logger.error("preLogin run error!");
            throw new InterruptedException(GsonUtils.toJson(preParamMap));
        }
        // spin for app input smsCode
        HashMap<String, Object> loginParamMap = new HashMap<String, Object>();
        loginParamMap.putAll(preParamMap);
        final String groupKey = DirectiveResult.getGroupKey(DirectiveType.PLUGIN_LOGIN, taskId);
        String errorCode = null;
        DirectiveResult<String> sendResult = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN, taskId);
        //5分钟超时
        long maxInterval = TimeUnit.MINUTES.toMillis(5) + System.currentTimeMillis();
        while (System.currentTimeMillis() < maxInterval) {
            if (ThreadInterruptedUtil.isInterrupted(Thread.currentThread())) {
                monitorService.sendTaskLog(taskId, "模拟登录因任务中断失败");
                throw new InterruptedException(
                        "refreshCodeCount:" + refreshCodeCount + ",loginCount:" + loginCount + ",qrCodeCount:" + qrCodeCount + ",verifyCodeCount:" +
                                verifyCodeCount);
            }
            DirectiveResult<Map<String, Object>> directive = getRedisService().getNextDirectiveResult(groupKey, 500, TimeUnit.MILLISECONDS);
            if (null == directive) {
                TimeUnit.MILLISECONDS.sleep(500);
                continue;
            }
            String status = directive.getStatus();
            String directiveId = directive.getDirectiveId();
            Map<String, Object> extra = directive.getData();
            //刷新图片验证码
            if (StringUtils.equals(DirectiveRedisCode.REFRESH_LOGIN_CODE, status)) {
                String remark = getVeryCode(loginParamMap);
                sendResult.fill(DirectiveRedisCode.SERVER_SUCCESS, remark);
                getRedisService().saveDirectiveResult(directiveId, sendResult);
                refreshCodeCount++;
                getMessageService().sendTaskLog(taskId, "刷新图片验证码");
                monitorService.sendTaskLog(taskId, "登录-->图片验证码-->刷新成功!");
                logger.info("refresh login code,taskId={},websiteName={},directiveId={}", taskId, websiteName, directiveId);
                continue;
            }
            //刷新短信验证码
            if (StringUtils.equals(DirectiveRedisCode.REFRESH_LOGIN_RANDOMPASSWORD, status)) {
                //短信发送成功/短信验证码发送失败
                String remark = sendRamdomPassword(extra);
                sendResult.fill(DirectiveRedisCode.SERVER_SUCCESS, remark);
                getRedisService().saveDirectiveResult(directiveId, sendResult);
                refreshCodeCount++;
                getMessageService().sendTaskLog(taskId, "向手机发送短信验证码");
                monitorService.sendTaskLog(taskId, "登录-->短信验证码-->刷新成功!");
                logger.info("send ramdom password,taskId={},websiteName={},directiveId={}", taskId, websiteName, directiveId);
                continue;
            }
            //登陆
            if (StringUtils.equals(DirectiveRedisCode.START_LOGIN, status)) {
                loginCount++;
                loginParamMap.putAll(extra);
                //init context
                this.setLonginInfo(loginParamMap);
                Map<String, Object> loginResultMap = null;
                try {
                    loginResultMap = doLogin(loginParamMap);
                } catch (Exception e) {
                    logger.error("doLogin error,taskId={},websiteName={},directiveId={}", taskId, websiteName, directiveId, e);
                    loginResultMap = new HashMap<>();
                    loginResultMap.put(AttributeKey.ERROR_CODE, ErrorMessage.SERVER_INTERNAL_ERROR);
                }
                if (loginResultMap.get(AttributeKey.ERROR_CODE) != null) {
                    errorCode = null != loginResultMap.get(AttributeKey.ERROR_CODE) ? String.valueOf(loginResultMap.get(AttributeKey.ERROR_CODE)) :
                            StringUtils.EMPTY;
                    sendResult.fill(DirectiveRedisCode.SERVER_FAIL, errorCode);
                    getRedisService().saveDirectiveResult(directiveId, sendResult);
                    getMessageService().sendTaskLog(taskId, "登陆失败", errorCode);
                    monitorService.sendTaskLog(taskId, "登录-->校验失败!", ErrorCode.LOGIN_UNEXPECTED_RESULT.getErrorCode(),
                            ErrorCode.LOGIN_UNEXPECTED_RESULT.getErrorMsg(), errorCode);
                    logger.warn("login fail taskId={},websiteName={},directiveId={},errorCode={},loginCount={}", taskId, websiteName, directiveId,
                            errorCode, loginCount);
                    //清理,避免对下一次登陆产生影响
                    loginResultMap.remove(AttributeKey.ERROR_CODE);
                    loginParamMap.putAll(loginResultMap);
                    continue;
                }
                sendResult.fill(DirectiveRedisCode.SERVER_SUCCESS, null);
                getRedisService().saveDirectiveResult(directiveId, sendResult);
                getMessageService().sendTaskLog(taskId, "登陆成功");
                monitorService.sendTaskLog(taskId, "登录-->校验成功!");
                logger.info("login successs taskId={},websiteName={},directiveId={},loginCount={}", taskId, websiteName, directiveId, loginCount);
                return GsonUtils.toJson(postLogin(loginResultMap));
            }
        }
        if (loginCount == 0) {
            monitorService.sendTaskLog(taskId, "登录-->登录超时,用户登录次数:0");
            logger.info("login timeout taskId={},websiteName={},loginCount={}", taskId, websiteName, loginCount);
            throw new com.datatrees.rawdatacentral.domain.exception.LoginTimeOutException(taskId);
        }
        monitorService.sendTaskLog(taskId, "登录-->登录超时,用户登录次数:" + loginCount);
        logger.info("login fail taskId={},websiteName={},loginCount={},errorCode={}", taskId, websiteName, loginCount, errorCode);
        throw new LoginFailException(taskId, errorCode);
    }

    protected boolean isTimeOut(long startTime, String websiteName) throws LoginTimeOutException {
        long now = System.currentTimeMillis();
        int maxInterval = PropertiesConfiguration.getInstance().getInt(websiteName + ".login.max.waittime", 2 * 60 * 1000);
        if (now <= startTime + maxInterval) {
            return false;
        } else {
            // mark the task login time out
            throw new LoginTimeOutException(
                    "refreshCodeCount:" + refreshCodeCount + ",loginCount:" + loginCount + ",qrCodeCount:" + qrCodeCount + ",verifyCodeCount:" +
                            verifyCodeCount);
        }
    }

    private boolean initLoginStatus() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", "PREPARE_FOR_CRAWL");
        map.put("remark", StringUtils.EMPTY);
        return this.sendMessageToApp(map);
    }

    protected Object getResponseByWebRequest(LinkNode linkNode, ContentType contentType) {
        return this.getResponseByWebRequest(linkNode, contentType, null);
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

    protected void setErrorCode(Map<String, Object> resultMap, String message) {
        resultMap.put("errorCode", message);
    }

    @Deprecated
    protected void setLonginInfo(String username, String password) {
        // try {
        // username = new String(CodecUtils.encrypt(username.getBytes()));
        // password = new String(CodecUtils.encrypt(password.getBytes()));
        // logger.info("set info " + username + " " + password);
        // PluginFactory.getProcessorContext().getProcessorResult().put("username", username);
        // PluginFactory.getProcessorContext().getProcessorResult().put("password", password);
        // } catch (Exception e) {
        // logger.error("setLonginInfo error! " + e.getMessage(), e);
        // }
    }

    protected void setLonginInfo(Map<String, Object> loginParams) throws Exception {
        String userName = StringUtils.defaultIfEmpty((String) loginParams.get("username"), "");
        String passWord = StringUtils.defaultIfEmpty((String) loginParams.get("password"), "");
        String userId = ProcessorContextUtil.getAccountKey(PluginFactory.getProcessorContext());
        Object taskId = ProcessorContextUtil.getTaskUnique(PluginFactory.getProcessorContext());
        String key = REDIS_PREFIX + userId + "_" + taskId;
        String result = new String(CodecUtils.encrypt((userName + "$$$" + passWord).getBytes()));
        DataResource gatewayService = BeanResourceFactory.getInstance().getBean(DataResource.class);
        gatewayService.ttlPush(key, result, defaultTimeToLiveTime);
    }

    protected void postSendMessageToApp(Map<String, Object> map) {
        if (map != null) {
            map.putAll(PluginFactory.getProcessorContext().getStatusContext());
        } else {
            logger.warn("illeage post map ...");
        }
    }

    public enum ContentType {
        ValidCode,
        Content
    }

}
