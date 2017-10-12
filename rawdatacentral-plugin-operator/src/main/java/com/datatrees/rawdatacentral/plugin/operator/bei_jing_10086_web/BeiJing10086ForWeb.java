package com.datatrees.rawdatacentral.plugin.operator.bei_jing_10086_web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.RegexpUtils;
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
            TaskHttpClient.create(param, RequestType.GET, "").setFullUrl("https://bj.ac.10086.cn/login").invoke();
            //获取cookie:JSESSIONID
            TaskHttpClient.create(param, RequestType.GET, "").setFullUrl("https://bj.ac.10086.cn/ac/cmsso/iloginnew.jsp").invoke();
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
            String templateUrl = "https://bj.ac.10086.cn/ac/ValidateNum?smartID={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "")
                    .setFullUrl(templateUrl, Math.ceil(Math.random() * 10000000000L)).invoke();
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
            String refer = "https://bj.ac.10086.cn/ac/cmsso/iloginnew.jsp";
            String templateUrl = "https://bj.ac.10086.cn/ac/ValidateRnum?loginMethod=1&loginMode=1&phone={}&rnum={}&service=www.bj.10086.cn&user={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "zhe_jiang_10086_web_003")
                    .setFullUrl(templateUrl, param.getMobile(), param.getPicCode(), param.getMobile()).setReferer(refer).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("对不起，您输入的验证码不正确")) {
                logger.warn("登录-->图片验证码-->校验失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            } else {
                logger.info("登录-->图片验证码-->校验成功,param={},pageContent={}", param, pageContent);
                return result.success();
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
        try {
            String referer = "https://bj.ac.10086.cn/ac/CmSsoLogin";
            String templateUrl = "https://bj.ac.10086.cn/ac/CmSsoLogin?backurl=http%3A%2F%2Fwww" +
                    ".bj.10086.cn%2Fmy&box=&ckCookie=on&continue=http%3A%2F%2Fwww" +
                    ".bj.10086.cn%2Fmy&loginMethod=1&loginMode=1&loginName={}&password={}&phone={}&rnum={}&service=www" +
                    ".bj.10086.cn&smsNum=%C3%8B%C3%A6%C2%BB%C3%BA%C3%82%C3%AB&ssoLogin=yes&style=BIZ_LOGINBOX&target=_parent&user={}";
            String loginUrl = TemplateUtils
                    .format(templateUrl, param.getMobile(), param.getPassword(), param.getMobile(), param.getPicCode(), param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "bei_jing_10086_web_002").setFullUrl(loginUrl).setReferer(referer).invoke();
            String redirectUrl = response.getRedirectUrl();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(redirectUrl)) {
                String errorCode = RegexpUtils.select(pageContent, "var \\$fcode = '(.{1,20})';", 1);
                String errorMsg = getErrorMsg(errorCode);
                if (!StringUtils.endsWith("PP_1_0807", errorCode)) {
                    logger.warn("登录失败,param={},errorCode={},errorMsg={}", param, errorCode, errorMsg);
                    return result.failure(ErrorCode.LOGIN_FAIL, errorMsg);
                }
                //已经登录
                referer = "https://bj.ac.10086.cn/ac/CmSsoLogin";
                String loginAgainUrl = "https://bj.ac.10086.cn/ac/loginAgain?backurl=http%3A%2F%2Fwww" +
                        ".bj.10086.cn%2Fmy&box=&continue=http%3A%2F%2Fwww" +
                        ".bj.10086.cn%2Fmy&hostId=4&loginMethod=1&loginMode=1&service=www.bj.10086.cn&style=BIZ_LOGINBOX&submit=&target=_self";
                TaskHttpClient.create(param, RequestType.POST, "").setFullUrl(loginAgainUrl).setReferer(referer).invoke();

            }
            String indexUrl = "http://www.bj.10086.cn/my";
            TaskHttpClient.create(param, RequestType.GET, "").setFullUrl(indexUrl).setReferer(referer).invoke();
            //详单校验
            templateUrl = "https://service.bj.10086.cn/poffice/package/xdcx/userYzmCheck.action?PACKAGECODE=XD&yzCheckCode={}";
            //不小心改了密码
            String validateCallDetailUrl = TemplateUtils.format(templateUrl, param.getMobile() == 15001285176l ? "716253" : param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "").setFullUrl(validateCallDetailUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "RelayState")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            if (StringUtils.contains(pageContent, "RelayState")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }

            JSONObject json = JSON.parseObject(pageContent);
            String message = json.getString("message");
            if (StringUtils.equals("Y", message)) {
                logger.info("登录成功,param={}", param);
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

}

