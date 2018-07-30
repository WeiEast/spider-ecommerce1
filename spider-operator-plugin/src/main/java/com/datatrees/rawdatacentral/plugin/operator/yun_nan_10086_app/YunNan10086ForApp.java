package com.datatrees.rawdatacentral.plugin.operator.yun_nan_10086_app;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云南移动App
 * 登录方式：云南移动App
 * 登陆方式:服务密码登陆
 * 每日各类详单查询次数有限
 * 之后则会提示：您当日查询此类详单已上限，请明日再试。
 * Created by guimeichao on 17/8/28.
 */
public class YunNan10086ForApp implements OperatorPluginService {

    private static final Logger logger       = LoggerFactory.getLogger(YunNan10086ForApp.class);

    private static final String templateUrl  = "http://www.yn.10086.cn/appsrv/actionDispatcher.do";

    private              String templateData = "deviceid=D7F40D126FE979D7C24E5FB874DBB84D&appKey=11100&cstamp={}&internet=WIFI&sys_version=6.0.1" +
            "&screen" + "=1080x1920&model=Mi Note 2&imsi=460078065323889&imei=8697{}&md5sign={}&jsonParam={}";

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //登陆页没有获取任何cookie,不用登陆
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
        switch (param.getFormType()) {
            case "BILL_DETAILS":
                return processForBill(param);
            case "CALL_DETAILS":
                return processForDetails(param, "YYXD");
            case "SMS_DETAILS":
                return processForDetails(param, "DCXXD");
            case "NET_DETAILS":
                return processForDetails(param, "SWXD");
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String encryptPassword = EncryptUtilsForYunNan10086App.getEncryptString(param.getPassword());

            /**
             * [
             *  {
             *      "dynamicURI":"/login",
             *      "dynamicParameter":{
             *          "method":"lnU",
             *          "m":"userName",
             *          "p":"encryPwd",
             *          "deviceCode":"3644755659516982113_870344981452283454",
             *          "t":"1503888366795"
             *      },
             *      "dynamicDataNodeName":"pwdLogin_node"
             *  }
             * ]
             */

            Map<String, Object> dynamicParameter = new HashMap<>();
            dynamicParameter.put("method", "lnU");
            dynamicParameter.put("m", param.getMobile());
            dynamicParameter.put("p", encryptPassword);
            dynamicParameter.put("deviceCode", "3644755659516982113_870344981452283454");
            dynamicParameter.put("t", System.currentTimeMillis());
            Map<String, Object> params = new HashMap<>();
            params.put("dynamicURI", "/login");
            params.put("dynamicParameter", dynamicParameter);
            params.put("dynamicDataNodeName", "pwdLogin_node");

            List<Object> paramsList = new ArrayList<>();
            paramsList.add(params);

            String base64Data = Base64.getEncoder().encodeToString(JSON.toJSONString(paramsList).getBytes());

            String data = TemplateUtils
                    .format(templateData, System.currentTimeMillis(), param.getMobile(), EncryptUtilsForYunNan10086App.md5sign(base64Data),
                            base64Data);
            //没有请求头{ "platform" : "android", "version" : "5.0.4"}会导致请求失败
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).addHeader("platform", "android").addHeader("version", "5.0.4")
                    .invoke();
            /**
             * 结果枚举
             *
             * 登陆成功
             * {"pwdLogin_node":{"errorCode":null,"errorMessage":null,"resultCode":"1","resultObj":{"androidCookie":"25E69B900F84332EB6EB178771711DB8","bal":""
             * ,"bbn":"QQT","bjn":"QQT","bjnn":"全球通","cbn":"DLDQ","certf":true,"certmsg":"","certurl":"","cityName":"大理地区","cjn":"DLDQ","is4G":null,
             * "mobile":"18787298850","mp":"","name":"陈*","sc":"","starLevel":"0","uad":"2016-12-30 17:17:09","uan":"0113","ubn":"G001","uc":0,"upm":null,
             * "us":"0","usd":null,"validMsg":""}}}
             *
             * 手机号非云南移动
             * {"pwdLogin_node":{"errorCode":"-900006","errorMessage":"设置服务中心异常 OrderCentre.person.ICCOutOperateSV.checkSubscriberPwd","resultCode":"0","resultObj":null}}
             *
             * 账号或密码错误
             * {"pwdLogin_node":{"errorCode":"-900006","errorMessage":"用户服务密码错误！","resultCode":"0","resultObj":null}}
             */
            JSONObject json = response.getPageContentForJSON();
            String resultCode = (String) JSONPath.eval(json, "$.pwdLogin_node.resultCode");
            if (StringUtils.equals("1", resultCode)) {
                logger.info("登陆成功,param={}", param);
                //存储登陆响应信息里的个人信息
                TaskUtils.addTaskShare(param.getTaskId(), "baseInfo", response.getPageContent());
                return result.success();
            } else {
                String errorMessage = (String) JSONPath.eval(json, "$.pwdLogin_node.errorMessage");
                if (StringUtils.contains(errorMessage, "用户服务密码错误")) {
                    logger.warn("登录失败-->账户名与密码不匹配,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                } else if (StringUtils.contains(errorMessage, "设置服务中心异常")) {
                    logger.warn("登录失败-->手机号码归属地不符(可能性比较大)/其他异常,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PHONE_FAIL);
                } else {
                    logger.error("登陆失败,param={},pageContent={}", param, response);
                    return result.failure(errorMessage);
                }
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
            /**[
             *  {
             *      "dynamicURI":"/sms",
             *      "dynamicParameter":{
             *          "method":"sendSmsCode",
             *          "mobile":"userName"
             *      },
             *      "dynamicDataNodeName":"smsPwdLogin"
             *  }
             *  ]
             */
            Map<String, Object> dynamicParameter = new HashMap<>();
            dynamicParameter.put("method", "sendSmsCode");
            dynamicParameter.put("mobile", param.getMobile());
            Map<String, Object> params = new HashMap<>();
            params.put("dynamicURI", "/sms");
            params.put("dynamicParameter", dynamicParameter);
            params.put("dynamicDataNodeName", "smsPwdLogin");
            List<Object> paramsList = new ArrayList<>();
            paramsList.add(params);

            String base64Data = Base64.getEncoder().encodeToString(JSON.toJSONString(paramsList).getBytes());

            String data = TemplateUtils
                    .format(templateData, System.currentTimeMillis(), param.getMobile(), EncryptUtilsForYunNan10086App.md5sign(base64Data),
                            base64Data);
            //没有请求头{ "platform" : "android", "version" : "5.0.4"}会导致请求失败
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).addHeader("platform", "android").addHeader("version", "5.0.4")
                    .invoke();
            //{"smsPwdLogin":{"errorCode":"","errorMessage":"","resultCode":"1","resultObj":"短信下发成功"}}
            JSONObject json = response.getPageContentForJSON();
            String resultCode = (String) JSONPath.eval(json, "$.smsPwdLogin.resultCode");
            if (StringUtils.equals("1", resultCode)) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response);
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
            /**
             * [
             *  {
             *      "dynamicURI":"/sms",
             *      "dynamicParameter":{
             *          "method":"ckeckSmsCode",
             *          "m":"18787298850",
             *          "p":"721721"
             *      },
             *      "dynamicDataNodeName":"ckeckSmsCode_node"
             *  }
             * ]
             */
            Map<String, Object> dynamicParameter = new HashMap<>();
            dynamicParameter.put("method", "ckeckSmsCode");
            dynamicParameter.put("m", param.getMobile());
            dynamicParameter.put("p", param.getSmsCode());
            Map<String, Object> params = new HashMap<>();
            params.put("dynamicURI", "/sms");
            params.put("dynamicParameter", dynamicParameter);
            params.put("dynamicDataNodeName", "ckeckSmsCode_node");

            List<Object> paramsList = new ArrayList<>();
            paramsList.add(params);

            String base64Data = Base64.getEncoder().encodeToString(JSON.toJSONString(paramsList).getBytes());

            String data = TemplateUtils
                    .format(templateData, System.currentTimeMillis(), param.getMobile(), EncryptUtilsForYunNan10086App.md5sign(base64Data),
                            base64Data);
            //没有请求头{ "platform" : "android", "version" : "5.0.4"}会导致请求失败
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).addHeader("platform", "android").addHeader("version", "5.0.4")
                    .invoke();
            /**
             * 结果枚举
             *
             * 验证成功
             *{"ckeckSmsCode_node":{"errorCode":"","errorMessage":"","resultCode":"1","resultObj":null}}
             * 短信验证码不正确
             * {"ckeckSmsCode_node":{"errorCode":"-20028","errorMessage":"对不起，您输入的验证码不正确","resultCode":"0","resultObj":null}}
             */
            JSONObject json = response.getPageContentForJSON();
            String resultCode = (String) JSONPath.eval(json, "$.ckeckSmsCode_node.resultCode");
            switch (resultCode) {
                case "1":
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                case "0":
                    logger.warn("详单-->短信验证码错误,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("详单-->校验失败,param={},pageContent={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Object> processForBill(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();

        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String billMonth = paramMap.get("page_content");
        Response response = null;
        try {
            /**
             * [
             *  {
             *      "dynamicURI":"/bill",
             *      "dynamicParameter":{
             *          "method":"getAccountBillU",
             *          "busiNum":"ZDCX",
             *          "beginDate":"billMonth"
             *      },
             *      "dynamicDataNodeName":"getAccountBill_node"
             *  }
             * ]
             */
            Map<String, Object> dynamicParameter = new HashMap<>();
            dynamicParameter.put("method", "getAccountBillU");
            dynamicParameter.put("busiNum", "ZDCX");
            dynamicParameter.put("beginDate", billMonth);
            Map<String, Object> params = new HashMap<>();
            params.put("dynamicURI", "/bill");
            params.put("dynamicParameter", dynamicParameter);
            params.put("dynamicDataNodeName", "getAccountBill_node");

            List<Object> paramsList = new ArrayList<>();
            paramsList.add(params);

            String base64Data = Base64.getEncoder().encodeToString(JSON.toJSONString(paramsList).getBytes());

            String data = TemplateUtils
                    .format(templateData, System.currentTimeMillis(), param.getMobile(), EncryptUtilsForYunNan10086App.md5sign(base64Data),
                            base64Data);
            //没有请求头{ "platform" : "android", "version" : "5.0.4"}会导致请求失败
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).addHeader("platform", "android").addHeader("version", "5.0.4")
                    .invoke();
            return result.success(response.getPageContent());
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForDetails(OperatorParam param, String queryType) {
        HttpResult<Object> result = new HttpResult<>();

        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String[] times = paramMap.get("page_content").split("#");

        Response response = null;
        try {
            /**
             * [
             *  {
             *      "dynamicURI":"/bill",
             *      "dynamicParameter":{
             *          "method":"getFeelListDetail",
             *          "queryMonth":"201705",
             *          "queryType":"YYXD",
             *          "startDate":"01",
             *          "endDate":"31",
             *          "queryFilterMobile":""
             *      },
             *      "dynamicDataNodeName":"getFeelListDetail_node"
             *  }
             * ]
             */
            Map<String, Object> dynamicParameter = new HashMap<>();
            dynamicParameter.put("method", "getFeelListDetail");
            dynamicParameter.put("queryMonth", times[0]);
            dynamicParameter.put("queryType", queryType);
            dynamicParameter.put("startDate", times[1]);
            dynamicParameter.put("endDate", times[2]);
            dynamicParameter.put("queryFilterMobile", "");
            Map<String, Object> params = new HashMap<>();
            params.put("dynamicURI", "/bill");
            params.put("dynamicParameter", dynamicParameter);
            params.put("dynamicDataNodeName", "getFeelListDetail_node");

            List<Object> paramsList = new ArrayList<>();
            paramsList.add(params);

            String base64Data = Base64.getEncoder().encodeToString(JSON.toJSONString(paramsList).getBytes());

            String data = TemplateUtils
                    .format(templateData, System.currentTimeMillis(), param.getMobile(), EncryptUtilsForYunNan10086App.md5sign(base64Data),
                            base64Data);
            //没有请求头{ "platform" : "android", "version" : "5.0.4"}会导致请求失败
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).addHeader("platform", "android").addHeader("version", "5.0.4")
                    .invoke();
            logger.info(response.getPageContent());
            for (int i = 0; i < 5; i++) {
                if (response.getPageContent().contains("系统繁忙")) {
                    response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                            .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).addHeader("platform", "android")
                            .addHeader("version", "5.0.4").invoke();
                    logger.info("系统繁忙，刷新" + (i + 1) + "次，页面为：" + response.getPageContent());
                } else {
                    break;
                }
            }
            return result.success(response.getPageContent());
        } catch (Exception e) {
            logger.error("通话记录页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
}
