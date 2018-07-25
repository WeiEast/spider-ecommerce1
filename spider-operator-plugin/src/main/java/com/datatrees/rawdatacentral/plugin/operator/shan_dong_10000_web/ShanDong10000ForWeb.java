package com.datatrees.rawdatacentral.plugin.operator.shan_dong_10000_web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/21.
 */
public class ShanDong10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(ShanDong10000ForWeb.class);

    private              LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCode(param);
            case FormType.VALIDATE_USER_INFO:
                return refeshPicCodeForUserInfo(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshPicCodeForBillDetail(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_USER_INFO:
                return refeshSmsCodeForUserInfo(param);
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
            case FormType.VALIDATE_USER_INFO:
                return submitForUserInfo(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_USER_INFO:
                return validatePicCodeForUserInfo(param);
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

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            if (StringUtils.isBlank(param.getRealName()) || StringUtils.isBlank(param.getIdCard())) {
                logger.error("登陆失败,信息不完整,姓名或身份证缺失,param={}", param);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }

            String referer = "http://www.189.cn/sd/";
            String templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10016&toStUrl=http://sd.189.cn/selfservice/account/returnAuth?columnId=0201";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();

            String returnStr = "";
            Matcher matcher = PatternUtils.matcher("returnStr:\"([^}\"]+)\"", pageContent);
            if (matcher.find()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    System.out.println("group" + i + " " + matcher.group(i));
                    returnStr = matcher.group(i);
                }
            } else {
                System.out.println("not match");
            }
            referer = "http://sd.189.cn/selfservice/account/returnAuth?columnId=0201&returnStr={}";
            templateUrl = "http://sd.189.cn/selfservice/service/loginAuthNew";

            Map<String, Object> params = new HashMap<>();
            /**
             * {
             *    "returnStr": returnStr
             * }
             */
            params.put("returnStr", returnStr);
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer, returnStr).invoke();

            referer = "http://sd.189.cn/selfservice/bill?tag=queryBalance";
            templateUrl = "http://sd.189.cn/selfservice/cust/checkIsLogin";
            /**
             * {}
             */
            params = new HashMap<>();
            data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            pageContent = response.getPageContent();
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

    private HttpResult<String> refeshPicCodeForUserInfo(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://sd.189.cn/selfservice/cust/manage";
            String templateUrl = "http://sd.189.cn/selfservice/cust/querymanage?100";
            /**
             * {}
             */
            Map<String, Object> params = new HashMap<>();
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();

            templateUrl = "http://sd.189.cn/selfservice/service/toBusiVa?v=83&r=6";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            referer = templateUrl;
            templateUrl = "http://sd.189.cn/selfservice/validatecode/codeimg.jpg";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            logger.info("个人信息-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("个人信息-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForUserInfo(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            logger.info("个人信息-->图片验证码-->校验成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("个人信息-->图片验证码-->校验失败,param={}", param, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForUserInfo(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://sd.189.cn/selfservice/service/toBusiVa?v=83&r=1";
            String templateUrl = "http://sd.189.cn/selfservice/service/sendSms";
            /**
             * {
             *      "orgInfo":"mobile",
             *      "valicode":"imageCode",
             *      "smsFlag":"real_2busi_validate"
             * }
             */
            Map<String, Object> params = new HashMap<>();
            params.put("orgInfo", param.getMobile().toString());
            params.put("valicode", param.getPicCode());
            params.put("smsFlag", "real_2busi_validate");
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            if (StringUtils.equals(response.getPageContent().trim(), "0")) {
                logger.info("个人信息-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("个人信息-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_ERROR);
            }
        } catch (Exception e) {
            logger.error("个人信息-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForUserInfo(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://sd.189.cn/selfservice/service/toBusiVa?v=83&r=6";
            String templateUrl = "http://sd.189.cn/selfservice/service/busiVa";
            /**
             * {
             *      "username_2busi":"undefined",
             *      "credentials_no_2busi":"undefined",
             *      "validatecode_2busi":"imageNo",
             *      "randomcode_2busi":"smsCode",
             *      "randomcode_flag":"0"
             * }
             */
            Map<String, Object> params = new HashMap<>();
            params.put("username_2busi", "undefined");
            params.put("credentials_no_2busi", "undefined");
            params.put("validatecode_2busi", param.getPicCode());
            params.put("randomcode_2busi", param.getSmsCode());
            params.put("randomcode_flag", "0");
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "retnCode\":0")) {
                logger.info("个人信息-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.warn("个人信息-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("个人信息-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String areaCode = TaskUtils.getTaskContext(param.getTaskId(), "areaCode");
            String referer = "http://sd.189.cn/selfservice/bill/";
            String templateUrl = "http://sd.189.cn/selfservice/bill/serverQuery";
            /**
             * {
             *      "accNbr":"mobile",
             *      "areaCode":"areaCode",
             *      "accNbrType":"4"
             * }
             */
            Map<String, Object> params = new HashMap<>();
            params.put("accNbr", param.getMobile().toString());
            params.put("areaCode", areaCode);
            params.put("accNbrType", "4");
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            String billMonth = sf.format(c.getTime());

            referer = "http://sd.189.cn/selfservice/bill/";
            templateUrl = "http://sd.189.cn/selfservice/bill/queryBillDetailNum";
            /**
             * {
             *      "accNbr":"mobile",
             *      "billingCycle":"billMonth",
             *      "ticketType":"0"
             * }
             */
            params = new HashMap<>();
            params.put("accNbr", param.getMobile().toString());
            params.put("billingCycle", billMonth);
            params.put("ticketType", "0");
            data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();

            templateUrl = "http://sd.189.cn/selfservice/service/toBusiVa?v=83&r=1";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            referer = templateUrl;
            templateUrl = "http://sd.189.cn/selfservice/validatecode/codeimg.jpg";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            logger.info("详单-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            logger.info("详单-->图片验证码-->校验成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,param={}", param, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://sd.189.cn/selfservice/service/toBusiVa?v=83&r=1";
            String templateUrl = "http://sd.189.cn/selfservice/service/sendSms";
            /**
             * {
             *      "orgInfo":"mobile",
             *      "valicode":"imageCode",
             *      "smsFlag":"real_2busi_validate"
             * }
             */
            Map<String, Object> params = new HashMap<>();
            params.put("orgInfo", param.getMobile().toString());
            params.put("valicode", param.getPicCode());
            params.put("smsFlag", "real_2busi_validate");
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            if (StringUtils.equals(response.getPageContent().trim(), "0")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_ERROR);
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
            String typeNumber = "1";
            String referer = "http://sd.189.cn/selfservice/service/toBusiVa?v=83&r=1";
            String templateUrl = "http://sd.189.cn/selfservice/service/realnVali";
            /**
             * {
             *      "username_2busi":"URLEncoder.encode(name, "UTF-8")",
             *      "credentials_type_2busi":"typeNumber",
             *      "credentials_no_2busi":"idCard",
             *      "validatecode_2busi":"imageNo",
             *      "randomcode_2busi":"smsCode",
             *      "randomcode_flag":"0",
             *      "rid":1,
             *      "fid":"bill_monthlyDetail"
             * }
             */
            Map<String, Object> params = new HashMap<>();
            params.put("username_2busi", URLEncoder.encode(param.getRealName(), "UTF-8"));
            params.put("credentials_type_2busi", typeNumber);
            params.put("credentials_no_2busi", param.getIdCard());
            params.put("validatecode_2busi", param.getPicCode());
            params.put("randomcode_2busi", param.getSmsCode());
            params.put("randomcode_flag", "0");
            params.put("rid", 1);
            params.put("fid", "bill_monthlyDetail");
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_JSON).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "retnCode\":0")) {
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
}
