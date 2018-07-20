package com.datatrees.rawdatacentral.plugin.operator.shang_hai_10086_web;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.operator.domain.model.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/7.
 */
public class ShangHai10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(ShangHai10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            response = TaskHttpClient.create(param, RequestType.GET, "").setFullUrl("https://sh.ac.10086.cn/login").invoke();
            String pageContent = response.getPageContent();
            String source = PatternUtils.group(pageContent, "var source\\s*=\\s*\"([^\"]+)\"", 1);
            String dtmToken = PatternUtils.group(pageContent, "var source\\s*=\\s*\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "source", source);
            TaskUtils.addTaskShare(param.getTaskId(), "dtmToken", dtmToken);
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshSmsCodeForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    public HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "https://sh.ac.10086.cn/login";
            String templateUrl = "https://sh.ac.10086.cn/loginsw?act=1";
            String templateData = "telno={}&dtmToken={}&source={}&ctype=2";
            String source = TaskUtils.getTaskShare(param.getTaskId(), "source");
            String dtmToken = TaskUtils.getTaskShare(param.getTaskId(), "dtmToken");
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_login.js", "GBK");
            String encodeMobile = invocable.invokeFunction("encrypt", param.getMobile().toString()).toString();
            String data = TemplateUtils.format(templateData, URLEncoder.encode(encodeMobile, "UTF-8"), dtmToken, source);
            response = TaskHttpClient.create(param, RequestType.POST, "shang_hai_10086_web_002").setFullUrl(templateUrl).setReferer(referer)
                    .addHeader("X-Requested-With", "XMLHttpRequest").addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                    .setRequestBody(data).invoke();
            if (StringUtils.contains(response.getPageContent(), "动态密码已经发送到您的手机上")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    public HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_login.js", "GBK");
            String encodeMobile = invocable.invokeFunction("encrypt", param.getMobile().toString()).toString();
            String encodePassword = invocable.invokeFunction("encrypt", param.getPassword().toString()).toString();
            String encodeSmscode = invocable.invokeFunction("encrypt", param.getSmsCode().toString()).toString();
            String source = TaskUtils.getTaskShare(param.getTaskId(), "source");
            String referer = "https://sh.ac.10086.cn/login";
            String loginUrl = "https://sh.ac.10086.cn/loginsw?act=2";
            String data = TemplateUtils
                    .format("telno={}&dtm={}&password={}&updtm=0&ctype=2&authLevel=5&source={}", URLEncoder.encode(encodeMobile, "UTF-8"),
                            URLEncoder.encode(encodeSmscode, "UTF-8"), URLEncoder.encode(encodePassword, "UTF-8"), source);
            response = TaskHttpClient.create(param, RequestType.POST, "shang_hai_10086_web_003").setFullUrl(loginUrl).setRequestBody(data)
                    .setReferer(referer).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            JSONObject json = response.getPageContentForJSON();
            String message = json.getString("message");
            String artifact = json.getString("artifact");
            String jtacArtifact = json.getString("jtacArtifact");
            String jtacTransactionId = json.getString("jtacTransactionId");
            if (StringUtils.isNotBlank(artifact) && (StringUtils.contains(message, "成功") || StringUtils.isBlank(message))) {
                referer = "https://sh.ac.10086.cn/login";
                Map<String, Object> params = new HashMap<>();
                params.put("Artifact", jtacArtifact);
                params.put("TransactionID", jtacTransactionId);
                params.put("channelID", "00210");
                String backUrl = TemplateUtils
                        .format("http://www.sh.10086.cn/service/server/sso/reload.jsp?source=wysso&uid={}&tourl=http%3A%2F%2Fwww" +
                                ".sh.10086.cn%2Fsh%2Fservice%2F", artifact);
                params.put("backUrl", TemplateUtils.format(backUrl));
                response = TaskHttpClient.create(param, RequestType.GET, "").setUrl("https://login.10086.cn/AddUID.action").setParams(params)
                        .setReferer(referer).invoke();
                response = TaskHttpClient.create(param, RequestType.POST, "")
                        .setFullUrl("http://www" + ".sh.10086.cn/service/server/ssologin/loginFromUid")
                        .setRequestBody("uid=" + artifact + "&isFirst=false").invoke();
                referer = response.getRedirectUrl();
                TaskHttpClient.create(param, RequestType.GET, "").setUrl("http://www.sh.10086.cn/service/static/").setReferer(referer).invoke();
                logger.info("登录成功,param={}", param);
                return result.success();
            }
            return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    public HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://www.sh.10086.cn/sh/wsyyt/ac/loginbox.jsp?al=1&telno={}";
            String templateUrl = "https://sh.ac.10086.cn/loginex?iscb=1&act=1&telno={}&t={}";
            response = TaskHttpClient.create(param, RequestType.GET, "shang_hai_10086_web_006")
                    .setFullUrl(templateUrl, param.getMobile(), System.currentTimeMillis()).setReferer(referer, param.getMobile()).invoke();
            if (StringUtils.contains(response.getPageContent(), "动态密码已经发送到您的手机上")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    public HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://www.sh.10086.cn/sh/wsyyt/ac/loginbox.jsp?al=1&telno={}";
            String templateUrl = "https://sh.ac.10086.cn/loginex?iscb=1&act=2&telno={}&password={}&authLevel=1&validcode=&t={}&decode=1";
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encodeMobile = invocable.invokeFunction("enString", param.getMobile().toString()).toString();
            String encodeSmscode = invocable.invokeFunction("enString", param.getSmsCode().toString()).toString();

            response = TaskHttpClient.create(param, RequestType.GET, "shang_hai_10086_web_007")
                    .setFullUrl(templateUrl, encodeMobile, encodeSmscode, System.currentTimeMillis()).setReferer(referer, param.getMobile()).invoke();
            String pageContent = response.getPageContent();

            String message = PatternUtils.group(pageContent, "\"message\":\"([^\"]+)\"", 1);
            String uid = PatternUtils.group(pageContent, "\"uid\":\"([^\"]+)\"", 1);
            if (StringUtils.isBlank(message) && StringUtils.isNotBlank(uid)) {
                templateUrl = "http://www.sh.10086.cn/sh/wsyyt/busi.json?sid=WF000022?uid={}";
                response = TaskHttpClient.create(param, RequestType.POST, "shang_hai_10086_web_008").setFullUrl(templateUrl, uid)
                        .setReferer(referer, param.getMobile()).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
                if (StringUtils.contains(response.getPageContent(), "\"code\":0")) {
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                } else {
                    logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
                }
            } else {
                logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }

        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
