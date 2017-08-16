package com.datatrees.rawdatacentral.plugin.operator.china_10010_web;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TaskHttpClient;
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

import java.util.Map;

/**
 * 中国联通--全国通用
 * 登陆地址:https://uac.10010.com/portal/mallLogin.jsp
 * 登陆方式:服务密码登陆
 * 图片验证码:支持
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
        HttpResult<Map<String, Object>> result = null;
        Response response = null;
        try {
            String templateUrl = "https://uac.10010.com/portal/Service/MallLogin?callback=jQuery&req_time={}" +
                    "&redirectURL=http%3A%2F%2Fwww.10010.com&userName={}&password={}&verifyCode={}&pwdType=01&productType=01" +
                    "&redirectType=01&rememberMe=1&_={}";
            String referer = "https://uac.10010.com/portal/mallLogin.jsp";
            response = TaskHttpClient.create(param, RequestType.GET, "china_10010_web_002").setReferer(referer)
                    .setFullUrl(templateUrl, System.currentTimeMillis(), param.getMobile(), param.getPassword(), param.getPicCode(), System.currentTimeMillis()).invoke();
            /**
             * 结果枚举:
             * 登陆成功:jQuery({resultCode:"0000",redirectURL:"http://www.10010.com"})
             * 非中国联通手机号:jQuery({resultCode:"7009",redirectURL:"http://www.10010.com",errDesc:"null",msg:'系统忙，请稍后再试。',needvode:"1",errorFrom:"bss"})
             * 手机号或密码不正确:jQuery({resultCode:"7007",redirectURL:"http://www.10010.com",errDesc:"null",msg:'用户名或密码不正确。<a href="https://uac.10010.com/cust/resetpwd/inputName"
             *                  target="_blank" style="color: #36c;cursor: pointer;text-decoration:underline;">忘记密码？</a>',needvode:"1",errorFrom:"bss"})
             */
            /**
             * 获取json字符串
             */
            String jsonResult = PatternUtils.group( response.getPageContent(), "jQuery\\(([^\\)]+)\\)", 1);
            JSONObject json = JSON.parseObject(jsonResult);
            String code = json.getString("resultCode");
            String errorMsg = StringUtils.EMPTY;
            if (jsonResult.contains(",msg:")) {
                errorMsg = json.getString("msg");
            }
            if (StringUtils.equals("0000", code)) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            }
            switch (code) {
                case "7007":
                    logger.warn("登录失败-->账户名与密码不匹配,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                case "7009":
                    logger.warn("登录失败-->手机号码与运营商归属地不符,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PHONE_FAIL);
                default:
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_FAIL);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_FAIL);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://uac.10010.com/portal/Service/CreateImage?t={}";
            response = TaskHttpClient
                    .create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "china_10010_web_001")
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }
}
