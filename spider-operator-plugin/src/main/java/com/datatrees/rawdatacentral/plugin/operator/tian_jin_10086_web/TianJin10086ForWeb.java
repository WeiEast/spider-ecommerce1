package com.datatrees.rawdatacentral.plugin.operator.tian_jin_10086_web;

import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/15.
 */
public class TianJin10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(TianJin10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://tj.ac.10086.cn/";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            String picToken = StringUtils.EMPTY;
            String appKey = StringUtils.EMPTY;
            List<String> tokenList = XPathUtil.getXpath("//input[@name='token']/@value", pageContent);
            if (CollectionUtils.isNotEmpty(tokenList)) {
                picToken = tokenList.get(0);
            }
            List<String> appKeyList = XPathUtil.getXpath("//input[@name='appKey']/@value", pageContent);
            if (CollectionUtils.isNotEmpty(appKeyList)) {
                appKey = appKeyList.get(0);
            }
            if (StringUtils.isBlank(picToken) || StringUtils.isBlank(appKey)) {
                logger.error("登录-->初始化失败,picToken或appKey为空,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "picToken", picToken);
            TaskUtils.addTaskShare(param.getTaskId(), "appKey", appKey);
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
            Thread.sleep(2000);
            String picToken = TaskUtils.getTaskShare(param.getTaskId(), "picToken");
            if (StringUtils.isBlank(picToken)) {
                Thread.sleep(2000);
                picToken = TaskUtils.getTaskShare(param.getTaskId(), "picToken");
            }
            if (StringUtils.isBlank(picToken)) {
                logger.error("登录-->图片验证码-->刷新失败,picToken不能为空,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
            }
            String templateUrl = "https://tj.ac.10086.cn/captcha.htm?token={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, picToken).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            String pageContent = response.getPageContent();
            String imgBase64 = PatternUtils.group(pageContent, "data:image\\/png;base64,(.*)", 1);
            return result.success(imgBase64);
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String picToken = TaskUtils.getTaskShare(param.getTaskId(), "picToken");
            String appKey = TaskUtils.getTaskShare(param.getTaskId(), "appKey");
            String referer = "https://tj.ac.10086.cn/";
            String templateUrl = "http://tj.ac.10086.cn/login.json";
            String templateData = "redirectUrl=http%3A%2F%2Fservice" +
                    ".tj.10086.cn%2Fics%2FartifactServletRev%3FRelayState%3DMyHome&token={}&appKey={}&action=passwd&mp={}&loginPwd={}&captcha={}";
            String data = TemplateUtils.format(templateData, picToken, appKey, param.getMobile(), param.getPassword(), param.getPicCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            templateUrl = PatternUtils.group(pageContent, "\"redirect\":\"([^\"]+)\"", 1);
            if (StringUtils.isBlank(templateUrl)) {
                if (pageContent.contains("验证码有误")) {
                    logger.error("登陆失败,图片验证码错误,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
                } else if (pageContent.contains("服务密码不正确")) {
                    logger.error("登陆失败,账户名与密码不匹配,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                } else if (pageContent.contains("手机号或密码位数不对")) {
                    logger.error("登陆失败,手机号或密码位数不对,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PHONE_FAIL);
                } else if (pageContent.contains("请正确输入11位天津移动手机号码!</font>")) {
                    logger.error("登陆失败,请正确输入11位天津移动手机号码,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PHONE_FAIL);
                } else {
                    logger.error("登陆失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            if (StringUtils.isNotEmpty(pageContent)) {
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
            String referer = "http://service.tj.10086.cn/ics/myMobile/myDetailRecords.html";
            String templateUrl = "http://service.tj.10086.cn/ics/ics?service=ajaxDirect/1/componant/componant/javascript/&pagename=componant" +
                    "&eventname=sendMessage&GOODSNAME=详单&DOWHAT=QUE&ajaxSubmitType=get&ajax_randomcode=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            if (StringUtils.contains(response.getPageContent(), "\"FLAG\":\"true\"")) {
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
            String referer = "http://service.tj.10086.cn/ics/myMobile/myDetailRecords.html";
            String templateUrl =
                    "http://service.tj.10086.cn/ics/ics?service=ajaxDirect/1/componant/componant/javascript/&pagename=componant&eventname" +
                            "=validateSms&smsCode={}&ajaxSubmitType=get&ajax_randomcode=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, param.getSmsCode())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "验证成功")) {
                String echtokeninfo = PatternUtils.group(pageContent, "ECHTOKENINFO\":\"([^\"]+)\"", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "echtokeninfo", echtokeninfo);
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
