package com.datatrees.rawdatacentral.plugin.operator.si_chuan_10086_web;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
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
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return null;
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshSmsCodeForLogin(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
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
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return null;
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.sc.10086.cn/service/actionDispatcher.do";
            String templateData = "reqUrl=SC_VerCode&busiNum=SC_VerCode&key=LOGIN_PWD";
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_001").setFullUrl(templateUrl).setRequestBody(templateData).invoke();
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
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptImgCode = invocable.invokeFunction("encryptByDES", param.getPicCode().toString()).toString();

            String templateUrl = "http://www.sc.10086.cn/service/sms.do";
            String templateData = "busiNum=SCLoginSMS&mobile=" + param.getMobile() + "&smsType=1&passwordType=1&imgVerCode=" + URLEncoder.encode(encryptImgCode, "UTF-8");
            response = TaskHttpClient.create(param, RequestType.POST, "si_chuan_10086_web_002").setFullUrl(templateUrl).setRequestBody(templateData).invoke();
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

    private HttpResult<Map<String,Object>> submitForLogin(OperatorParam param) {


        return null;

    }
}
