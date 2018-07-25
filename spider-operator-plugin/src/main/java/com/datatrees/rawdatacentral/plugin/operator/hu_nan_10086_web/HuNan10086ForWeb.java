package com.datatrees.rawdatacentral.plugin.operator.hu_nan_10086_web;

import javax.script.Invocable;
import java.math.BigDecimal;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginPostService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/14.
 */
public class HuNan10086ForWeb implements OperatorPluginPostService {

    private static final Logger logger = LoggerFactory.getLogger(HuNan10086ForWeb.class);

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
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://www.hn.10086.cn/service/static/componant/login.html";
            String templateUrl = "http://www.hn.10086.cn/service/ics/login/sendSms?serialNumber={}&validateCode=&chanId=E003&operType=LOGIN" +
                    "&goodsName=发送短信验证码&loginType=2&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), Math.random()).setReferer(referer).invoke();

            if (StringUtils.contains(response.getPageContent(), "随机短信验证码已经下发")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
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
            String encryptPassword = invocable.invokeFunction("strEnc", param.getPassword(), param.getMobile().toString().substring(0, 8),
                    param.getMobile().toString().substring(1, 9), param.getMobile().toString().substring(3, 11)).toString();

            BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
            String referer = "http://www.hn.10086.cn/newservice/static/componant/login.html";
            String templateUrl = "http://www.hn.10086.cn/service/ics/login/SSOLogin?REMEMBER_TAG=false&SERIAL_NUMBER={}&LOGIN_TYPE=2&USER_PASSWD" +
                    "={}&USER_PASSSMS={}&VALIDATE_CODE=&chanId=E003&operType=LOGIN&goodsName=%E6%9C%8D%E5%8A%A1%E5%AF%86%E7%A0%81%E7%99%BB%E5%BD%95" +
                    "&loginType=0&ajaxSubmitType=post&ajax_randomcode={}";
            //获取tokenId并追加到cookie
            String tokenId = db.setScale(17, BigDecimal.ROUND_HALF_UP).toString();
            TaskUtils.addTaskShare(param.getTaskId(), "tokenId", tokenId);

            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, param.getMobile(), encryptPassword, param.getSmsCode(), db.setScale(16, BigDecimal.ROUND_HALF_UP))
                    .setReferer(referer).addExtralCookie("www.hn.10086.cn", tokenId, "0").invoke();
            JSONObject json = response.getPageContentForJSON();
            String RESULT = json.getString("RESULT");
            String RESULTINFO = json.getString("RESULTINFO");
            if (StringUtils.equals(RESULTINFO, "登陆成功") || StringUtils.equals(RESULT, "0")) {
                String ARTIFACT = json.getString("ARTIFACT");
                TaskUtils.addTaskShare(param.getTaskId(), "ARTIFACT", ARTIFACT);
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(RESULTINFO);
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
            String tokenId = TaskUtils.getTaskShare(param.getTaskId(), "tokenId");
            double random = Math.random();
            String referer = "http://www.hn.10086.cn/service/static/myMobile/detailBillQuery.html";
            String templateUrl = "http://www.hn.10086.cn/service/ics/componant/initTelQCellCore?tel={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, param.getMobile(), random).setReferer(referer)
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01").addExtralCookie("www.hn.10086.cn", random + "", "0")
                    .invoke();

            random = Math.random();
            referer = "http://www.hn.10086.cn/service/static/myMobile/detailBillQuery.html";
            templateUrl = "http://www.hn.10086.cn/service/ics/componant/initSendHattedCode?requestTel={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, param.getMobile(), random).setReferer(referer)
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01").addExtralCookie("www.hn.10086.cn", random + "", "0")
                    .invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, "短信密码已经发送到您的手机") || StringUtils.contains(pageContent, "短信密码5分钟之内有效，请不要重复获取")) {
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
            String tokenId = TaskUtils.getTaskShare(param.getTaskId(), "tokenId");
            double random = Math.random();
            String referer = "http://www.hn.10086.cn/service/static/myMobile/detailBillQuery.html";
            String templateUrl = "http://www.hn.10086.cn/service/ics/componant/initTelQCellCore?tel={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, param.getMobile(), random).setReferer(referer)
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01").addExtralCookie("www.hn.10086.cn", random + "", "0")
                    .invoke();

            random = Math.random();
            referer = "http://www.hn.10086.cn/service/static/myMobile/detailBillQuery.html";
            templateUrl = "http://www.hn.10086.cn/service/ics/componant/initSmsCodeAndServicePwd?smsCode={}&servicePwd=NaN&requestTel" +
                    "={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, param.getSmsCode(), param.getMobile(), random).setReferer(referer)
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01").addExtralCookie("www.hn.10086.cn", random + "", "0")
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "resultCode\":0")) {
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
            String ARTIFACT = TaskUtils.getTaskShare(param.getTaskId(), "ARTIFACT");
            String referer = "http://www.hn.10086.cn/service/static/componant/login.html";
            String templateUrl
                    = "https://login.10086.cn/AddUID.htm?channelID=00731&Artifact={}&backUrl=http://www.hn.10086.cn/service/static/index.html&TransactionID={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, ARTIFACT, System.currentTimeMillis()).setReferer(referer).invoke();
            referer = "http://www.hn.10086.cn/service/static/index.html";
            templateUrl = "https://login.10086.cn/SSOCheck.action?channelID=12034&backUrl=http://www.hn.10086.cn/service/static/index.html";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
