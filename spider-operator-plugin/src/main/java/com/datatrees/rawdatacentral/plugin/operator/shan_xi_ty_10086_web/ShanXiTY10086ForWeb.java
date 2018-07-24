package com.datatrees.rawdatacentral.plugin.operator.shan_xi_ty_10086_web;

import javax.script.Invocable;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * Created by guimeichao on 17/9/11.
 */
public class ShanXiTY10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(ShanXiTY10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://sx.ac.10086.cn/login";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }

            if (StringUtils.contains(pageContent, "location.replace")) {
                templateUrl = PatternUtils.group(pageContent, "location.replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }

            String loginType = StringUtils.EMPTY;
            String backUrl = "https://sx.ac.10086.cn/4login/backPage.jsp";
            String errorurl = "https://sx.ac.10086.cn/4login/errorPage.jsp";
            String spid = StringUtils.EMPTY;
            String relayStateId = StringUtils.EMPTY;

            List<String> loginTypeList = XPathUtil.getXpath("//input[@id='loginType']/@value", pageContent);
            if (!CollectionUtils.isEmpty(loginTypeList)) {
                loginType = loginTypeList.get(0);
            }
            List<String> backUrlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backUrlList)) {
                backUrl = backUrlList.get(0);
            }
            List<String> errorUrlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorUrlList)) {
                errorurl = errorUrlList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@name='spid']/@value", pageContent);
            if (!CollectionUtils.isEmpty(spidList)) {
                spid = spidList.get(0);
            }
            List<String> relayStateIdList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
            if (!CollectionUtils.isEmpty(relayStateIdList)) {
                relayStateId = relayStateIdList.get(0);
            }

            TaskUtils.addTaskShare(param.getTaskId(), "loginType", loginType);
            TaskUtils.addTaskShare(param.getTaskId(), "backUrl", backUrl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl", errorurl);
            TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);
            TaskUtils.addTaskShare(param.getTaskId(), "relayStateId", relayStateId);

            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshPicCodeForBillDetail(param);
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
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return validatePicCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return null;
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String templateUrl = "https://sx.ac.10086.cn/SMSCodeSend?mobileNum={}&errorurl=https://sx.ac.10086.cn/4login/errorPage" +
                    ".jsp&name=menhu&validCode=%B5%E3%BB%F7%BB%F1%C8%A1" + "&isCheckImage=false&displayPic=0&spid={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, param.getMobile(), spid)
                    .invoke();
            if (StringUtils.contains(response.getPageContent(), "短信验证码已发送到您的手机")) {
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
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String loginType = TaskUtils.getTaskShare(param.getTaskId(), "loginType");
            String backUrl = TaskUtils.getTaskShare(param.getTaskId(), "backUrl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String relayStateId = TaskUtils.getTaskShare(param.getTaskId(), "relayStateId");

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encyptPassword = invocable.invokeFunction("enString", param.getPassword().toString()).toString();

            if (StringUtils.isNotEmpty(relayStateId)) {
                relayStateId = URLEncoder.encode(relayStateId, "UTF-8");
            }

            String templateUrl = "https://sx.ac.10086.cn/Login";
            String templateData = "type={}&backurl={}&errorurl={}&spid={}&RelayState={}&webPassword=&mobileNum={}&displayPic=&isValidateCode" +
                    "=&isCheckImage=false&mobileNum_temp={}&servicePassword={}&smsValidCode={}&validCode=%B5%E3%BB%F7%BB%F1%C8%A1";
            String data = TemplateUtils
                    .format(templateData, loginType, URLEncoder.encode(backUrl, "UTF-8"), URLEncoder.encode(errorurl, "UTF-8"), spid, relayStateId,
                            param.getMobile(), param.getMobile(), encyptPassword, param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            String redirectUrl = response.getRedirectUrl();
            if (StringUtils.isNotBlank(redirectUrl)) {
                if (pageContent.contains("\"5006\" == \"5006\"")) {
                    logger.error("登陆失败,手机号码与运营商归属地不符,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PHONE_FAIL);
                } else {
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }

            if (StringUtils.contains(pageContent, "location.replace")) {
                templateUrl = PatternUtils.group(pageContent, "location.replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            }
            //String samLart = PatternUtils.group(pageContent, "name=\"SAMLart\" value=\"([^\"]+)\"", 1);
            //String relay = PatternUtils.group(pageContent, "name=\"RelayState\" value=\"([^\"]+)\"", 1);
            //
            //templateUrl = "https://sx.ac.10086.cn/4login/backPage.jsp";
            //templateData = "SAMLart={}&isEncodePassword=2&displayPic=1&RelayState={}&isEncodeMobile=1&displayPics=mobile_sms_login%3A0%3D%3D" +
            //        "%3DsendSMS%3A0%3D%3D%3Dmobile_servicepasswd_login%3A0";
            //data = TemplateUtils.format(templateData, samLart, URLEncoder.encode(relay, "UTF-8"));
            //response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
            //        .invoke();
            pageContent = response.getPageContent();
            String samLart = PatternUtils.group(pageContent, "'([^']+)'", 1);
            templateUrl = "http://service.sx.10086.cn/my/";
            templateData = "SAMLart={}&RelayState={}";
            if (StringUtils.isNotEmpty(relayStateId)) {
                relayStateId = URLEncoder.encode(relayStateId, "UTF-8");
            }
            data = TemplateUtils.format(templateData, samLart, relayStateId);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();

            templateUrl = "http://service.sx.10086.cn/login/toLoginSso.action";
            templateData = "loginType=0&jumpMenu=01&phoneNo={}&loginPasswordType=0&returl=%252Fmy%252Findex.action";
            data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
        Response response = null;
        try {
            String referer = "http://service.sx.10086.cn/my/xd.html";
            String templateUrl = "http://service.sx.10086.cn/checkimage.shtml?{}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, db.setScale(16, BigDecimal.ROUND_HALF_UP)).setReferer(referer).invoke();
            logger.info("详单-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://service.sx.10086.cn/my/xd.html";
            String templateUrl = "http://service.sx.10086.cn/enhance/operate/pwdModify/checkRandCode.action?seccodeverify={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, param.getPicCode())
                    .setReferer(referer).invoke();
            if (StringUtils.contains(response.getPageContent(), "\"retCode\":\"0\"")) {
                logger.info("详单-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://service.sx.10086.cn/enhance/operate/pwdModify/sendRandomPwd.action";
            String referer = "http://service.sx.10086.cn/my/xd.html";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            if (StringUtils.contains(response.getPageContent(), "\"retCode\":\"0\"")) {
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
        HttpResult<Map<String, Object>> result = validatePicCodeForBillDetail(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "desforsms.js", "GBK");
            String encyptSmsCode = invocable.invokeFunction("strEnc", param.getSmsCode(), "sitech", "", "").toString();

            String templateUrl = "http://service.sx.10086.cn/enhance/operate/pwdModify/randomPwdCheck.action?randomPwd={}";
            String referer = "http://service.sx.10086.cn/my/xd.html";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl, encyptSmsCode)
                    .setReferer(referer).invoke();
            if (StringUtils.contains(response.getPageContent(), "retMsg\":\"ok!")) {
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
