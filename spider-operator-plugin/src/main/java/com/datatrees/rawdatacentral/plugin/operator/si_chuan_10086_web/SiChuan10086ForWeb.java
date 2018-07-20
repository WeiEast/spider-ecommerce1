package com.datatrees.rawdatacentral.plugin.operator.si_chuan_10086_web;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.spider.operator.domain.model.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: yand
 * Date: 2017/11/9
 */
public class SiChuan10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(SiChuan10086ForWeb.class);

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
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshPicCodeForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshPicCodeForBillDetail(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return validatePicCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
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
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return null;
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            String templateData = "reqUrl=SC_VerCode&busiNum=SC_VerCode&key=LOGIN_PWD";
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_001").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if ("0".equals(resultCode)) {
                String resultObj = json.getString("resultObj");
                logger.info("登录-->图片验证码-->刷新成功,param={}", param);
                return result.success(resultObj);
            }
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, resultCode);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptImgCode = invocable.invokeFunction("encryptByDES", param.getPicCode().toString()).toString();

            String templateUrl = "http://www.sc.10086.cn/service/sms.do";
            String templateData = "busiNum=SCLoginSMS&mobile=" + param.getMobile() + "&smsType=1&passwordType=1&imgVerCode=" +
                    URLEncoder.encode(encryptImgCode, "UTF-8");
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_002").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if ("0".equals(resultCode)) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;

        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassWord = invocable.invokeFunction("encryptByDES", param.getPassword().toString()).toString();
            String encryptSmsCode = invocable.invokeFunction("encryptByDES", param.getSmsCode().toString()).toString();
            // 登录
            String templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            String templateData = "reqUrl=SCLogin&busiNum=SCLogin&operType=0&mobile=" + param.getMobile() + "&password=" +
                    URLEncoder.encode(encryptPassWord, "UTF-8") + "&verifyCode=" + URLEncoder.encode(encryptSmsCode, "UTF-8") +
                    "&loginFormTab=&passwordType=1&url=my%2FSC_MY_INDEX.html";
            String referer = "http://www.sc.10086.cn/service/login.html?url=my/SC_MY_INDEX.html";
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_003").setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if (!"0".equals(resultCode)) {
                logger.warn("登录异常，请稍后重试,param={}", param);
                return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
            }
            //访问主页
            templateUrl = (String) JSONPath.eval(json, "$.resultObj.url");
            referer = "http://www.sc.10086.cn/service/login.html?url=my/SC_MY_INDEX.html";
            response = TaskHttpClient.create(param, RequestType.GET, "si_chuan_10086_web_004").setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent) && StringUtils.contains(pageContent, String.valueOf(param.getMobile()))) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            }
            logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);

        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            String templateData = "reqUrl=SC_VerCode&busiNum=SC_VerCode&key=KET_XDCX_CODE";
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_005").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if ("0".equals(resultCode)) {
                String resultObj = json.getString("resultObj");
                logger.info("详单-->图片验证码-->刷新成功,param={}", param);
                return result.success(resultObj);
            }
            logger.error("详单-->图片验证码-->刷新失败,param={}", param);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            String templateData = "reqUrl=SC_VerCode&busiNum=SC_VerCode&methodQuery=validatVerCode&key=KET_XDCX_CODE&code=" + param.getPicCode();
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_006").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if ("0".equals(resultCode)) {
                logger.info("详单-->图片验证码-->校验成功,param={}", param);
                return result.success();
            }
            logger.error("详单-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            String templateData = "reqUrl=SC_MY_XDCXQuery&mothodQuery=detailBillSMSQuery";
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_007").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();

            templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            templateData = "reqUrl=SC_MY_XDCXQuery&mothodQuery=sendSMSCode&async=false";
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_008").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if ("0".equals(resultCode)) {
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
        HttpResult<Map<String, Object>> result = validatePicCodeForBillDetail(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptSmsWord = invocable.invokeFunction("encryptByDES", param.getSmsCode().toString()).toString();
            String templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            String templateData = "reqUrl=SC_MY_XDCXQuery&mothodQuery=smsverify&smscode=" + URLEncoder.encode(encryptSmsWord, "UTF-8") + "&yzmCode=" +
                    param.getPicCode();
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_009").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if ("0".equals(resultCode)) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            }
            logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
