package com.datatrees.rawdatacentral.plugin.operator.liao_ning_10086_web;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://www.ln.10086.cn/my/account/index.xhtml
 * Created by guimeichao on 17/9/6.
 */
public class LiaoNing10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(LiaoNing10086ForWeb.class);

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
        logger.warn("defineProcess fail,params={}", param);
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "https://login.10086.cn/html/window/loginMini.html?channelID=00240&backUrl=www.ln.10086.cn/sso/iLoginFrameCas.jsp";
            String templateUrl = "https://login.10086.cn/needVerifyCode.htm?accountType=01&account={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, param.getMobile())
                    .setReferer(referer).invoke();
            templateUrl = "https://login.10086.cn/sendRandomCodeAction.action?userName={}&channelID=00240&type=01";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl, param.getMobile())
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.equals("0", pageContent)) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.warn("登录-->短信验证码-->刷新失败,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_SMS_FAIL);
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
            String referer = "https://login.10086.cn/html/window/loginMini.html?channelID=00240&backUrl=www.ln.10086.cn/sso/iLoginFrameCas.jsp";
            String templateUrl = "https://login.10086.cn/login" +
                    ".htm?accountType=01&account={}&password={}&pwdType=01&smsPwd={}&inputCode=&backUrl=www.ln.10086.cn/sso/iLoginFrameCas.jsp&rememberMe=0&channelID=00240&protocol=https:";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), param.getPassword(), param.getSmsCode()).setReferer(referer).invoke();
            JSONObject json = response.getPageContentForJSON();
            String desc = json.getString("desc");
            if (StringUtils.equals("认证成功", desc)) {
                String redirectUrl = json.getString("assertAcceptURL");
                String artifact = json.getString("artifact");
                templateUrl = "{}?backUrl=www.ln.10086.cn/sso/iLoginFrameCas.jsp&artifact={}";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                        .setFullUrl(templateUrl, redirectUrl, artifact).invoke();
                templateUrl = "http://www.ln.10086.cn/my/account/index.xhtml";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
                if (StringUtils.contains(response.getPageContent(), param.getMobile().toString())) {
                    String menuId = PatternUtils.group(response.getPageContent(), "var _menuId = '(\\d+)'", 1);
                    TaskUtils.addTaskShare(param.getTaskId(), "menuId", menuId);
                    logger.info("登陆成功,param={}", param);
                    return result.success();
                } else {
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
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
            String count = TaskUtils.getTaskShare(param.getTaskId(), "count");
            if (count == null) {
                String templateUrl
                        = "http://www.ln.10086.cn/busicenter/myinfo/MyInfoMenuAction/initBusi.menu?_menuId=1040101&_menuId=1040101&divId=main";
                String referer = "http://www.ln.10086.cn/my/account/mydata.xhtml";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setReferer(referer).invoke();
                String pageContent = response.getPageContent();
                TaskUtils.addTaskShare(param.getTaskId(), "count", "send");
                if (pageContent.contains("<br class=\"spacer\"")) {
                    logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                }
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
            String templateUrl = "http://www.ln.10086.cn/busicenter/myinfo/MyInfoMenuAction/reSendSmsPassWd.menu?_menuId=1040101&commonSmsPwd=";
            String referer = "http://www.ln.10086.cn/my/account/mydata.xhtml";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("\"sendResult\":\"success\"")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            }
            logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.ln.10086.cn/busicenter/myinfo/MyInfoMenuAction/checkSmsPassWd.menu?_menuId=1040101&commonSmsPwd={}";
            String referer = "http://www.ln.10086.cn/my/account/mydata.xhtml";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl, param.getSmsCode())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("\"checkResult\":\"success\"")) {
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
