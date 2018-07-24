package com.datatrees.rawdatacentral.plugin.operator.yun_nan_10000_web;

import java.net.URLEncoder;
import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YunNan10000ForWeb implements OperatorPluginService {

    private Logger                     logger     = LoggerFactory.getLogger(YunNan10000ForWeb.class);

    private LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

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
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
            String refer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=01941227";
            String templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10025&toStUrl=http://yn.189.cn/service/jt/bill/qry_mainjt.jsp?SERV_NO=9A001&fastcode=01941226&cityCode=yn";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(refer).invoke();
            logger.info("登录成功,params={}", param);
            return result.success();
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
            String referer = "http://yn.189.cn/service/jt/bill/qry_mainjt.jsp?SERV_NO=SHQD1&fastcode=01941229&cityCode=yn";
            String templateUrl = "http://yn.189.cn/public/postValidCode.jsp";
            String templateDate = "NUM={}&AREA_CODE={}&LOGIN_TYPE=21&OPER_TYPE=CR0&RAND_TYPE=004";
            String data = TemplateUtils.format(templateDate, param.getMobile(), areaCode);
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
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
            String proNo = TaskUtils.getTaskContext(param.getTaskId(), "proDid");
            String areaCode = TaskUtils.getTaskContext(param.getTaskId(), "areaCode");
            String name = URLEncoder.encode(param.getRealName(), "utf-8");
            String templateUrl = "http://yn.189.cn/public/custValid.jsp";
            String templateData = "_FUNC_ID_=WB_PAGE_PRODPASSWDQRY&NAME={}&CUSTCARDNO={}&PROD_PASS={}&MOBILE_CODE={}&NAME={}&CUSTCARDNO={}";
            String data = TemplateUtils
                    .format(templateData, name, param.getIdCard(), param.getPassword(), param.getSmsCode(), name, param.getIdCard());
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            if (!StringUtils.contains(pageContent, "<rsFlag>1</rsFlag>")) {
                logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.VALIDATE_ERROR);
            }
            templateUrl = "http://yn.189.cn/public/pwValid.jsp";
            templateData
                    = "_FUNC_ID_=WB_PAGE_PRODPASSWDQRY&NAME={}&CUSTCARDNO={}&PROD_PASS={}&MOBILE_CODE={}&ACC_NBR={}&AREA_CODE={}&LOGIN_TYPE=21&PASSWORD={}&MOBILE_FLAG=1&MOBILE_LOGON_NAME={}&MOBILE_CODE={}&PROD_NO={}";
            data = TemplateUtils.format(templateData, name, param.getIdCard(), param.getPassword(), param.getSmsCode(), param.getMobile(), areaCode,
                    param.getPassword(), param.getMobile(), param.getSmsCode(), proNo);
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "<rsFlag>2</rsFlag>")) {
                logger.info("详单-->校验成功,param={}", param);
                templateUrl = "http://yn.189.cn/service/jt/bill/actionjt/ifr_bill_detailslist_new.jsp";
                templateData = "NUM={}&AREA_CODE={}&PROD_NO={}";
                data = TemplateUtils.format(templateData, param.getMobile(), areaCode, proNo);
                response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                        .invoke();
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.VALIDATE_ERROR);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
