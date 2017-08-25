package com.datatrees.rawdatacentral.plugin.operator.china_10010_web;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中国联通--全国通用
 * 登陆地址:https://uac.10010.com/portal/mallLogin.jsp
 * 登陆方式:服务密码登陆
 * 图片验证码:支持
 * 验证图片验证码:支持
 * <p>
 * Created by guimeichao on 17/8/15.
 */
public class China10010ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(China10010ForWeb.class);

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
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
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

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = validatePicCodeForLogin(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            String templateUrl = "https://uac.10010.com/portal/Service/MallLogin?callback=jQuery&req_time={}&redirectURL=http://www.10010.com&userName={}&password={}&pwdType=01&productType=01&verifyCode={}&uvc={}&redirectType=01&rememberMe=1&_={}";
            String uacverifykey = TaskUtils.getCookieValue(param.getTaskId(), "uacverifykey");
            response = TaskHttpClient.create(param, RequestType.GET, "china_10010_web_003").setFullUrl(templateUrl, System.currentTimeMillis(), param.getMobile(), param.getPassword(), param.getPicCode(), uacverifykey, System.currentTimeMillis()).invoke();
            /**
             * 结果枚举:
             * 登陆成功:jQuery({resultCode:"0000",redirectURL:"http://www.10010.com"})
             * 非中国联通手机号:jQuery({resultCode:"7009",redirectURL:"http://www.10010.com",errDesc:"null",msg:'系统忙，请稍后再试。',needvode:"1",errorFrom:"bss"})
             * 手机号或密码不正确:jQuery({resultCode:"7007",redirectURL:"http://www.10010.com",errDesc:"null",msg:'用户名或密码不正确。<a href="https://uac.10010.com/cust/resetpwd/inputName"
             *                  target="_blank" style="color: #36c;cursor: pointer;text-decoration:underline;">忘记密码？</a>',needvode:"1",errorFrom:"bss"})
             */
            JSONObject json = response.getPageContentForJSON();
            String resultCode = json.getString("resultCode");
            if (StringUtils.equals("0000", resultCode)) {

                /**
                 * 获取关键性cookie，没有的话，会访问不了查询请求
                 * 顺便验证登录是否成功
                 */
                templateUrl = "http://iservice.10010.com/e3/static/check/checklogin/";
                TaskHttpClient.create(param, RequestType.POST, "china_10010_web_004").setFullUrl(templateUrl).setSocketTimeout(30000).setMaxRetry(2).invoke();
                logger.info("登陆成功,param={}", param);
                return result.success();
            }
            switch (resultCode) {
                case "7007":
                    logger.warn("登录失败-->账户名与密码不匹配,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                case "7009":
                    logger.warn("登录失败-->手机号码与运营商归属地不符,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PHONE_FAIL);
                default:
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,response={}", response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
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
        logger.warn("defineProcess fail,params={}", param);
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://uac.10010.com/portal/Service/CreateImage?t={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "china_10010_web_001").setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
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
            String templateUrl = "https://uac.10010.com/portal/Service/CtaIdyChk?callback=jQuery&verifyCode={}&verifyType=1&_={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "china_10010_web_002").setFullUrl(templateUrl, param.getPicCode(), System.currentTimeMillis()).invoke();
            //结果枚举:正确{"resultCode":"true"},错误{"resultCode":"false"}
            JSONObject json = response.getPageContentForJSON();
            Boolean resultCode = json.getBoolean("resultCode");
            if (resultCode) {
                logger.info("登录-->图片验证码-->校验成功,param={}", param);
                return result.success();
            }
            logger.warn("登录-->图片验证码-->校验失败,param={}", param);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }
}
