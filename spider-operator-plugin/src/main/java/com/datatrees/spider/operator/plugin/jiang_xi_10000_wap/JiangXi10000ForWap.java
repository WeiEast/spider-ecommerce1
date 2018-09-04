package com.datatrees.spider.operator.plugin.jiang_xi_10000_wap;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.plugin.OperatorPlugin;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.http.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiangXi10000ForWap implements OperatorPlugin {

    private static final Logger logger = LoggerFactory.getLogger(JiangXi10000ForWap.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wapjx.189.cn/wap_and/login/login.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent)) {
                return result.success();
            } else {
                logger.error("登录-->初始化失败,param={}", param);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
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
            case FormType.LOGIN:
                return refeshSmsCodeForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForValidateBillDetail(param);
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
                return submitForValidateBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return new HttpResult();
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
            String templateUrl = "http://wapjx.189.cn/wap_and/public/image.jsp?date=" + URLEncoder.encode(new Date().toString(), "UTF-8");
            String referer = "http://wapjx.189.cn/wap_and/login/login.jsp?PHONE_NO=" + param.getMobile();
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wapjx.189.cn/wap_and/login/get_code.jsp?PHONE_NO=" + param.getMobile();
            String referer = "http://wapjx.189.cn/wap_and/login/login.jsp?PHONE_NO=" + param.getMobile() + "&SHOWMSG=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录获取短信返回失败");
                return result.failure(ErrorCode.NOT_EMPTY_ERROR_CODE);
            }
            if (pageContent.contains("验证码已经送到您的手机，请留意！")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            }
            logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent(), e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForValidateBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String scriptSessionId = TaskUtils.getTaskShare(param.getTaskId(), "scriptSessionId");

            String templateUrl = "http://wapjx.189.cn/dwr/call/plaincall/Service.excute.dwr";
            String referer = "http://wapjx.189.cn/o2o/xxcx/pak-used-info/details-ck.jsp";
            String templateData = "callCount=1&page=/o2o/xxcx/pak-used-info/details-ck.jsp&httpSessionId=&scriptSessionId={}" +
                    "&c0-scriptName=Service&c0-methodName=excute&c0-id=0&c0-param0=string:DETAILS_SERVICE&c0-param1=boolean:false" +
                    "&c0-e1=string:SEND_SMS_CODE&c0-param2=Object_Object:{method:reference:c0-e1}&batchId=0";
            String data = TemplateUtils.format(templateData, scriptSessionId);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data, ContentType.TEXT_PLAIN).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "CODE:\"1\"")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);

        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        //请求参数scriptSessionId
        //String paramUrl = "http://wapjx.189.cn/public/v4/common/control/dwr/engine.js";
        String scriptSessionId = "AA0CBE9FB90164F9E0E55CF74FCC9338" + (int) Math.floor(Math.random() * 1000);
        TaskUtils.addTaskShare(param.getTaskId(), "scriptSessionId", scriptSessionId);
        try {
            String templateUrl = "http://wapjx.189.cn/wap_and/login_new/login_result.jsp";
            String templateData = "login_name={}&login_passwd={}&login_type=22&logon_valid={}&isComingOrder=";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getSmsCode(), param.getPicCode());
            String referer = "http://wapjx.189.cn/wap_and/login/login.jsp?PHONE_NO=" + param.getMobile();
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.containsNone(pageContent, "重新载入页面以获取源代码: http://wapjx.189.cn/wap_and/login/login_result.jsp")) {
                logger.error("jx189 login request is error! errormessage: pageContent返回内容不符,pageContent{}", pageContent);
                return result.failure(ErrorCode.LOGIN_FAIL);
            } else {
                templateUrl = "http://wapjx.189.cn/wap_and/index.jsp";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl)
                        .setReferer(referer).invoke();
                pageContent = response.getPageContent();
                if (StringUtils.isBlank(pageContent)) {
                    logger.error("jx189 login request is error! errormessage:pageContent为空");
                    return result.failure(ErrorCode.LOGIN_FAIL);
                } else {
                    templateUrl = "http://wapjx.189.cn/wap_and/login_new/index.jsp";
                    response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl)
                            .setReferer(referer).invoke();
                    pageContent = response.getPageContent();
                    if (StringUtils.contains(pageContent, "退出登录")) {
                        logger.info("登陆成功,param={}", param);
                        return result.success();
                    } else {
                        logger.error("jx189 login check password failed , 登录失败");
                        return result.failure(ErrorCode.LOGIN_FAIL);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForValidateBillDetail(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        CheckUtils.checkNotBlank(param.getRealName(), ErrorCode.TASK_CHECK_EMPTY_DATA);

        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String scriptSessionId = TaskUtils.getTaskShare(param.getTaskId(), "scriptSessionId");
            String templateUrl = "http://wapjx.189.cn/dwr/call/plaincall/Service.excute.dwr";
            String referer = "http://wapjx.189.cn/o2o/xxcx/pak-used-info/details-ck.jsp";

            //templateData中属性 c0-e4 为可选身份验证类型，1：身份证号码，2：服务密码(默认)
            String templateData = "callCount=1&page=/o2o/xxcx/pak-used-info/details-ck.jsp&httpSessionId=&scriptSessionId={}" +
                    "&c0-scriptName=Service&c0-methodName=excute&c0-id=0&c0-param0=string:DETAILS_SERVICE&c0-param1=boolean:false" +
                    "&c0-e1=string:CHK_USER_INFO&c0-e2=string:{}&c0-e3=string:%E9%82%B1%E5%8B%87%E7%94%9F&c0-e4=string:2" +
                    "&c0-e5=string:&c0-e6=string:{}&c0-param2=Object_Object:{method:reference:c0-e1, sms_code:reference:c0-e2, " +
                    "cust_name:reference:c0-e3, chk_type:reference:c0-e4, id_card:reference:c0-e5, user_pwd:reference:c0-e6}&batchId=0";
            String data = TemplateUtils.format(templateData, scriptSessionId, param.getSmsCode(), param.getPassword());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data, ContentType.TEXT_PLAIN).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "CODE:\"0\"")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
