package com.datatrees.rawdatacentral.plugin.operator.bei_jing_10086_web;

import javax.script.Invocable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginPostService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一天最多只能发送8条短信随机码
 * Created by guimeichao on 17/9/13.
 */
public class BeiJing10086ForWebByPwd implements OperatorPluginPostService {

    private static final Logger logger = LoggerFactory.getLogger(BeiJing10086ForWebByPwd.class);

    /**
     * 错误信息
     * @param errorCode
     * @return
     */
    private String getErrorMsg(String errorCode) {
        String json = "{\"PP_2_0008\":\"温馨提示：您的网站密码安全级别过低，请您修改密码后再重新登录！\",\"PP_9_0101\":\"温馨提示：您输入的手机号码格式不正确，请重新输入!\"," +
                "\"PP_1_0807\":\"温馨提示：您已经登录本网站，请稍后再试！\",\"PP_9_0110\":\"对不起，您的账号不能在本站登录！\",\"PP_1_0806\":\"由于您一直未注册，为了您的个人信息安全请注册后再使用。\",\"PP_1_0805\":\"您一直使用随机密码方式登录但从未注册，请您注册后使用。\",\"PP_1_0803\":\"温馨提示：您输入的用户名或密码不正确！\",\"PP_1_0802\":\"温馨提示：您尚未注册，请注册后使用。\",\"PP_1_2020\":\"温馨提示：请输入正确的短信随机密码!\",\"PP_1_2021\":\"温馨提示：用户名不能为空!\",\"PP_1_0822\":\"您的账户已经进入锁定状态，如需解锁，请点击“忘记密码？”功能进行密码重置\",\"PP_1_0821\":\"累计密码错误次数已达到上限，请用找回密码功能重置密码!\",\"PP_1_0820\":\"对不起，密码输入错误三次，账号锁定!\",\"CMSSO_1_0500\":\"系统忙，请稍候再试！\",\"PP_1_2014\":\"随机密码已经发送，请注意查收！\",\"PP_1_2015\":\"温馨提示：验证码不能为空!\",\"PP_1_2016\":\"您输入的验证码不正确!\",\"PP_1_1002\":\"手机号码非北京号码,请切换至号码归属地。\",\"PP_1_2017\":\"您输入的验证码不正确!\",\"PP_1_2018\":\"温馨提示：请输入正确的短信随机密码!\",\"PP_1_2019\":\"温馨提示：请输入正确的短信随机密码!\",\"PP_0_1017\":\"对不起，您的手机号状态为停机！\",\"PP_0_1018\":\"对不起，您的手机号状态为注销！\"}\n";
        Map<String, String> map = JSON.parseObject(json, new TypeReference<Map<String, String>>() {});
        return map.get(errorCode);

    }

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //获取cookie:Webtrends
            TaskHttpClient.create(param, RequestType.GET, "").setFullUrl("https://login.10086.cn/html/bj/login.html").invoke();
            //获取cookie:JSESSIONID
            TaskHttpClient.create(param, RequestType.GET, "")
                    .setFullUrl("https://login.10086.cn/html/bj/iloginnew.html?{}", System.currentTimeMillis()).invoke();
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
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://login.10086.cn/captchazh.htm?type=12&timestamp={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "")
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return validatePicCodeForLogin(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String picCode = param.getPicCode();
            boolean check = Boolean.parseBoolean(invocable.invokeFunction("checkzh", picCode).toString());
            if (check) {
                picCode = invocable.invokeFunction("toUnicode", picCode).toString();
            } else {
                picCode = picCode.toUpperCase();
            }

            String refer = "https://login.10086.cn/html/bj/iloginnew.html?{}";
            String templateUrl = "https://login.10086.cn/verifyCaptcha?inputCode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "").setFullUrl(templateUrl, picCode)
                    .setReferer(refer, System.currentTimeMillis()).invoke();
            JSONObject jsonObject = response.getPageContentForJSON();
            String resultCode = jsonObject.getString("resultCode");
            if (StringUtils.equals(resultCode, "0")) {
                logger.info("登录-->图片验证码-->校验成功,param={},pageContent={}", param, response.getPageContent());
                return result.success();
            } else {
                logger.warn("登录-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            }
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return null;
    }

    public HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = validatePicCodeForLogin(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        String passWord = param.getPassword();
        if (StringUtils.equals(param.getMobile().toString(), "15001285176")) {
            passWord = "GFD123456";
        }
        try {
            String referer = "https://login.10086.cn/html/bj/iloginnew.html?{}";
            String templateUrl = "https://login.10086.cn/touchBjLogin.action";
            String templateData = "rememberMe=1&accountType=01&pwdType=04&account={}&password={}&inputCode={}&channelID=00100&protocol=https%3A" +
                    "&timestamp={}";
            String data = TemplateUtils
                    .format(templateData, param.getMobile(), passWord, URLEncoder.encode(param.getPicCode(), "UTF-8"), System.currentTimeMillis());
            response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_002").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer, System.currentTimeMillis()).invoke();
            JSONObject json = response.getPageContentForJSON();
            String code = json.getString("code");
            String desc = json.getString("desc");
            if (!StringUtils.equals(code, "0000")) {
                if (StringUtils.equals(code,"3003")){
                    logger.info("登录失败,{},param={}", desc, param);
                    return result.failure("您的密码较简单，请修改密码后重新登录");
                }
                logger.info("登录失败,{},param={}", desc, param);
                return result.failure(desc);
            }

            String artifact = json.getString("artifact");
            String assertAcceptURL = json.getString("assertAcceptURL");
            templateUrl = "{}?backUrl=http%3A%2F%2Fservice.bj.10086.cn&artifact={}";
            response = TaskHttpClient.create(param, RequestType.GET, "bei_jing_10086_web_003").setFullUrl(templateUrl, assertAcceptURL, artifact)
                    .setReferer(referer, System.currentTimeMillis()).invoke();
            logger.info("登录成功,param={}", param);
            return result.success();
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

    @Override
    public HttpResult<Map<String, Object>> loginPost(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://login.10086.cn/SSOCheck" +
                    ".action?channelID=12034&backUrl=http%3A%2F%2Fwww.10086.cn%2Findex%2Fbj%2Findex_100_100.html";
            TaskHttpClient.create(param, RequestType.GET, "").setFullUrl(templateUrl).invoke();

            templateUrl = "http://www1.10086.cn/web-Center/authCenter/assertionQuery.do";
            String templateData = "requestJson=%7B%22serviceName%22%3A%22if008_query_user_assertion%22%2C%22header%22%3A%7B%22version%22%3A%221.0" +
                    "%22" + "%2C%22timestamp%22%3A{}%2C%22digest%22%3A%22{}%22%2C%22conversationId%22%3A%22%22%7D%2C%22data%22" +
                    "%3A%7B%22channelId%22%3A%2212034%22%7D%7D";
            String timestamp = System.currentTimeMillis() + "";
            Invocable invocableForMd5 = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "md5.js", "GBK");
            String md5String = invocableForMd5.invokeFunction("getDigest", timestamp).toString();
            String data = TemplateUtils.format(templateData, timestamp, URLEncoder.encode(md5String, "UTF-8"));
            response = TaskHttpClient.create(param, RequestType.POST, "").setFullUrl(templateUrl).setRequestBody(data).invoke();

            templateUrl = "https://www1.10086.cn/web-Center/interfaceService/realFeeQry.do";
            templateData = "requestJson=%7B%22serviceName%22%3A%22if007_query_fee%22%2C%22header%22%3A%7B%22version%22%3A%221.0%22" +
                    "%2C%22timestamp%22%3A{}%2C%22digest%22%3A%22{}%22%2C%22conversationId%22%3A%22%22%7D%2C%22data%22" +
                    "%3A%7B%22channelId%22%3A%220001%22%7D%7D";
            timestamp = System.currentTimeMillis() + "";
            md5String = invocableForMd5.invokeFunction("getDigest", timestamp).toString();
            data = TemplateUtils.format(templateData, timestamp, URLEncoder.encode(md5String, "UTF-8"));
            response = TaskHttpClient.create(param, RequestType.POST, "").setFullUrl(templateUrl).setRequestBody(data).invoke();
            TaskUtils.addTaskShare(param.getTaskId(), "balancePage", response.getPageContent());

            templateUrl = "https://www1.10086.cn/web-Center/interfaceService/custInfoQry.do";
            templateData = "requestJson=%7B%22serviceName%22%3A%22if007_query_user_info%22%2C%22header%22%3A%7B%22version%22%3A%221.0%22" +
                    "%2C%22timestamp%22%3A{}%2C%22digest%22%3A%22{}%22%2C%22conversationId%22%3A%22%22%7D%2C%22data%22" +
                    "%3A%7B%22channelId%22%3A%220001%22%7D%7D";
            md5String = invocableForMd5.invokeFunction("getDigest", timestamp).toString();
            data = TemplateUtils.format(templateData, timestamp, URLEncoder.encode(md5String, "UTF-8"));
            response = TaskHttpClient.create(param, RequestType.POST, "").setFullUrl(templateUrl).setRequestBody(data).invoke();
            TaskUtils.addTaskShare(param.getTaskId(), "baseInfoPage", response.getPageContent());
            TaskUtils.addTaskShare(param.getTaskId(), "baseInfoUrl", templateUrl);

            //详单校验
            templateUrl = "https://service.bj.10086.cn/poffice/package/xdcx/userYzmCheck.action?PACKAGECODE=XD&yzCheckCode={}";
            String validateCallDetailUrl = TemplateUtils.format(templateUrl, param.getPassword());
            response = TaskHttpClient.create(param, RequestType.POST, "").setFullUrl(validateCallDetailUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "RelayState")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            if (StringUtils.contains(pageContent, "RelayState")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }

            JSONObject json = JSON.parseObject(pageContent);
            String message = json.getString("message");
            if (StringUtils.equals("Y", message)) {
                logger.info("详单校验成功,param={}", param);
                TaskHttpClient.create(param, RequestType.GET, "")
                        .setFullUrl("https://login.10086.cn/SSOCheck.action?channelID=12003&backUrl=http://shop.10086.cn/i/?f=custinfoqry").invoke();
                return result.success();
            } else {
                logger.warn("详单校验失败,param={}", param);
                return result.failure(ErrorCode.LOGIN_FAIL, "详单校验失败");
            }

        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}

