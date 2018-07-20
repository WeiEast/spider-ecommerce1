package com.datatrees.rawdatacentral.plugin.operator.zhe_jiang_10086_web;

import javax.script.Invocable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.*;
import com.datatrees.spider.operator.domain.model.FormType;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 浙江移动--web
 * 登陆地址:http://www.zj.10086.cn/my/login/login.jsp?AISSO_LOGIN=true&jumpurl=http://www.zj.10086.cn/my/index.jsp?ul_loginclient=my
 * 登陆(服务密码登陆):手机号,服务密码,图片验证码(不支持验证)
 * 详单:短信验证码
 * Created by zhouxinghai on 2017/8/25
 */
public class ZheJiang10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(ZheJiang10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://zj.ac.10086.cn/login";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "zhe_jiang_10086_web_001")
                    .setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            String relayState
                    = "type=B;backurl=http://www.zj.10086.cn/my/servlet/assertion;nl=6;loginFromUrl=http://www.zj.10086.cn/my/index.do;callbackurl=/servlet/assertion;islogin=true";
            String backurl = "https://zj.ac.10086.cn/login/backPage.jsp";
            String errorurl = "https://zj.ac.10086.cn/login/errorPage.jsp";
            String spid = "8ace47be5dbc4890015dbfe2e4c80004";
            String type = "B";
            String warnurl = "https://zj.ac.10086.cn/login/warnPage.jsp";
            List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
            if (!relayStateList.isEmpty()) {
                relayState = relayStateList.get(0);
            }
            List<String> backurlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!backurlList.isEmpty()) {
                backurl = backurlList.get(0);
            }
            List<String> errorurlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", pageContent);
            if (!errorurlList.isEmpty()) {
                errorurl = errorurlList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@name='spid']/@value", pageContent);
            if (!spidList.isEmpty()) {
                spid = spidList.get(0);
            }
            List<String> typeList = XPathUtil.getXpath("//input[@name='type']/@value", pageContent);
            if (!typeList.isEmpty()) {
                type = typeList.get(0);
            }
            List<String> warnurlList = XPathUtil.getXpath("//input[@name='warnurl']/@value", pageContent);
            if (!warnurlList.isEmpty()) {
                warnurl = warnurlList.get(0);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "relayState", relayState);
            TaskUtils.addTaskShare(param.getTaskId(), "backurl", backurl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl", errorurl);
            TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);
            TaskUtils.addTaskShare(param.getTaskId(), "type", type);
            TaskUtils.addTaskShare(param.getTaskId(), "warnurl", warnurl);
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
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return validatePicCodeForLogin(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        switch (param.getFormType()) {
            case "BASEINFO_DETAILS":
                return processForBaseInfo(param);
            case "BALANCEINFO_DETAILS":
                return processForBalanceInfo(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://zj.ac.10086.cn/common/image.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "zhe_jiang_10086_web_002")
                    .setFullUrl(templateUrl).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://zj.ac.10086.cn/validImageCode?r_{}&imageCode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "zhe_jiang_10086_web_003")
                    .setFullUrl(templateUrl, Math.random(), param.getPicCode()).invoke();
            if (response.getPageContent().contains("1")) {
                logger.info("登录-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->图片验证码-->校验失败,param={}", param);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            }
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        String pageContent = null;
        try {
            String templateUrl = null;
            String bid = TaskUtils.getTaskShare(param.getTaskId(), "bid");
            for (int i = 0; i <= 1; i++) {
                if (StringUtils.isBlank(bid)) {
                    templateUrl = "http://service.zj.10086.cn/yw/detail/queryHisDetailBill.do?menuId=13009";
                    response = TaskHttpClient.create(param, RequestType.GET, "china_10086_shop_007").setFullUrl(templateUrl).invoke();
                    pageContent = response.getPageContent();
                    if (!StringUtils.contains(pageContent, "postartifact")) {
                        return result.failure(ErrorCode.REFESH_SMS_FAIL);
                    }
                    pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_008", pageContent);

                    if (!StringUtils.contains(pageContent, "postartifact")) {
                        return result.failure(ErrorCode.REFESH_SMS_FAIL);
                    }
                    pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_009", pageContent);

                    bid = RegexpUtils.select(pageContent, "\"bid\":\"(.*)\"", 1);
                    if (StringUtils.isBlank(bid)) {
                        logger.warn("详单-->短信验证码-->刷新失败,bid not found,param={},pateContent={}", param, response.getPageContent());
                        return result.failure(ErrorCode.REFESH_SMS_FAIL);
                    }
                    TaskUtils.addTaskShare(param.getTaskId(), "bid", bid);
                }
                logger.info("frefeshSmsCodeForBillDetail find bid={},param={}", bid, param);

                templateUrl = "http://service.zj.10086.cn/yw/detail/secondPassCheck.do?bid={}";
                response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10086_web_010").setFullUrl(templateUrl, bid).invoke();
                pageContent = response.getPageContent();
            }
            switch (pageContent) {
                case "1":
                    logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                default:
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
            String bid = TaskUtils.getTaskShare(param.getTaskId(), "bid");
            String templateUrl = "http://service.zj.10086.cn/yw/detail/secondPassCheck.do?validateCode={}&bid={}";
            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10086_web_011").setFullUrl(templateUrl, param.getSmsCode(), bid)
                    .invoke();
            String pageContent = response.getPageContent();
            switch (pageContent) {
                case "12":
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                default:
                    logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                    return result.failure(ErrorCode.VALIDATE_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = validatePicCodeForLogin(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            String relayState = TaskUtils.getTaskShare(param.getTaskId(), "relayState");
            String backurl = TaskUtils.getTaskShare(param.getTaskId(), "backurl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String type = TaskUtils.getTaskShare(param.getTaskId(), "type");
            String warnurl = TaskUtils.getTaskShare(param.getTaskId(), "warnurl");
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptMobile = (String) invocable.invokeFunction("enString", param.getMobile().toString());
            String encryptPassword = (String) invocable.invokeFunction("enString", param.getPassword());
            String templateUrl = "https://zj.ac.10086.cn/Login";
            String templateData = "type={}&backurl={}&warnurl={}&errorurl={}&spid={}&RelayState={}&mobileNum={}&loginmodel=&validCode" +
                    "={}&smsValidCode=&servicePassword={}";
            String data = TemplateUtils.format(templateData, type, URLEncoder.encode(backurl, "UTF-8"), URLEncoder.encode(warnurl, "UTF-8"),
                    URLEncoder.encode(errorurl, "UTF-8"), spid, URLEncoder.encode(relayState, "UTF-8"), encryptMobile, param.getPicCode(),
                    encryptPassword);

            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10086_web_004").setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();

            if (StringUtils.contains(response.getPageContent(), "location.replace")) {
                templateUrl = PatternUtils.group(response.getPageContent(), "replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param, RequestType.GET, "").setFullUrl(templateUrl).invoke();
            }
            //出现错误返回304
            //if (StringUtils.isNoneBlank(response.getRedirectUrl())) {
            //    String redirectUrl = URLDecoder.decode(response.getRedirectUrl(), "GBK");
            //    String errorMsg = RegexpUtils.select(redirectUrl, "&msg=(.+)", 1);
            //    switch (errorMsg) {
            //        case "您输入的验证码不正确，请重新输入验证码":
            //            logger.warn("登录-->图片验证码--校验失败,params={}", param);
            //            return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            //        default:
            //            logger.warn("登录失败,response not contains postartifact,params={},errorMsg={},redirectUrl={}", param, errorMsg, redirectUrl);
            //            return result.failure();
            //    }
            //
            //}

            String pageContent = response.getPageContent();
            String sAMLart = PatternUtils.group(pageContent, "callAssert\\('([^']+)'\\)", 1);
            templateUrl = "http://www.zj.10086.cn/my/servlet/assertion";
            templateData = "SAMLart={}&RelayState={}";
            data = TemplateUtils.format(templateData, sAMLart, URLEncoder.encode(relayState, "UTF-8"));
            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10086_web_006").setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
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

    /**
     * 处理跳转服务
     * @param pageContent
     * @return
     */
    private String executeScriptSubmit(Long taskId, String websiteName, String remark, String pageContent) {
        String action = JsoupXpathUtils.selectFirst(pageContent, "//form/@action");
        String method = JsoupXpathUtils.selectFirst(pageContent, "//form/@method");
        List<Map<String, String>> list = JsoupXpathUtils.selectAttributes(pageContent, "//input");
        StringBuilder fullUrl = new StringBuilder(action);
        if (StringUtils.contains(fullUrl, "?")) {
            if (!StringUtils.endsWith(fullUrl, "?")) {
                fullUrl.append("&");
            }
        } else {
            fullUrl.append("?");
        }
        if (null != list && !list.isEmpty()) {
            for (Map<String, String> map : list) {
                if (map.containsKey("name") && map.containsKey("value")) {
                    try {
                        fullUrl.append(map.get("name")).append("=").append(URLEncoder.encode(map.get("value"), "UTF-8")).append("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String url = fullUrl.substring(0, fullUrl.length() - 1);
        RequestType requestType = StringUtils.equalsIgnoreCase("post", method) ? RequestType.POST : RequestType.GET;
        Response response = TaskHttpClient.create(taskId, websiteName, requestType, remark).setFullUrl(url).invoke();
        return response.getPageContent();
    }

    private HttpResult<Object> processForBaseInfo(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String smsCode = TaskUtils.getTaskShare(param.getTaskId(), RedisKeyPrefixEnum.TASK_SMS_CODE.getRedisKey(FormType.VALIDATE_BILL_DETAIL));
            String templateUrl = "http://www.zj.10086.cn/my/userinfo/queryUserYdInfo.do?fromFlag=&secPwd={}";
            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10086_web_012").setFullUrl(templateUrl, smsCode).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "postartifact") || StringUtils.contains(pageContent, "authnrequestform")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_013", pageContent);
            }
            if (StringUtils.contains(pageContent, "postartifact") || StringUtils.contains(pageContent, "authnrequestform")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_014", pageContent);
            }
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("个人信息页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForBalanceInfo(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://service.zj.10086.cn/yw/bill/realFee.do?menuId=13004&bid=";
            response = TaskHttpClient.create(param, RequestType.GET, "zhe_jiang_10086_web_015").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "postartifact") || StringUtils.contains(pageContent, "authnrequestform")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_016", pageContent);
            }
            if (StringUtils.contains(pageContent, "postartifact") || StringUtils.contains(pageContent, "authnrequestform")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_017", pageContent);
            }
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("余额信息页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
}
