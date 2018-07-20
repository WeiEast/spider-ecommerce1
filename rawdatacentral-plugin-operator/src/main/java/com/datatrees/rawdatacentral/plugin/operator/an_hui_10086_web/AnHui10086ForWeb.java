package com.datatrees.rawdatacentral.plugin.operator.an_hui_10086_web;

import javax.script.Invocable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.util.selenium.SeleniumUtils;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/12.
 */
public class AnHui10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(AnHui10086ForWeb.class);

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
            case "UIFENCODE":
                return processForUifEncode(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            if (StringUtils.isBlank(spid)) {
                RemoteWebDriver driver = null;
                try {
                    String currentUrl = "https://ah.ac.10086.cn/login";
                    driver = SeleniumUtils.createClient(param.getTaskId(), param.getWebsiteName());
                    long start = System.currentTimeMillis();
                    driver.get(currentUrl);
                    String pageContent = driver.getPageSource();
                    while (!StringUtils.contains(pageContent, "name=\"spid\"")) {
                        if (System.currentTimeMillis() - start > 5000) {
                            break;
                        }
                        TimeUnit.MILLISECONDS.sleep(200);
                        pageContent = driver.getPageSource();
                    }
                    logger.info("登录页面加载耗时{}毫秒,taskId={}", System.currentTimeMillis() - start, param.getTaskId());
                    spid = JsoupXpathUtils.selectFirst(pageContent, "//form[@id='oldLogin']/input[@name='spid']/@value");
                    TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);
                    TaskUtils.saveCookie(param.getTaskId(), SeleniumUtils.getCookies(driver));
                } catch (Exception e) {
                    logger.error("安徽移动登录页面初始化失败,taskId={}", param.getTaskId(), e);
                } finally {
                    SeleniumUtils.closeClient(driver);
                }
            }
            String templateUrl = "https://ah.ac.10086.cn/common/image.jsp?l={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "an_hui_10086_web_003")
                    .setFullUrl(templateUrl, Math.random()).invoke();
            String base64 = PatternUtils.group(response.getPageContent(), "data:image\\/JPEG;base64,([^']+)'\\);", 1);
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(base64);
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
            String referer = "https://ah.ac.10086.cn/login";
            String templateUrl = "https://ah.ac.10086.cn/validImageCode?r_0.{}&imageCode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "an_hui_10086_web_004").setReferer(referer)
                    .setFullUrl(templateUrl, System.currentTimeMillis(), param.getPicCode()).invoke();
            if (StringUtils.contains(response.getPageContent(), "1")) {
                logger.info("登录-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    public HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = validatePicCode(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");

            String templateUrl = "https://ah.ac.10086.cn/Login";
            String templateData = "type=B&formertype=B&backurl=https%3A%2F%2Fservice.ah.10086.cn%2FLoginSso&backurlflag=https%3A%2F%2Fah" +
                    ".ac.10086.cn%2F4login%2FbackPage.jsp&errorurl=https%3A%2F%2Fah.ac.10086.cn%2F4login%2FerrorPage" +
                    ".jsp&spid={}&RelayState=&mobileNum={}&login_type_ah=&login_pwd_type=2&loginBackurl=&timestamp={}&smsValidCode" +
                    "=&servicePassword={}&validCode_state=false&loginType=0&servicePassword_1=&smsValidCode=&validCode={}";

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", null);
            String encryptPassword = invocable.invokeFunction("enString", param.getPassword().toString()).toString();

            String data = TemplateUtils
                    .format(templateData, spid, param.getMobile(), System.currentTimeMillis(), encryptPassword, param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10086_web_005").setFullUrl(templateUrl).setRequestBody(data).invoke();
            templateUrl = PatternUtils.group(response.getPageContent(), "replace\\('([^']+)'\\);", 1);
            if (StringUtils.isBlank(templateUrl)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_ERROR);
            }
            response = TaskHttpClient.create(param, RequestType.GET, "an_hui_10086_web_006").setFullUrl(templateUrl).invoke();
            if (StringUtils.isBlank(response.getPageContent())) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_ERROR);
            }
            String sAMLart = StringUtils.EMPTY;
            List<String> sAMLartList = XPathUtil.getXpath("//input[@name='SAMLart']/@value", response.getPageContent());
            if (!CollectionUtils.isEmpty(sAMLartList)) {
                sAMLart = sAMLartList.get(0);
            }
            templateUrl = "http://service.ah.10086.cn/LoginSso";
            templateData = "SAMLart={}&RelayState=";
            data = TemplateUtils.format(templateData, sAMLart);
            response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10086_web_007").setFullUrl(templateUrl).setRequestBody(data).invoke();

            templateUrl = PatternUtils.group(response.getPageContent(), "window.top.location.href=\"([^\"]+)\"", 1);
            response = TaskHttpClient.create(param, RequestType.GET, "an_hui_10086_web_008").setFullUrl(templateUrl).invoke();

            String referer = "http://service.ah.10086.cn/index.html";
            templateUrl = "http://service.ah.10086.cn/common/pageInfoInit?kind=&f=&url=%2Findex.html&_=";
            response = TaskHttpClient.create(param, RequestType.GET, "an_hui_10086_web_009").setFullUrl(templateUrl).setReferer(referer)
                    .setRequestContentType(ContentType.APPLICATION_JSON).addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01").invoke();

            if (StringUtils.contains(response.getPageContent(), param.getMobile().toString())) {
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
            String referer = "http://service.ah.10086.cn/pub-page/qry/qryDetail/billDetailIndex.html?kind=200011522&f=200011538&area=cd";
            String templateUrl = "https://service.ah.10086.cn/busi/broadbandZQ/getSubmitId?type=billDetailIndex_submitId&_={}";
            response = TaskHttpClient.create(param, RequestType.GET, "an_hui_10086_web_010").setFullUrl(templateUrl, System.currentTimeMillis())
                    .setReferer(referer).setRequestContentType(ContentType.APPLICATION_JSON).invoke();
            JSONObject jsonObject = response.getPageContentForJSON();
            String submitId = (String) JSONPath.eval(jsonObject, "$.object.yzm_submitId");

            templateUrl = "https://service.ah.10086.cn/pub/sendSmPass?opCode=EC20&phone_No=&type=billDetailIndex_submitId&yanzm_submitId={}&_={}";
            response = TaskHttpClient.create(param, RequestType.GET, "an_hui_10086_web_010")
                    .setFullUrl(templateUrl, submitId, System.currentTimeMillis()).setReferer(referer)
                    .setRequestContentType(ContentType.APPLICATION_JSON).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "retMsg\":\"OK")) {
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

    public HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
        Response response = null;
        try {
            String referer = "https://service.ah.10086.cn/pub-page/qry/qryDetail/billDetailIndex.html?kind=200011522&f=200011538&area=cd";
            String templateUrl = "https://service.ah.10086.cn/pub/chkSmPass?smPass={}&phone_No=&_={}";
            response = TaskHttpClient.create(param, RequestType.GET, "he_bei_10086_web_014")
                    .setFullUrl(templateUrl, param.getSmsCode(), System.currentTimeMillis()).setReferer(referer)
                    .addHeader("X-Requested-With", "XMLHttpRequest").setRequestContentType(ContentType.APPLICATION_JSON).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "retMsg\":\"OK")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    public HttpResult<Object> processForUifEncode(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            JSONObject jsonObject = JSON.parseObject(param.getArgs()[0]);
            String baseInfo = jsonObject.getString("page_content");
            JSONObject json = JSON.parseObject(baseInfo);
            Map baseinfoMap = new LinkedHashMap();

            String phone_no = (String) JSONPath.eval(json, "$.object.phone_no");
            String open_time = (String) JSONPath.eval(json, "$.object.open_time");
            String run_name = (String) JSONPath.eval(json, "$.object.run_name");
            String cust_name = (String) JSONPath.eval(json, "$.object.cust_name");
            String star_level = (String) JSONPath.eval(json, "$.object.star_level");

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_unifdecode.js", null);

            phone_no = invocable.invokeFunction("uifDecode", phone_no).toString();
            open_time = invocable.invokeFunction("uifDecode", open_time).toString();
            run_name = invocable.invokeFunction("uifDecode", run_name).toString();
            cust_name = invocable.invokeFunction("uifDecode", cust_name).toString();
            star_level = invocable.invokeFunction("uifDecode", star_level).toString();

            baseinfoMap.put("phone_no", phone_no);
            baseinfoMap.put("open_time", open_time);
            baseinfoMap.put("run_name", run_name);
            baseinfoMap.put("cust_name", cust_name);
            baseinfoMap.put("star_level", star_level);

            return result.success(baseinfoMap);
        } catch (Exception e) {
            logger.error("通话记录页访问失败,param={}", param, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
}
