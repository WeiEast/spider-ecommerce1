package com.datatrees.rawdatacentral.plugin.operator.fu_jian_10086_web;

import javax.script.Invocable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.operator.domain.model.FormType;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginPostService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * Created by guimeichao on 17/8/29.
 */
public class FuJian10086ForWeb implements OperatorPluginPostService {

    private static final Logger logger = LoggerFactory.getLogger(FuJian10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            String templateUrl = "https://fj.ac.10086.cn/login";
            Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "fu_jian_10086_web_001")
                    .setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
            if (StringUtils.isNotBlank(templateUrl)) {
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "fu_jian_10086_web_002")
                        .setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }

            String backUrl = "https://fj.ac.10086.cn/4login/backPage.jsp";
            String errorurl = "https://fj.ac.10086.cn/4login/errorPage.jsp";
            String relayStateId = StringUtils.EMPTY;

            String loginType = PatternUtils.group(pageContent, "id=\"loginType\"\\s*name=\"type\"\\s*value=\"([^\"]+)\"", 1);
            List<String> backUrlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backUrlList)) {
                backUrl = backUrlList.get(0);
            }
            List<String> errorUrlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorUrlList)) {
                errorurl = errorUrlList.get(0);
            }
            String spid = PatternUtils.group(pageContent, "name=\"spid\"\\s*value=\"([^\"]+)\"", 1);
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
            logger.error("登录-->初始化失败,param={}", param, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshPicCodeForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshPicCodeForBillDetail(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return new HttpResult<Map<String, Object>>().success();
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
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
            String templateUrl = "https://fj.ac.10086.cn/common/image.jsp?r_{}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "fu_jian_10086_web_003")
                    .setFullUrl(templateUrl, db.setScale(16, BigDecimal.ROUND_HALF_UP)).invoke();
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
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPwd = invocable.invokeFunction("enString", param.getPassword()).toString();

            String loginType = TaskUtils.getTaskShare(param.getTaskId(), "loginType");
            String backUrl = TaskUtils.getTaskShare(param.getTaskId(), "backUrl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String relayStateId = TaskUtils.getTaskShare(param.getTaskId(), "relayStateId");

            String templateUrl = "https://fj.ac.10086.cn/Login";
            String templateData = "type={}&backurl={}&errorurl={}&spid={}&RelayState={}&mobileNum={}&servicePassword={}&smsValidCode=&validCode" +
                    "={}&Password-type=&button=%E7%99%BB++%E5%BD%95";
            if (StringUtils.isNotBlank(backUrl)) {
                backUrl = URLEncoder.encode(backUrl, "UTF-8");
            }
            if (StringUtils.isNotBlank(errorurl)) {
                errorurl = URLEncoder.encode(errorurl, "UTF-8");
            }
            if (StringUtils.isNotBlank(relayStateId)) {
                relayStateId = URLEncoder.encode(relayStateId, "UTF-8");
            }
            String data = TemplateUtils
                    .format(templateData, loginType, backUrl, errorurl, spid, relayStateId, param.getMobile(), encryptPwd, param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "fu_jian_10086_web_004").setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            //TODO
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            String errorMessage = PatternUtils.group(pageContent, "errorMsg=([^\"]+)", 1);
            if (StringUtils.isNotBlank(errorMessage)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "pageContentTemp", pageContent);
            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://fj.ac.10086.cn/common/image.jsp?id={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "china_10086_shop_006")
                    .setFullUrl(templateUrl, Math.random()).invoke();
            logger.info("详单-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://fj.ac.10086.cn/SMSCodeSend?spid={}&mobileNum={}&validCode={}&errorurl=http://www" +
                    ".fj.10086.cn:80/my/login/send.jsp";
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_007")
                    .setFullUrl(templateUrl, spid, param.getMobile(), param.getPicCode()).invoke();
            String pageContent = response.getPageContent();
            String url = response.getRedirectUrl();
            if (url.contains("code=0000")) {
                logger.info("详单-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->图片验证码-->校验失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
        Response response = null;
        try {
            String referer = "http://www.fj.10086.cn/my/index.jsp?id_type=YANZHENGMA";
            String templateUrl = "https://fj.ac.10086.cn/Login";
            String templateData = "s02=false&Password=&Password-type=&spid={}&validCode=0000&servicePassword=&n1=1&sso=0&RelayState=1&ocs_url=&s02" +
                    "=false&sp_id=&do_login_type=&isValidateCode=1&type=A&smscode={}&mobileNum={}&agentcode=&backurl=http%3A%2F%2Fwww" +
                    ".fj.10086.cn%3A80%2Fmy%2FssoAssert.jsp%3Ftypesso%3DC%26CALLBACK_URL%3Dhttp%3A%2F%2Fwww" +
                    ".fj.10086.cn%3A80%2Fmy%2Fuser%2FgetUserInfo.do&errorurl=http%3A%2F%2Fwww.fj.10086.cn%3A80%2Fmy%2Flogin%2Fsend" +
                    ".jsp&smsValidCode={}&smscode1={}";
            String data = TemplateUtils.format(templateData, spid, param.getSmsCode(), param.getMobile(), param.getSmsCode(), param.getSmsCode());
            response = TaskHttpClient.create(param, RequestType.POST, "fu_jian_10086_web_008").setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "basicInfoReferUrl", templateUrl);
            if (StringUtils.isNotBlank(templateUrl)) {
                response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_009").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }
            if (StringUtils.contains(pageContent,"postartifact")){
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "fu_jian_10086_web_009", pageContent);
            }
            templateUrl = PatternUtils.group(pageContent, "href = \"([^\"]+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "basicInfoUrl", templateUrl);
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_010").setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();

            if (!StringUtils.contains(pageContent, param.getMobile().toString()) && StringUtils.contains(pageContent, "window.location.href")) {
                templateUrl = PatternUtils.group(pageContent, "href=\"([^\"]+)\"", 1);
                if (!StringUtils.contains(templateUrl, "http://www.fj.10086.cn")) {
                    templateUrl = "http://www.fj.10086.cn" + templateUrl;
                }
                TaskUtils.addTaskShare(param.getTaskId(), "basicInfoUrl", templateUrl);
                response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_011").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }

            if (pageContent.contains(param.getMobile().toString())) {
                TaskUtils.addTaskShare(param.getTaskId(), "basicInfoPage", pageContent);
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.warn("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
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
            String templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
            if (StringUtils.isNotBlank(templateUrl)) {
                response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_005").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }
            if (StringUtils.contains(pageContent,"postartifact")){
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "fu_jian_10086_web_006", pageContent);
            }

            String samLart = PatternUtils.group(pageContent, "callBackurlAll\\('([^']+)'", 1);

            templateUrl = "http://www.fj.10086.cn/my/?SAMLart={}&RelayState=";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_006").setFullUrl(templateUrl, samLart).setMaxRetry(2)
                    .invoke();
            pageContent = response.getPageContent();

            if (StringUtils.isNotBlank(pageContent) && pageContent.contains(param.getMobile().toString())) {
                logger.info("爬虫启动前处理成功,param={}", param);
                return result.success();
            } else {
                logger.error("爬虫启动前处理失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("爬虫启动前处理失败,param={},response={}", param, response, e);
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
        if (org.apache.commons.lang3.StringUtils.contains(fullUrl, "?")) {
            if (!org.apache.commons.lang3.StringUtils.endsWith(fullUrl, "?")) {
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
        RequestType requestType = org.apache.commons.lang3.StringUtils.equalsIgnoreCase("post", method) ? RequestType.POST : RequestType.GET;
        Response response = TaskHttpClient.create(taskId, websiteName, requestType, remark).setFullUrl(url).invoke();
        return response.getPageContent();
    }
}
