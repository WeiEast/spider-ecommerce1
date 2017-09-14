package com.datatrees.rawdatacentral.plugin.operator.bei_jing_10086_web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一天最多只能发送8条短信随机码
 * Created by guimeichao on 17/9/13.
 */
public class BeiJing10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(BeiJing10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={}", param, e);
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
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
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
        return null;
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "https://bj.ac.10086.cn/ac/CmSsoLogin";
            String templateUrl = "https://bj.ac.10086.cn/ac/tempPwdSend?mobile={}";
            response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_001").setFullUrl(templateUrl, param.getMobile())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "短信发送成功")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,一天最多只能发送8条短信随机码,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    public HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "https://bj.ac.10086.cn/ac/cmsso/iloginnew.jsp";
            String templateUrl = "https://bj.ac.10086.cn/ac/CmSsoLogin";
            String templateData = "user={}&phone={}&backurl=http%3A%2F%2Fwww.bj.10086.cn%2Fmy&continue=http%3A%2F%2Fwww" +
                    ".bj.10086.cn%2Fmy&style=BIZ_LOGINBOX&service=www" +
                    ".bj.10086.cn&box=&target=_parent&ssoLogin=yes&loginMode=2&loginMethod=1&loginName={}&password=&smsNum={}&rnum=&ckCookie=on";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getMobile(), param.getMobile(), param.getSmsCode());

            response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_002").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isEmpty(pageContent) || !StringUtils.contains(pageContent, "$continueUrl = 'http://www.bj.10086.cn/my'")) {
                // need relogin
                if (!StringUtils.contains(pageContent, "登录中")) {
                    if (StringUtils.contains(pageContent, "您刚刚登录过并且仍未退出")) {
                        // relogin
                        String errorMessage = PatternUtils.group(pageContent, "var\\s*\\$fcode\\s*=\\s*'([^']+)'", 1);
                        switch (errorMessage) {
                            case "PP_1_2019":
                                logger.warn("登录失败-->短信随机码不正确或已过期,param={}", param);
                                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                            case "PP_1_2020":
                                logger.warn("登录失败-->短信随机码不正确或已过期,param={}", param);
                                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                            case "PP_1_2018":
                                logger.warn("登录失败-->短信随机码不正确或已过期,param={}", param);
                                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                            case "CMSSO_1_0500":
                                logger.warn("登录失败-->系统忙,param={}", param);
                                return result.failure(ErrorCode.LOGIN_FAIL);
                        }
                        referer = "https://bj.ac.10086.cn/ac/CmSsoLogin";
                        templateUrl = "https://bj.ac.10086.cn/ac/loginAgain";
                        data = "backurl=http%3A%2F%2Fwww.bj.10086.cn%2Fmy&continue=http%3A%2F%2Fwww" +
                                ".bj.10086.cn%2Fmy&style=BIZ_LOGINBOX&loginMethod=1&loginMode=1&service=www.bj.10086.cn&box=&target=_self&hostId=4&submit=";
                        response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_003").setFullUrl(templateUrl)
                                .setRequestBody(data).setReferer(referer).invoke();
                        pageContent = response.getPageContent();
                        if (StringUtils.isEmpty(pageContent)) {
                            logger.error("登陆失败,param={},response={}", param, response);
                            return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                        }
                    } else {
                        // errorCode
                        String errorMessage = PatternUtils.group(pageContent, "var\\s*\\$fcode\\s*=\\s*'([^']+)'", 1);
                        switch (errorMessage) {
                            case "PP_1_0803":
                                logger.warn("登录失败-->用户名或密码不正确,param={}", param);
                                return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                            case "PP_1_0820":
                                logger.warn("登录失败-->密码输入错误三次，账号锁定,param={}", param);
                                return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                            case "PP_1_0821":
                                logger.warn("登录失败-->累计密码错误次数已达到上限,param={}", param);
                                return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                            default:
                                logger.error("登陆失败,param={},response={}", param, response);
                                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                        }
                    }
                }
            }

            templateUrl = "http://www.bj.10086.cn/my";
            response = TaskHttpClient.create(param, RequestType.GET, "bei_jing_10086_web_004").setFullUrl(templateUrl).invoke();

            referer = templateUrl;
            templateUrl = "http://www.bj.10086.cn/www/mybusiness?cmdFlag=phone1";
            response = TaskHttpClient.create(param, RequestType.GET, "bei_jing_10086_web_005").setFullUrl(templateUrl).setReferer(referer)
                    .addHeader("X-Requested-With", "XMLHttpRequest").invoke();

            referer = "http://www.bj.10086.cn/service/fee/";
            templateUrl = "http://www.bj.10086.cn/service/fee/qqtxdcx/";
            response = TaskHttpClient.create(param, RequestType.GET, "bei_jing_10086_web_006").setFullUrl(templateUrl).setReferer(referer)
                    .addHeader("X-Requested-With", "XMLHttpRequest").invoke();

            referer = "http://www.bj.10086.cn/service/fee/zdcx/";
            templateUrl = "http://cmodsvr1.bj.chinamobile.com/PortalCMOD/InnerInterFaceCiisNowDetail";
            response = TaskHttpClient.create(param, RequestType.GET, "bei_jing_10086_web_007").setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();

            String action = StringUtils.EMPTY;
            String sAMLRequest = StringUtils.EMPTY;
            String relayState = StringUtils.EMPTY;
            if (StringUtils.isNotBlank(pageContent)) {
                List<String> actionList = XPathUtil.getXpath("//FORM/@action", pageContent);
                List<String> sAMLRequestList = XPathUtil.getXpath("//input[@name='SAMLRequest']/@value", pageContent);
                List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
                if (actionList == null || actionList.size() == 0 || sAMLRequestList == null || sAMLRequestList.size() == 0) {
                    logger.error("登陆失败,验证服务密码失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                action = actionList.get(0);
                // sAMLRequest =sAMLRequestList.get(0);
                sAMLRequest = URLEncoder.encode(sAMLRequestList.get(0), "UTF-8");
                if (relayStateList != null && relayStateList.size() > 0) {
                    relayState = relayStateList.get(0);
                }
            } else {
                logger.error("登陆失败,验证服务密码失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            if (action.equals("") || sAMLRequest.equals("")) {
                logger.error("登陆失败,验证服务密码失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            referer = "https://cmodsvr1.bj.chinamobile.com/PortalCMOD/InnerInterFaceCiisHisBill";
            templateUrl = action;
            templateData = "SAMLRequest={}&RelayState={}";
            data = TemplateUtils.format(templateData, sAMLRequest, relayState);
            response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_008").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            pageContent = response.getPageContent();

            if (StringUtils.isNotBlank(pageContent)) {
                List<String> actionList = XPathUtil.getXpath("//form/@action", pageContent);
                List<String> sAMLRequestList = XPathUtil.getXpath("//input[@name='SAMLart']/@value", pageContent);
                List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
                if (sAMLRequestList == null || sAMLRequestList.size() == 0) {
                    logger.error("登陆失败,验证服务密码失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                action = actionList.get(0);
                sAMLRequest = URLEncoder.encode(sAMLRequestList.get(0), "UTF-8");
                if (relayStateList != null && relayStateList.size() > 0) {
                    relayState = relayStateList.get(0);
                }
            } else {
                logger.error("登陆失败,验证服务密码失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            if (action.equals("") || sAMLRequest.equals("")) {
                logger.error("登陆失败,验证服务密码失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            referer = "https://bj.ac.10086.cn/ac/SamlCmAuthnResponse";
            templateUrl = action;
            templateData = "SAMLRequest={}&RelayState={}";
            data = TemplateUtils.format(templateData, sAMLRequest, relayState);
            response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_009").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            pageContent = response.getPageContent();

            String sessionId = PatternUtils.group(pageContent, "ssoSessionID=([^ \"'&]+)", 1);
            if (StringUtils.isBlank(sessionId)) {
                logger.error("登陆失败,获取sessionId失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            SimpleDateFormat sf = new SimpleDateFormat("yyyy.MM");
            Calendar c = Calendar.getInstance();
            templateUrl = "https://cmodsvr1.bj.chinamobile.com/PortalCMOD/LoginSecondCheck?ssoSessionID={}";
            templateData = "searchType=HisDetail&detailType=RC&checkMonth={}&password={}";
            data = TemplateUtils.format(templateData, sf.format(c.getTime()), param.getPassword());
            referer = "https://cmodsvr1.bj.chinamobile.com/PortalCMOD/LoginSecond?searchType=HisDetail&checkMonth={}&ssoSessionID={}&detailType=RC";
            response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_010").setFullUrl(templateUrl, sessionId)
                    .setRequestBody(data).setReferer(referer, sf.format(c.getTime()), sessionId).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, param.getMobile().toString()) && !StringUtils.contains(pageContent, "客服密码输入不正确")) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,您的账户名与密码不匹配,param={},response={}", param, response);
                return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
