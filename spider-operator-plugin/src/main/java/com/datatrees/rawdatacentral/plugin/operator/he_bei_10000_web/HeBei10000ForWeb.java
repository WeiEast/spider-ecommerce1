package com.datatrees.rawdatacentral.plugin.operator.he_bei_10000_web;

import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/25.
 */
public class HeBei10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(HeBei10000ForWeb.class);

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
        switch (param.getFormType()) {
            case "CALL_DETAILS":
                return processForDetails(param, "callDetails");
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
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

            String templateUrl = "http://www.189.cn/login/index.do";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            if (StringUtils.contains(response.getPageContent(), "regUrl\":null")) {
                /**
                 * 不访问会导致无权限获取短信
                 */
                String referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00380407";
                templateUrl = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10006&toStUrl=http://he.189.cn/service/bill/feeQuery_iframe" +
                        ".jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl)
                        .setReferer(referer).invoke();

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
            String areaCode = TaskUtils.getTaskContext(param.getTaskId(), "areaCode");
            String loginType = TaskUtils.getTaskContext(param.getTaskId(), "loginType");
            String operType = TaskUtils.getTaskContext(param.getTaskId(), "operType");
            String randType = TaskUtils.getTaskContext(param.getTaskId(), "randType");

            String referer = "http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he";
            String templateUrl = "http://he.189.cn/public/postValidCode.jsp";
            String templateData = "NUM={}&AREA_CODE={}&LOGIN_TYPE={}&OPER_TYPE={}&RAND_TYPE={}";
            String data = TemplateUtils.format(templateData, param.getMobile(), areaCode, loginType, operType, randType);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "<actionFlag>0</actionFlag>")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
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
            String referer = "http://he.189.cn/service/bill/feeQuery_iframe.jsp?SERV_NO=SHQD1&fastcode=00380407&cityCode=he";
            String[] urls = TaskUtils.getTaskContext(param.getTaskId(), "callUrl").split("\"");
            String templateUrl = urls[0];
            String data = TemplateUtils.format(urls[1], param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (!StringUtils.contains(pageContent, "随机码不正确,请重新输入")) {
                TaskUtils.addTaskShare(param.getTaskId(), "callDetails", pageContent);
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Object> processForDetails(OperatorParam param, String key) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            String pageContent = TaskUtils.getTaskShare(param.getTaskId(), key);
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("通话记录页获取失败,param={}", param, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
}
