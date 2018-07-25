package com.datatrees.rawdatacentral.plugin.operator.ji_lin_10086_web;

import javax.script.Invocable;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.service.utils.ScriptEngineUtil;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录页 http://www.jl.10086.cn/service/GroupLogin/popuplogin.jsp
 * Created by guimeichao on 17/11/9.
 */
public class JiLin10086ForWeb implements OperatorPluginService {

    private static Logger logger = LoggerFactory.getLogger(JiLin10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
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

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptMobile = invocable.invokeFunction("doRSAEncrypt", param.getMobile().toString()).toString();
            String templateUrl = "http://www.jl.10086.cn/service/operate/action/SendSmsCheckCode_sendSmsCodeForLogin" +
                    ".action?bossPhoneid={}&randomStr={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, encryptMobile, System.currentTimeMillis()).invoke();
            if (response.getPageContent().contains("短信验证码已下发至您的手机中")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("doRSAEncrypt", param.getPassword()).toString();
            String encryptSmsCode = invocable.invokeFunction("doRSAEncrypt", param.getSmsCode()).toString();

            String referer = "http://www.jl.10086.cn/service/GroupLogin/popuplogin.jsp";
            String templateUrl = "http://www.jl.10086.cn/service/ssojson/LoginAction_login" +
                    ".action?loginType=01&userId={}&userPassword={}&sms_num={}&_={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), encryptPassword, encryptSmsCode, System.currentTimeMillis()).setReferer(referer)
                    .invoke();
            JSONObject json = response.getPageContentForJSON();
            String return_code = (String) JSONPath.eval(json, "$.resultBean.return_code");
            switch (return_code) {
                case "1":
                    logger.info("登陆成功,param={}", param);
                    return result.success();
                case "2":
                    logger.error("登陆失败,短信验证码不正确,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                case "8001":
                    logger.error("登陆失败,短信验证码不正确,param={}", param);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.jl.10086.cn/service/operate/action/SendSmsCheckCode_sendSmsCode.action?randomStr={}&type=query";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "短信验证码已下发至您的手机中")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String billMonth = TaskUtils.getTaskContext(param.getTaskId(), "billMonth");
            String referer = "http://www.jl.10086.cn/service/fee/QueryDetailList_3300.jsp";
            String templateUrl = "http://www.jl.10086.cn/service/operate/json/CheckSmsAndLetter_handleJson.json?checkCodeBean" +
                    ".checkRange=checkSms&checkCodeBean.smsCheckCode={}&rnd={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getSmsCode(), Math.random()).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "\"checkBoolean\":true")) {
                logger.error("详单-->短信验证码正确,param={}", param);

                Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
                String encryptPassword = invocable.invokeFunction("doRSAEncrypt", param.getPassword()).toString();

                templateUrl = "http://www.jl.10086.cn/service/fee/json/QueryDetailList_queryJson.json?serviceBean.serviceType=3300&serviceBean" +
                        ".DATE_TYPE=1&serviceBean.RADIO_TIME={}&serviceBean.PASSWORD={}&rnd={}";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                        .setFullUrl(templateUrl, billMonth, encryptPassword, Math.random()).invoke();
                pageContent = response.getPageContent();
                TaskUtils.addTaskShare(param.getTaskId(), "details", pageContent);
                return result.success();
            } else {
                logger.error("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
