package com.datatrees.rawdatacentral.plugin.operator.ji_lin_10086_web;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
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
 * 登录页 http://www.jl.10086.cn/service/GroupLogin/popuplogin.jsp
 * Created by guimeichao on 17/11/9.
 */
public class JiLin10086ForWeb implements OperatorPluginService {

    private static Logger logger = LoggerFactory.getLogger(JiLin10086ForWeb.class);

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
            //case FormType.VALIDATE_BILL_DETAIL:
            //    return refeshSmsCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            //case FormType.LOGIN:
            //    return submitForLogin(param);
            //case FormType.VALIDATE_BILL_DETAIL:
            //    return submitForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return null;
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptMobile = invocable.invokeFunction("doRSAEncrypt", param.getMobile().toString()).toString();
            String encryptPassword = invocable.invokeFunction("doRSAEncrypt", param.getPassword()).toString();
            String templateUrl = "http://www.jl.10086.cn/service/operate/action/SendSmsCheckCode_sendSmsCodeForLogin" +
                    ".action?bossPhoneid={}&randomStr={}";
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10086_web_001").setFullUrl(templateUrl, encryptMobile,
                    System.currentTimeMillis()).invoke();
            if (response.getPageContent().contains("短信验证码已下发至您的手机中")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }
}
