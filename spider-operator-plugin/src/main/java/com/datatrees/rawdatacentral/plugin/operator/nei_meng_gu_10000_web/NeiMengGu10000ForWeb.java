package com.datatrees.rawdatacentral.plugin.operator.nei_meng_gu_10000_web;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/25.
 */
public class NeiMengGu10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(NeiMengGu10000ForWeb.class);

    private              LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    //中文转为Unicode
    public static String unicode(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "%u" + hexB;  //注：此处 "\\u" 应当前运营商需求转为 "%u"
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCode(param);
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

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }

            String referer = "http://www.189.cn/nm/";
            String templateUrl = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10008&toStUrl=http://nm.189.cn/selfservice/bill/hf";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();

            //String intLoginType = "4";
            //String areaCode = "0471";
            //String isBusinessCustType = "N";
            //String identifyType = "B";
            //String userLoginType = "4";
            //String noCheck = "N";
            //String isSSOLogin = "Y";
            //String sRand = "SSOLogin";

            String intLoginType = PatternUtils.group(pageContent, "intLoginType:\"(\\d+)\"", 1);
            String areaCode = PatternUtils.group(pageContent, "areaCode:\"(\\d+)\"", 1);
            String isBusinessCustType = PatternUtils.group(pageContent, "isBusinessCustType:\"([^\"]+)\"", 1);
            String identifyType = PatternUtils.group(pageContent, "identifyType:\"([^\"]+)\"", 1);
            String userLoginType = PatternUtils.group(pageContent, "identifyType:\"(\\d+)\"", 1);
            String noCheck = PatternUtils.group(pageContent, "noCheck:\"([^\"]+)\"", 1);
            String isSSOLogin = PatternUtils.group(pageContent, "isSSOLogin:\"([^\"]+)\"", 1);
            String sRand = PatternUtils.group(pageContent, "sRand:\"([^\"]+)\"", 1);

            referer = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10008&toStUrl=http://nm.189.cn/selfservice/bill/hf";
            templateUrl = "http://nm.189.cn/selfservice/service/userLogin";
            /**
             *  {
             *      "number":"userName",
             *      "intLoginType":"intLoginType",
             *      "areaCode":"areaCode",
             *      "isBusinessCustType":"isBusinessCustType",
             *      "identifyType":"identifyType",
             *      "userLoginType":"userLoginType",
             *      "password":"",
             *      "randomPass":"",
             *      "noCheck":"noCheck",
             *      "isSSOLogin":"isSSOLogin",
             *      "sRand":"sRand"
             *  }
             */
            Map<String, Object> params = new HashMap<>();
            params.put("number", param.getMobile().toString());
            params.put("intLoginType", intLoginType);
            params.put("areaCode", areaCode);
            params.put("isBusinessCustType", isBusinessCustType);
            params.put("identifyType", identifyType);
            params.put("userLoginType", userLoginType);
            params.put("password", "");
            params.put("randomPass", "");
            params.put("noCheck", noCheck);
            params.put("isSSOLogin", isSSOLogin);
            params.put("sRand", sRand);

            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();

            //templateUrl = "http://nm.189.cn/selfservice/bill/hfQuery";
            ///**
            // * {
            // *      "accNbr":"userName",
            // *      "accNbrType":"4",
            // *      "areaCode":"0476",
            // *      "prodSpecId":"378",
            // *      "smsCode":"",
            // *      "prodSpecName":"??"
            // * }
            // */
            //params = new HashMap<>();
            //params.put("accNbr", param.getMobile().toString());
            //params.put("accNbrType", "4");
            //params.put("areaCode", "0476");
            //params.put("prodSpecId", "378");
            //params.put("smsCode", "");
            //params.put("prodSpecName", "??");
            //
            //data = JSON.toJSONString(params);
            //response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
            //        .setRequestBody(data, ContentType.APPLICATION_JSON).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
                areaCode = PatternUtils.group(pageContent, "areaCode\":\"(\\d+)\"", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "areaCode", areaCode);

                templateUrl = "http://nm.189.cn/selfservice/cust/queryAllProductInfo";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setRequestBody("{\"qryAccNbrType\":\"\"}", ContentType.APPLICATION_JSON).invoke();
                pageContent = response.getPageContent();
                String prodSpecId = PatternUtils.group(pageContent, "\"prodSpecId\":\"(\\d+)\"", 1);
                String productType = PatternUtils.group(pageContent, "\"productType\":\"(\\d+)\"", 1);

                TaskUtils.addTaskShare(param.getTaskId(), "prodSpecId", prodSpecId + "");
                TaskUtils.addTaskShare(param.getTaskId(), "productType", productType + "");

                logger.warn("登录成功,params={}", param);
                return result.success();
            } else {
                logger.warn("登录失败,param={},response={}", param, response);
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
            String referer = "http://nm.189.cn/selfservice/bill/xd";
            String templateUrl = "http://nm.189.cn/selfservice/cust/checkNameAndIdentNbr";
            /**
             *  {
             *      "custName":"userName",
             *      "identNbr":"idCard",
             *      "IDCardType":"1"
             *  }
             */
            Map<String, Object> params = new HashMap<>();
            params.put("custName", NeiMengGu10000ForWeb.unicode(param.getRealName()));
            params.put("identNbr", param.getIdCard());
            params.put("IDCardType", "1");

            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (!StringUtils.contains(pageContent, "flag\":\"1\"")) {
                logger.error("详单-->短信验证码-->刷新失败,身份校验失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
            referer = "http://nm.189.cn/selfservice/bill/wdcp";
            templateUrl = "http://nm.189.cn/selfservice/bill/xdQuerySMS";
            /**
             * {"phone":"phone"}
             */
            params = new HashMap<>();
            params.put("phone", param.getMobile().toString());

            data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "flag\":\"0\"")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                /**
                 * "flag":"2"  一小时超过5次
                 */
                logger.error("详单-->短信验证码-->刷新失败,param={},pageContent={}", param, pageContent);
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
            String referer = "http://nm.189.cn/selfservice/bill/wdcp";
            String templateUrl = "http://nm.189.cn/selfservice/bill/xdQuerySMSCheck";
            /**
             *  {"code":"smsCode"}
             */
            Map<String, Object> params = new HashMap<>();
            params.put("code", param.getSmsCode());

            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "flag\":\"1\"")) {
                referer = "http://nm.189.cn/selfservice/bill/xd";
                templateUrl = "http://nm.189.cn/selfservice/bill/xdQuerySMSCheckIf";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setRequestBody("{}", ContentType.APPLICATION_JSON).setReferer(referer).invoke();
                pageContent = response.getPageContent();
                if (StringUtils.contains(pageContent, "flag\":\"1\"")) {
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                } else {
                    logger.error("详单-->校验失败,还需要进行短信校验,param={},pateContent={}", param, pageContent);
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
                }
            } else {
                logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
