package com.datatrees.rawdatacentral.plugin.operator.hai_nan_10086_web;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.spider.share.service.utils.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.service.utils.ScriptEngineUtil;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginPostService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/14.
 */
public class HaiNan10086ForWeb implements OperatorPluginPostService {

    private static final Logger logger = LoggerFactory.getLogger(HaiNan10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.hi.10086.cn/service/zzzz.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }

            String backurl = "https://hi.ac.10086.cn/sso3//4login/backPage.jsp";
            String errorurlForLogin = "http://www.hi.10086.cn/service/login/errorPage.jsp";
            String errorurlForSms = "http://www.hi.10086.cn/service/login/errorPage2.jsp";
            String hasdespwd = "hasdespwd";
            String spid = "8a481e862c08afe5012c0a9788590002";

            List<String> backurlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backurlList)) {
                backurl = backurlList.get(0);
            }
            List<String> errorurlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorurlList)) {
                errorurlForLogin = errorurlList.get(0);
                errorurlForSms = errorurlList.get(1);
            }
            List<String> hasdespwdList = XPathUtil.getXpath("//input[@name='hasdespwd']/@value", pageContent);
            if (!CollectionUtils.isEmpty(hasdespwdList)) {
                hasdespwd = hasdespwdList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@name='spid']/@value", pageContent);
            if (!CollectionUtils.isEmpty(spidList)) {
                spid = spidList.get(0);
            }

            TaskUtils.addTaskShare(param.getTaskId(), "backurl", backurl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurlForLogin", errorurlForLogin);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurlForSms", errorurlForSms);
            TaskUtils.addTaskShare(param.getTaskId(), "hasdespwd", hasdespwd);
            TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);

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
            String templateUrl = "https://hi.ac.10086.cn/sso3/common/image.jsp?l=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
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
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String errorurlForSms = TaskUtils.getTaskShare(param.getTaskId(), "errorurlForSms");
            String templateUrl = "https://hi.ac.10086.cn/sso3/SMSCodeSend?mobileNum={}&validCode={}&spid={}&errorurl={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), param.getPicCode().toLowerCase(), spid, errorurlForSms).setAutoRedirect(false)
                    .invoke();
            String code = PatternUtils.group(response.getRedirectUrl(), "code=([^&]+)&", 1);
            if (StringUtils.equals(code, "0000")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            }
            logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
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
            String backurl = TaskUtils.getTaskShare(param.getTaskId(), "backurl");
            String errorurlForLogin = TaskUtils.getTaskShare(param.getTaskId(), "errorurlForLogin");
            String hasdespwd = TaskUtils.getTaskShare(param.getTaskId(), "hasdespwd");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("enString", param.getPassword()).toString();

            String referer = "https://hi.ac.10086.cn/login";
            String templateUrl = "https://hi.ac.10086.cn/sso3/Login";
            String templateData = "backurl={}&errorurl={}&spid={}&RelayState=&hasdespwd={}&Password-type=&type=C&mobileNum={}&servicePassword={}" +
                    "&smsValidCode={}&servicePassword_show=%B7%FE%CE%F1%C3%DC%C2%EB&smscode1=&validCode={}";
            String data = TemplateUtils
                    .format(templateData, URLEncoder.encode(backurl, "UTF-8"), URLEncoder.encode(errorurlForLogin, "UTF-8"), spid, hasdespwd,
                            param.getMobile(), encryptPassword, param.getSmsCode(), param.getPicCode());

            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            if (StringUtils.contains(pageContent, "replace")) {
                TaskUtils.addTaskShare(param.getTaskId(), "pageContentTemp", pageContent);
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
            String templateUrl = "http://www.hi.10086.cn/service/bill/beforeQueryNewDetails.do";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            String referer = "http://www.hi.10086.cn/service/login_yzmpasswd.jsp";
            templateUrl = "http://www.hi.10086.cn/service/user/sendvaildateSmsCode.do";
            String templateData = "mobileno={}&getsmscode=true&isrecord=";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.isNotBlank(pageContent)) {
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
            String referer = "http://www.hi.10086.cn/service/login_yzmpasswd.jsp";
            String templateUrl = "http://www.hi.10086.cn/service/user/vaildateCode.do";
            String templateData = "vaildateCode={}";
            String data = TemplateUtils.format(templateData, param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            if (StringUtils.contains(response.getPageContent(), "true")) {
                templateUrl = "http://www.hi.10086.cn/service/user/vaildateSms.do";
                templateData = "mobileno={}&agentcode=&sso=0&INPASS=ture_aa&INSMS=ture_aa&vaildateCode={}";
                data = TemplateUtils.format(templateData, param.getMobile(), param.getSmsCode());
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setRequestBody(data).setReferer(referer).invoke();
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

    @Override
    public HttpResult<Map<String, Object>> loginPost(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String pageContent = TaskUtils.getTaskShare(param.getTaskId(), "pageContentTemp");
            String referer = "https://hi.ac.10086.cn/sso3/Login";
            String templateUrl = PatternUtils.group(pageContent, "replace\\('([^\\)]+)'\\);", 1);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            templateUrl = "http://www.hi.10086.cn/service";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            templateUrl = "https://login.10086.cn/SSOCheck.action?channelID=12027&backUrl=http%3A%2F%2Fwww.hi.10086.cn%2Fservice%2F";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            templateUrl = "http://www1.10086.cn/service/sso/checkuserloginstatus.jsp?callback=checkuserloginstatuscallback&_={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis()).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "UserInfo\":\"true")) {
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
}
