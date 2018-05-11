package com.datatrees.rawdatacentral.plugin.operator.hei_long_jiang_10086_web;

import javax.script.Invocable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 黑龙江移动--web
 * 登陆地址:http://hl.10086.cn/apps/login/ssoLoginBox.html
 * 登陆方式:服务密码登陆
 * 图片验证码:支持
 * 验证图片验证码:支持
 * Created by guimeichao on 17/8/24.
 */
public class HeiLongJiang10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(HeiLongJiang10086ForWeb.class);

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
            case FormType.VALIDATE_USER_INFO:
                return refeshSmsCodeForBillDetail(param, "000015");
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(param, "000004");
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
                return submitForBillDetail(param, "000015");
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param, "000004");
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
            case "ENCRYPT_PASSWORD":
                return processForEncryptPassword(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://hl.10086.cn/rest/authImg?type=0&rand={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "hei_long_jiang_10086_web_001")
                    .setFullUrl(templateUrl, Math.random()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
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
            String templateUrl = "http://hl.10086.cn/rest/common/vali/valiImage?imgCode={}&_={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "hei_long_jiang_10086_web_002")
                    .setFullUrl(templateUrl, param.getPicCode(), System.currentTimeMillis()).setRequestContentType(ContentType.APPLICATION_JSON)
                    .invoke();
            /**
             * 结果枚举:
             * 正确
             * {"data":null,"retCode":"000000","retMsg":"验证码输入正确","detail_msg":null,"user_msg":null,"prompt_msg":null
             * ,"object":null,"list":null,"uuid":null,"sOperTime":null},
             * 错误
             * {"data":null,"retCode":"000001","retMsg":"对不起，你输入的图片验证码错误，请重新输入！","detail_msg":null,
             * "user_msg":null,"prompt_msg":null,"object":null,"list":null,"uuid":null,"sOperTime":null}
             */
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            switch (retCode) {
                case "000000":
                    logger.info("登录-->图片验证码-->校验成功,param={}", param);
                    return result.success();
                case "000001":
                    logger.error("登录-->图片验证码-->校验失败,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
                default:
                    logger.error("登录-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = validatePicCodeForLogin(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            String templateUrl = "http://hl.10086.cn/rest/login/sso/doUnifyLogin/";
            //{"userName":"{}","passWord":"{}","pwdType":"01","clientIP":"{}"}
            Map<String, Object> params = new HashMap<>();
            params.put("userName", param.getMobile());
            params.put("passWord", getEncryptPwdNew(param));
            params.put("pwdType", "01");
            params.put("clientIP", param.getPicCode());
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param, RequestType.POST, "hei_long_jiang_10086_web_004").setFullUrl(templateUrl).setRequestBody(data)
                    .setRequestContentType(ContentType.APPLICATION_JSON).invoke();
            /**
             * 结果枚举:
             * 登陆成功:{"data":"5c600ce90ad44248aefce5b7fc87160d","retCode":"000000","retMsg":"success","detail_msg":null,"user_msg":null,
             * "prompt_msg":null,"object":null,"list":null,"uuid":null,"sOperTime":null}
             *
             * 图片有误：{"data":null,"retCode":"000001","retMsg":"对不起，你输入的图片验证码错误，请重新输入！","detail_msg":null,"user_msg":null,
             * "prompt_msg":null,"object":null,"list":null,"uuid":null,"sOperTime":null}
             * 密码有误：{"data":null,"retCode":"2036","retMsg":"您的账户名与密码不匹配，请重新输入","detail_msg":null,"user_msg":null,
             * "prompt_msg":null,"object":null,"list":null,"uuid":null,"sOperTime":null}
             */
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            if (StringUtils.equals("000000", retCode)) {
                //获取权限信息
                data = json.getString("data");
                templateUrl = "http://hl.10086.cn/rest/login/unified/callBack/?artifact={}&backUrl=";
                TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10086_web_005").setFullUrl(templateUrl, data).invoke();
                logger.info("登陆成功,param={}", param);
                return result.success();
            }
            switch (retCode) {
                case "2036":
                    logger.warn("登录失败-->账户名与密码不匹配,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                case "000001":
                    logger.warn("登录失败-->图片验证码不正确,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param, String funcCode) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://hl.10086.cn/rest/sms/sendSmsMsg";
            //{"func_code":"000004","sms_type":"2","phone_no":"","sms_params":""}
            Map<String, Object> params = new HashMap<>();
            params.put("func_code", funcCode);
            params.put("sms_type", "2");
            params.put("phone_no", "");
            params.put("sms_params", "");
            String data = JSON.toJSONString(params);
            response = TaskHttpClient.create(param, RequestType.POST, "hei_long_jiang_10086_web_006").setFullUrl(templateUrl).setRequestBody(data)
                    .setRequestContentType(ContentType.APPLICATION_JSON).invoke();
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            switch (retCode) {
                case "000000":
                    logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                default:
                    logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param, String funcCode) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            /**
             * 结果枚举:
             * 随机短信验证码输入正确
             * /"retCode":"000000"
             * 随机短信验证码输入错误
             * "retCode":"100004"
             */
            String templateUrl = "http://hl.10086.cn/rest/sms/checkSmsCode?func_code={}&sms_type=2&phone_no=&sms_code={}&_={}";
            response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10086_web_007")
                    .setFullUrl(templateUrl, funcCode, param.getSmsCode(), System.currentTimeMillis()).invoke();
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            switch (retCode) {
                case "000000":
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                case "100004":
                    logger.warn("详单-->短信验证码错误,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Object> processForEncryptPassword(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            /**
             * 获取加密后密码
             */
            String encryptPassword = getEncryptPwd(param);
            return result.success(encryptPassword);
        } catch (Exception e) {
            logger.error("获取加密密码失败,param={},response={}", param, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    /**
     * 获取加密密码
     * @return 加密之后的密码
     */
    private String getEncryptPwd(OperatorParam param) {
        Response response = null;
        try {
            String key = "abc123";
            String templateUrl = "http://hl.10086.cn/rest/session/getPscToken/?_={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "hei_long_jiang_10086_web_003")
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            JSONObject json = response.getPageContentForJSON();
            if (StringUtils.isNotBlank(json.getString("data"))) {
                key = json.getString("data");
            }

            /**
             * 加载js加密脚本
             */
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPwd = invocable.invokeFunction("encrypt", param.getPassword(), key).toString();
            return encryptPwd;
        } catch (Exception e) {
            logger.error("加密失败,param={},response={}", param, response, e);
            return null;
        }
    }

    /**
     * 获取加密密码 2018.3.9 更新
     * @return 加密之后的密码
     */
    private String getEncryptPwdNew(OperatorParam param) {
        Response response = null;
        try {
            String modulus
                    = "AJUBsfNgY8oBztH06scAJATI5a6OkDSr8DE3Lhv5F+0lyXEB4naYet1az8LV/StH94z0rJXX791YlVgzXusVpEc4nxOqpPu4qsM8ZpQMq/ryvAcLP5YNRIbfoqcuSJhrKWvfAW/qMS/vb0yPxfd7xmjvReO8H2ZK+ufF6yAfPleZ";
            String exponent = "AQAB";
            String templateUrl = "http://hl.10086.cn/rest/rsa/new-key?_={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "hei_long_jiang_10086_web_003")
                    .setFullUrl(templateUrl, System.currentTimeMillis())
                    .addHeader(HttpHeadKey.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()).invoke();
            JSONObject json = response.getPageContentForJSON();
            if (StringUtils.isNotBlank(json.getString("data"))) {
                modulus = (String) JSONPath.eval(json, "$.data.modulus");
                exponent = (String) JSONPath.eval(json, "$.data.exponent");
            }

            /**
             * 加载js加密脚本
             */
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_new.js", "GBK");
            String encryptPwd = invocable.invokeFunction("paramEncrypt", param.getPassword(), modulus, exponent).toString();
            return encryptPwd;
        } catch (Exception e) {
            logger.error("加密失败,param={},response={}", param, response, e);
            return null;
        }
    }
}
