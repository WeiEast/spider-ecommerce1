package com.datatrees.rawdatacentral.plugin.operator.hai_nan_10086_web;

import javax.script.Invocable;
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
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/14.
 */
public class HaiNan10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(HaiNan10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://hi.ac.10086.cn/login";
            response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10086_web_001").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }

            String referer = templateUrl;
            templateUrl = PatternUtils.group(pageContent, "replace\\('([^\\)]+)'\\);", 1);
            response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10086_web_002").setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }

            String backurl = "https://hi.ac.10086.cn/sso3//4login/backPage.jsp";
            String errorurl = "https://hi.ac.10086.cn/sso3//4login/errorPage2.jsp";
            String hasdespwd = "hasdespwd";
            String spid = "8a481e862c08afe5012c0a9788590002";

            List<String> backurlList = XPathUtil.getXpath("//input[@id='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backurlList)) {
                backurl = backurlList.get(0);
            }
            List<String> errorurlList = XPathUtil.getXpath("//input[@id='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorurlList)) {
                errorurl = errorurlList.get(0);
            }
            List<String> hasdespwdList = XPathUtil.getXpath("//input[@id='hasdespwd']/@value", pageContent);
            if (!CollectionUtils.isEmpty(hasdespwdList)) {
                hasdespwd = hasdespwdList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@id='spid']/@value", pageContent);
            if (!CollectionUtils.isEmpty(spidList)) {
                spid = spidList.get(0);
            }

            TaskUtils.addTaskShare(param.getTaskId(), "backurl", backurl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl", errorurl);
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
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "hai_nan_10086_web_003")
                    .setFullUrl(templateUrl).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
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
            String backurl = TaskUtils.getTaskShare(param.getTaskId(), "backurl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String hasdespwd = TaskUtils.getTaskShare(param.getTaskId(), "hasdespwd");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("enString", param.getPassword()).toString();

            String referer = "https://hi.ac.10086.cn/login";
            String templateUrl = "https://hi.ac.10086.cn/sso3/Login";
            String templateData = "backurl={}&errorurl={}&spid={}&RelayState=&hasdespwd={}&Password-type=&servicePassword={}&smsValidCode=&type=B" +
                    "&mobileNum={}&servicePassword_show=%B7%FE%CE%F1%C3%DC%C2%EB&validCode={}&smsValidCodeShow=";
            String data = TemplateUtils
                    .format(templateData, URLEncoder.encode(backurl, "UTF-8"), URLEncoder.encode(errorurl, "UTF-8"), spid, hasdespwd, encryptPassword,
                            param.getMobile(), param.getPicCode());

            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10086_web_004").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            referer = "https://hi.ac.10086.cn/sso3/Login";
            templateUrl = PatternUtils.group(pageContent, "replace\\('([^\\)]+)'\\);", 1);
            response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10086_web_005").setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (!StringUtils.contains(pageContent, "callBackurl")) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            String sAMLart = PatternUtils.group(pageContent, "callBackurl\\('([^\\)]+)'\\);", 1);
            templateUrl = "http://www.hi.10086.cn/my/";
            templateData = "SAMLart={}&weaktype=&loginmodelparam=&RelayState=";
            data = TemplateUtils.format(templateData, sAMLart);
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10086_web_006").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            pageContent = response.getPageContent();

            String relayState = "type=null;backurl=http%3A%2F%2Fwww.hi.10086.cn%2Fservice%2FmainQuery.do;" +
                    "nl=1;loginFrom=http%3A%2F%2Fwww.hi.10086.cn%2Fservice%2F";
            List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
            if (!CollectionUtils.isEmpty(relayStateList)) {
                relayState = relayStateList.get(0);
            }
            String sAMLRequest = "PHNhbWxwOkF1dGhuUmVxdWVzdCB4bWxuczpzYW1scD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBT" +
                    "Uw6Mi4wOnByb3RvY29sIiB4bWxuczpzYW1sPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXNzZXJ0aW9" +
                    "uIiBJRD0iZGViNzZmYTc5ZmI2NDczMTkwYjU4NjFjOTM3ZmYzYmEiIElzc3VlSW5zdGFudD0iMjAxNy0wMy0wMi" +
                    "AwNTo1MTo0MCIgVmVyc2lvbj0iMi4wIj48c2FtbDpJc3N1ZXI+OGE0ODFlODYyYzA4YWZlNTAxMmMwYTk3ODg1O" +
                    "TAwMDI8L3NhbWw6SXNzdWVyPjxzYW1scDpOYW1lSURQb2xpY3kgQWxsb3dDcmVhdGU9InRydWUiIEZvcm1hdD0id" +
                    "XJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOm5hbWVpZC1mb3JtYXQ6dHJhbnNpZW50Ii8+PC9zYW1scDpBdXRo" + "blJlcXVlc3Q+";
            List<String> sAMLRequestList = XPathUtil.getXpath("//input[@name='SAMLRequest']/@value", pageContent);
            if (!CollectionUtils.isEmpty(sAMLRequestList)) {
                sAMLRequest = sAMLRequestList.get(0);
                sAMLRequest = sAMLRequest.replace(" ", "");
                sAMLRequest = sAMLRequest.replace("[", "");
                sAMLRequest = sAMLRequest.replace("]", "");
            }

            templateUrl = "https://hi.ac.10086.cn/sso3/POST";
            templateData = "SAMLRequest={}&RelayState={}";
            data = TemplateUtils.format(templateData, URLEncoder.encode(sAMLRequest, "UTF-8"), URLEncoder.encode(relayState, "UTF-8"));
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10086_web_007").setFullUrl(templateUrl).setRequestBody(data).invoke();
            pageContent = response.getPageContent();

            List<String> sAMLartList = XPathUtil.getXpath("//input[@name='SAMLart']/@value", pageContent);
            if (!CollectionUtils.isEmpty(sAMLRequestList)) {
                sAMLart = sAMLartList.get(0);
            }
            String displayPic = "0";
            List<String> displayPicList = XPathUtil.getXpath("//input[@name='displayPic']/@value", pageContent);
            if (!CollectionUtils.isEmpty(displayPicList)) {
                displayPic = displayPicList.get(0);
            }
            String displayPics = "";
            List<String> displayPicsList = XPathUtil.getXpath("//input[@name='displayPics']/@value", pageContent);
            if (!CollectionUtils.isEmpty(displayPicsList)) {
                displayPics = displayPicsList.get(0);
            }
            String isEncodeMobile = "1";
            List<String> isEncodeMobileList = XPathUtil.getXpath("//input[@name='isEncodeMobile']/@value", pageContent);
            if (!CollectionUtils.isEmpty(isEncodeMobileList)) {
                isEncodeMobile = isEncodeMobileList.get(0);
            }
            String isEncodePassword = "2";
            List<String> isEncodePasswordList = XPathUtil.getXpath("//input[@name='isEncodePassword']/@value", pageContent);
            if (!CollectionUtils.isEmpty(isEncodePasswordList)) {
                isEncodePassword = isEncodePasswordList.get(0);
            }
            templateUrl = "http://www.hi.10086.cn/service/mainQuery.do";
            templateData = "SAMLart={}&isEncodePassword={}&displayPic={}&RelayState={}&isEncodeMobile={}&displayPics=";
            data = TemplateUtils
                    .format(templateData, sAMLart, isEncodePassword, displayPic, URLEncoder.encode(relayState, "UTF-8"), isEncodeMobile, displayPics);
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10086_web_008").setFullUrl(templateUrl).setRequestBody(data).invoke();

            templateUrl = "http://www.hi.10086.cn/service/mainQuery.do";
            response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10086_web_009").setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
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
            response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10086_web_010").setFullUrl(templateUrl).invoke();

            String referer = "http://www.hi.10086.cn/service/login_yzmpasswd.jsp";
            templateUrl = "http://www.hi.10086.cn/service/user/sendvaildateSmsCode.do";
            String templateData = "mobileno={}&getsmscode=true&isrecord=";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10086_web_011").setFullUrl(templateUrl).setRequestBody(data)
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
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10086_web_012").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            if (StringUtils.contains(response.getPageContent(), "true")) {
                templateUrl = "http://www.hi.10086.cn/service/user/vaildateSms.do";
                templateData = "mobileno={}&agentcode=&sso=0&INPASS=ture_aa&INSMS=ture_aa&vaildateCode={}";
                data = TemplateUtils.format(templateData, param.getMobile(), param.getSmsCode());
                response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10086_web_013").setFullUrl(templateUrl).setRequestBody(data)
                        .setReferer(referer).invoke();
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
