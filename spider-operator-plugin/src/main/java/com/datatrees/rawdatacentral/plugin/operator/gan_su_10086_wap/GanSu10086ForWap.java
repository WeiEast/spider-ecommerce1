package com.datatrees.rawdatacentral.plugin.operator.gan_su_10086_wap;

import javax.script.Invocable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.RegexpUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.spider.share.domain.http.HttpHeadKey;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/15.
 */
public class GanSu10086ForWap implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(GanSu10086ForWap.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            //获取cookie:JSESSIONID
            String loginUrl = "http://wap.gs.10086.cn/jsbo_oauth/login?redirectURL=http://wap.gs.10086.cn/index.html";
            String pageContent = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(loginUrl).invoke()
                    .getPageContent();
            //获取时间戳timestamp,这个很重要,没有的话后面刷新短信验证码不行
            String timestamp = RegexpUtils.select(pageContent, "jstimestamp = (\\d+)", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "timestamp", timestamp);
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
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
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            //http://wap.gs.10086.cn/jsbo_oauth/getNumMsg?mobile={}可以省掉

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("hex_md5", param.getPassword()).toString();

            //获取时间戳timestamp,这个很重要,不能用System.currentTimeMillis(),否则-1014
            String timestamp = TaskUtils.getTaskShare(param.getTaskId(), "timestamp");
            String loginUrl = "http://wap.gs.10086.cn/jsbo_oauth/popDoorPopLogonNew";
            Map<String, Object> params = new HashMap<>();
            params.put("mobile", param.getMobile());
            params.put("password", encryptPassword);
            params.put("loginType", 1);
            params.put("icode", null);
            params.put("fromFlag", "doorPage");
            params.put("isHasV", false);
            params.put("redirectUrl", "http://wap.gs.10086.cn/index.html");
            params.put("dxYzm", null);
            params.put("timestamp", timestamp);
            params.put("clickType", 1);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setUrl(loginUrl).setParams(params)
                    .addHeader(HttpHeadKey.X_REQUESTED_WITH, "XMLHttpRequest").invoke();
            JSONObject json = response.getPageContentForJSON();
            Integer rcode = json.getInteger("rcode");
            switch (rcode) {
                case 200:
                    logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                case 1020:
                    logger.warn("登录-->短信验证码-->刷新失败,手机号码或密码错误,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                default:
                    logger.error("登录-->短信验证码-->刷新失败,手机号码或密码错误,param={},pageContent={}", param, response);
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
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("hex_md5", param.getPassword()).toString();

            //获取时间戳timestamp,这个很重要,不能用System.currentTimeMillis(),否则-1014
            String timestamp = TaskUtils.getTaskShare(param.getTaskId(), "timestamp");
            String loginUrl = "http://wap.gs.10086.cn/jsbo_oauth/popDoorPopLogonNew";
            String referer = "http://wap.gs.10086.cn/jsbo_oauth/login?redirectURL=http://wap.gs.10086.cn/index.html";
            Map<String, Object> params = new HashMap<>();
            params.put("mobile", param.getMobile());
            params.put("password", encryptPassword);
            params.put("loginType", 1);
            params.put("icode", null);
            params.put("fromFlag", "doorPage");
            params.put("isHasV", false);
            params.put("redirectUrl", "http://wap.gs.10086.cn/index.html");
            params.put("dxYzm", param.getSmsCode());
            params.put("timestamp", timestamp);
            params.put("clickType", 2);

            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setUrl(loginUrl).setParams(params)
                    .setReferer(referer).addHeader(HttpHeadKey.X_REQUESTED_WITH, "XMLHttpRequest").invoke();
            JSONObject json = response.getPageContentForJSON();
            Integer rcode = json.getInteger("rcode");
            if (rcode != 1000) {
                logger.error("登陆失败,param={},pageContent={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            //获取重要cookie:SESSION否则个人信息要访问2次才能成功
            TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl("http://wap.gs.10086.cn/index.html")
                    .invoke();

            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .addHeader(HttpHeadKey.X_REQUESTED_WITH, "XMLHttpRequest")
                    .setFullUrl("http://wap.gs.10086.cn/actionDispatcher.do?reqUrl=MessageInfo").setReferer("http://wap.gs.10086.cn/index.html")
                    .invoke();
            json = response.getPageContentForJSON();
            String resultMsg = json.getString("resultMsg");

            if (!StringUtils.contains(resultMsg, "您尚未登录或已超时，请重新登录")) {
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

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wap.gs.10086.cn/actionDispatcher.do?reqUrl=sendSmsCode&busiNum=XDCX";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, "短信下发成功")) {
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
            String templateUrl
                    = "http://wap.gs.10086.cn/actionDispatcher.do?reqUrl=XDCX_YY_Query&busiNum=XDCX&operType=3&confirm_smsPassword={}&confirmFlg=1";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl, param.getSmsCode())
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "系统流程处理正常")) {
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

    private String getErrorMsg(String rcode) {
        String errorMsg = StringUtils.EMPTY;
        switch (rcode) {
            case "-4":
                errorMsg = "手机号码格式不正确";
                break;
            case "-5":
                errorMsg = "密码位数不正确";
                break;
            case "-6":
                errorMsg = "验证码位数不正确";
                break;
            case "-7":
                errorMsg = "验证码失效,请重新登录";
                break;
            case "-8":
                errorMsg = "验证码错误";
                break;
            case "-9":
                errorMsg = "用户信息为空";
                break;
            case "-12":
                errorMsg = "用户IP在登录黑名单中，不允许登录！";
                break;
            case "-13":
                errorMsg = "用户手机号码在登录黑名单中，不允许登录！";
                break;
            case "-2203":
                errorMsg = "您输入的号码非甘肃省归属，请切换至手机号码归属省登录！";
                break;
            case "-2230":
                errorMsg = "对不起,密码长度错误，请输入6位有效密码！";
                break;
            case "-2231":
                errorMsg = "对不起,密码包含非法字符，请重新输入！";
                break;
            case "-1020":
                errorMsg = "对不起,登录密码错误，请重新输入！";
                break;
            case "-1030":
                errorMsg = "您的密码错误次数已达到上限，为保障您的信息安全，服务密码登陆方式已锁定，请明天再试！";
                break;
            case "-1040":
                errorMsg = "对不起，您的IP已受限，请改天再试！";
                break;
            case "-2303":
                errorMsg = "对不起,您的号码已锁！";
                break;
            case "-1010":
                errorMsg = "对不起,登录手机号码或者密码错误，请重新输入！";
                break;
            case "-1016":
                errorMsg = "对不起，验证码已失效，请重新获取验证码";
                break;
        }
        return errorMsg;
    }
}
