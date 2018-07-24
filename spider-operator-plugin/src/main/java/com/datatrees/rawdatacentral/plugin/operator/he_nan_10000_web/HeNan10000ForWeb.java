package com.datatrees.rawdatacentral.plugin.operator.he_nan_10000_web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.rawdatacentral.service.OperatorPluginPostService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 河南电信web端
 * 登录：手机号，服务密码
 * 查询详单：短信验证码
 * User: yand
 * Date: 2017/9/21
 */
public class HeNan10000ForWeb implements OperatorPluginPostService {

    private static final Logger                     logger     = LoggerFactory.getLogger(HeNan10000ForWeb.class);

    private              LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
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

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;

        try {
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }
            String templateUrl = "http://www.189.cn/ha/";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            logger.info("登陆成功,param={}", param);
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
            String referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000354";
            String templateUrl = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            String param_PRODTYPE = PatternUtils.group(pageContent, "doQuery\\('(\\d+)','(\\d+)',''\\)", 2);

            TaskUtils.addTaskShare(param.getTaskId(), "PRODTYPE", param_PRODTYPE);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, 0);

            referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            templateUrl = "http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp?ACC_NBR=" + param.getMobile() + "&PROD_TYPE=" + param_PRODTYPE +
                    "&BEGIN_DATE=&END_DATE=&SERV_NO=&ValueType=1&REFRESH_FLAG=1&FIND_TYPE=1&radioQryType=on&QRY_FLAG=1&ACCT_DATE=" +
                    sf.format(c.getTime()) + "&ACCT_DATE_1=" + sf.format(c.getTime());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.isBlank(response.getPageContent())) {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }

            String param_RAND_TYPE = PatternUtils.group(pageContent, "name=\"RAND_TYPE\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "RAND_TYPE", param_RAND_TYPE);
            String param_BureauCode = PatternUtils.group(pageContent, "name=\"BureauCode\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "BureauCode", param_BureauCode);
            String param_REFRESH_FLAG = PatternUtils.group(pageContent, "name=\"REFRESH_FLAG\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "REFRESH_FLAG", param_REFRESH_FLAG);
            String param_ACCT_DATE = PatternUtils.group(pageContent, "name=\"ACCT_DATE\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "ACCT_DATE", param_ACCT_DATE);
            String param_QRY_FLAG = PatternUtils.group(pageContent, "name=\"QRY_FLAG\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "QRY_FLAG", param_QRY_FLAG);
            String param_ValueType = PatternUtils.group(pageContent, "name=\"ValueType\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "ValueType", param_ValueType);
            String param_OPER_TYPE = PatternUtils.group(pageContent, "name=\"OPER_TYPE\" value=\"(\\w+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "OPER_TYPE", param_OPER_TYPE);
            if (StringUtils.isBlank(param_PRODTYPE) || StringUtils.isBlank(param_RAND_TYPE) || StringUtils.isBlank(param_BureauCode)) {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param,
                        "param_PRODTYPE is null or param_RAND_TYPE is null or param_BureauCode is null");
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
            referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            templateUrl = "http://ha.189.cn/service/bill/getRand.jsp?PRODTYPE=" + param_PRODTYPE + "&RAND_TYPE=" + param_RAND_TYPE + "&BureauCode=" +
                    param_BureauCode + "&ACC_NBR=" + param.getMobile() + "&PROD_TYPE=" + param_PRODTYPE + "&PROD_PWD=&REFRESH_FLAG=" +
                    param_REFRESH_FLAG + "&BEGIN_DATE=&END_DATE=&ACCT_DATE=" + param_ACCT_DATE + "&FIND_TYPE=1&SERV_NO=&QRY_FLAG=" + param_QRY_FLAG +
                    "&ValueType=" + param_ValueType + "&MOBILE_NAME=" + param.getMobile() + "&OPER_TYPE=" + param_OPER_TYPE + "&PASSWORD=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "<flag>0</flag>")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
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

            String param_PRODTYPE = TaskUtils.getTaskShare(param.getTaskId(), "PRODTYPE");
            String param_RAND_TYPE = TaskUtils.getTaskShare(param.getTaskId(), "RAND_TYPE");
            String param_BureauCode = TaskUtils.getTaskShare(param.getTaskId(), "BureauCode");
            String param_REFRESH_FLAG = TaskUtils.getTaskShare(param.getTaskId(), "REFRESH_FLAG");
            String param_ACCT_DATE = TaskUtils.getTaskShare(param.getTaskId(), "ACCT_DATE");
            String param_QRY_FLAG = TaskUtils.getTaskShare(param.getTaskId(), "QRY_FLAG");
            String param_ValueType = TaskUtils.getTaskShare(param.getTaskId(), "ValueType");
            String param_OPER_TYPE = TaskUtils.getTaskShare(param.getTaskId(), "OPER_TYPE");

            String referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            String templateUrl = "http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp?PRODTYPE=" + param_PRODTYPE + "&RAND_TYPE=" +
                    param_RAND_TYPE + "&BureauCode=" + param_BureauCode + "&ACC_NBR=" + param.getMobile() + "&PROD_TYPE=" + param_PRODTYPE +
                    "&PROD_PWD=&REFRESH_FLAG=" + param_REFRESH_FLAG + "&BEGIN_DATE=&END_DATE=&ACCT_DATE=" + param_ACCT_DATE +
                    "&FIND_TYPE=1&SERV_NO=&QRY_FLAG=" + param_QRY_FLAG + "&ValueType=" + param_ValueType + "&MOBILE_NAME=" + param.getMobile() +
                    "&OPER_TYPE=" + param_OPER_TYPE + "&PASSWORD=" + param.getSmsCode();
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer).invoke();
            if (StringUtils.contains(response.getPageContent(), "开始时间")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }

        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }

    }

    @Override
    public HttpResult<Map<String, Object>> loginPost(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;

        try {
            String referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000354";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET).setFullUrl(referer).setReferer(referer).invoke();
            String templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10017&toStUrl=http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-3&fastcode=20000355&cityCode=ha";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer).invoke();

            referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-1&fastcode=20000354&cityCode=ha";
            templateUrl = "http://ha.189.cn/service/iframe/bill/iframe_inzd.jsp";
            String dataTemplate = "ACC_NBR={}&PROD_TYPE=713058010165&ACCTNBR97=";
            String data = TemplateUtils.format(dataTemplate, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).setConnectTimeout(60000).setSocketTimeout(60000).invoke();

            referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-1&fastcode=20000354&cityCode=ha";
            templateUrl = "http://ha.189.cn/service/iframe/bill/iframe_inzd.jsp";
            dataTemplate = "ACC_NBR={}&SERV_NO=&REFRESH_FLAG=1&BillingCycle=201804&operateType=2&operateType=2";
            data = TemplateUtils.format(dataTemplate, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).setConnectTimeout(60000).setSocketTimeout(60000).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, String.valueOf(param.getMobile())) && StringUtils.contains(pageContent, "该客户总费用为")) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
