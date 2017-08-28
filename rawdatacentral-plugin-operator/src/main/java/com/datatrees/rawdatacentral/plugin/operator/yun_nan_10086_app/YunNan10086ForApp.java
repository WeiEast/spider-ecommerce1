package com.datatrees.rawdatacentral.plugin.operator.yun_nan_10086_app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云南移动App
 * 登录方式：云南移动App
 * 登陆方式:服务密码登陆
 * Created by guimeichao on 17/8/28.
 */
public class YunNan10086ForApp implements OperatorPluginService {

    private static final Logger logger       = LoggerFactory.getLogger(YunNan10086ForApp.class);
    private static final String templateUrl  = "http://www.yn.10086.cn/appsrv/actionDispatcher.do";
    private static final String templateData = "deviceid=D7F40D126FE979D7C24E5FB874DBB84D&appKey=11100&internet=WIFI&sys_version=6.0.1&screen" + "=1080x1920&model=Mi Note 2&imsi=460078065323889&imei=8697{}&md5sign={}&jsonParam={}";

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
        return null;
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String encryptPassword = EncryptUtilsForYunNan10086App.getEncryptString(param.getPassword());

            /**
             * [{"dynamicURI":"/login","dynamicParameter":{"method":"ln","m":"userName","p":"encryPwd","deviceCode":""},"dynamicDataNodeName":"pwdLogin_node"}]
             */

            Map<String, Object> dynamicParameter = new HashMap<>();
            dynamicParameter.put("method", "ln");
            dynamicParameter.put("m", param.getMobile());
            dynamicParameter.put("p", encryptPassword);
            dynamicParameter.put("deviceCode", "");
            Map<String, Object> params = new HashMap<>();
            params.put("dynamicURI", "/login");
            params.put("dynamicParameter", dynamicParameter);
            params.put("dynamicDataNodeName", "pwdLogin_node");

            List<Object> paramsList = new ArrayList<>();
            paramsList.add(params);

            String base64Data = Base64.encodeBase64String(JSON.toJSONString(paramsList).getBytes());

            String data = TemplateUtils.format(templateData, param.getMobile(), EncryptUtilsForYunNan10086App.md5sign(base64Data), base64Data);
            response = TaskHttpClient.create(param, RequestType.POST, "yun_nan_10086_app_001").setFullUrl(templateUrl).setRequestBody(data).invoke();

            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        return null;
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        return null;
    }
}
