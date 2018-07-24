package com.datatrees.rawdatacentral.plugin.operator.shan_xi_xa_10086_wap;

import javax.script.Invocable;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/14.
 */
public class ShanXiXA10086ForWap implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(ShanXiXA10086ForWap.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wap.sn.10086.cn/h5/personal/html/login.html";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();

            String key1 = "4";
            String key2 = "7";
            String key3 = "9";

            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }

            String temp = PatternUtils.group(pageContent, "var\\s*key1\\s*=\\s*\"(\\d+)\";", 1);
            if (StringUtils.isNotBlank(temp)) {
                key1 = temp;
            }
            temp = PatternUtils.group(pageContent, "var\\s*key2\\s*=\\s*\"(\\d+)\";", 1);
            if (StringUtils.isNotBlank(temp)) {
                key2 = temp;
            }
            temp = PatternUtils.group(pageContent, "var\\s*key3\\s*=\\s*\"(\\d+)\";", 1);
            if (StringUtils.isNotBlank(temp)) {
                key3 = temp;
            }

            TaskUtils.addTaskShare(param.getTaskId(), "key1", key1);
            TaskUtils.addTaskShare(param.getTaskId(), "key2", key2);
            TaskUtils.addTaskShare(param.getTaskId(), "key3", key3);

            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshPicCodeForLogin(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
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

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://wap.sn.10086.cn/h5/personal/html/login.html";
            String templateUrl = "http://wap.sn.10086.cn/h5/servlet/validateCodeServlet?width=86.15&height=35";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String tokenId = String.valueOf((int) (Math.random() * 100000000));
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encodeMobile = invocable.invokeFunction("strEncForNew", param.getMobile().toString(), tokenId).toString();
            String referer = "http://wap.sn.10086.cn/h5/personal/html/login.html";
            String templateUrl = "http://wap.sn.10086.cn/h5/server/authLogin/sendSMSPwd?serialNumber={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, encodeMobile, tokenId).setReferer(referer).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            if (StringUtils.contains(response.getPageContent(), "短信验证码已经下发到您的手机")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String key1 = TaskUtils.getTaskShare(param.getTaskId(), "key1");
            String key2 = TaskUtils.getTaskShare(param.getTaskId(), "key2");
            String key3 = TaskUtils.getTaskShare(param.getTaskId(), "key3");

            String tokenId = String.valueOf((int) (Math.random() * 100000000));
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encodeSmsCode = invocable.invokeFunction("strEnc", param.getSmsCode(), key1, key2, key3).toString();
            String loginMethod = "0";
            String encodeLoginMethod = invocable.invokeFunction("strEncForNew", loginMethod, tokenId).toString();
            String encodeMobile = invocable.invokeFunction("strEncForNew", param.getMobile().toString(), tokenId).toString();
            String encodePassword = invocable.invokeFunction("strEncForNew", encodeSmsCode, tokenId).toString();
            String encodePicCode = invocable.invokeFunction("strEncForNew", param.getPicCode().toUpperCase(), tokenId).toString();

            String referer = "http://wap.sn.10086.cn/h5/personal/html/login.html";
            String templateUrl = "http://wap.sn.10086.cn/h5/server/authLogin/ssoLogin?loginMethod={}&username={}&password={}&validateCode" +
                    "={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, encodeLoginMethod, encodeMobile, encodePassword, encodePicCode, tokenId).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "登录成功")) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},response={}", param, response);
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
            String templateUrl = "http://wap.sn.10086.cn/h5/personal/html/detailedQuery.html";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            templateUrl = "http://wap.sn.10086.cn/h5/server/DetailedQuery/sendSMS?&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl, Math.random())
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "短信验证码已经下发到您的手机")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
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
            String tokenId = String.valueOf((int) (Math.random() * 100000000));
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encodeSmsCode = invocable.invokeFunction("strEncForNew", param.getSmsCode(), tokenId).toString();
            String templateUrl = "http://wap.sn.10086.cn/h5/server/DetailedQuery/forgotPwd?SMS_NUMBER={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, encodeSmsCode, tokenId).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "X_RESULTCODE\":\"0")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
