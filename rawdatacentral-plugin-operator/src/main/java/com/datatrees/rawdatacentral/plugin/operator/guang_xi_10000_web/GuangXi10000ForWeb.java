package com.datatrees.rawdatacentral.plugin.operator.guang_xi_10000_web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
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
import org.springframework.util.CollectionUtils;

/**
 * Created by guimeichao on 17/9/21.
 */
public class GuangXi10000ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(GuangXi10000ForWeb.class);

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
        return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_USER_INFO:
                return refeshSmsCodeForUserInfo(param);
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
            case FormType.VALIDATE_USER_INFO:
                return submitForUserInfo(param);
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
            if (StringUtils.isBlank(param.getRealName()) || StringUtils.isBlank(param.getIdCard())) {
                logger.error("登陆失败,信息不完整,姓名或身份证缺失,param={}", param);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            String referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
            String templateUrl = "http://gx.189.cn/public/login.jsp";
            String templateData = "LOGIN_TYPE=21&RAND_TYPE=001&AREA_CODE=&logon_name={}&password_type_ra=1&logon_passwd={}&logon_valid=%E8%AF%B7%E8" +
                    "%BE%93%E5%85%A5%E9%AA%8C%E8%AF%81%E7%A0%81";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getPassword());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_001").setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, "<rs_up_num></rs_up_num>")) {
                String userNo = PatternUtils.group(pageContent, "<user_no>([^<]+)</user_no>", 1);
                if (StringUtils.isBlank(userNo)) {
                    logger.error("登陆失败,userNo为空,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
                templateUrl = "http://gx.189.cn/public/user_protocol.jsp";
                templateData = "Logon_Name={}&USER_FLAG=001&USE_PROTOCOL=&LOGIN_TYPE=21&USER_NO={}&ESFlag=8&REDIRECT_URL=%2F2015%2F";
                data = TemplateUtils.format(templateData, param.getMobile(), userNo);
                response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_002").setFullUrl(templateUrl).setReferer(referer)
                        .setRequestBody(data).invoke();

                referer = templateUrl;
                templateUrl = "http://gx.189.cn/public/protocollogin.jsp";
                templateData = "OPEN_TYPE=1&LOGIN_TYPE=21&USER_NO={}&CUSTBRAND=&ESFlag=8&REDIRECT_URL=%2F2015%2F";
                data = TemplateUtils.format(templateData, userNo);
                response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_003").setFullUrl(templateUrl).setReferer(referer)
                        .setRequestBody(data).invoke();

                referer = templateUrl;
                templateUrl = "http://gx.189.cn/service/account/";
                data = "CUSTBRAND=&ESFlag=8&REDIRECT_URL=%2F2015%2F";
                response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_004").setFullUrl(templateUrl).setReferer(referer)
                        .setRequestBody(data).invoke();
                if (StringUtils.contains(response.getPageContent(), param.getMobile().toString())) {
                    logger.info("登陆成功,param={}", param);
                    return result.success();
                } else {
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            } else {
                if (StringUtils.contains(pageContent, "<flag>0</flag>") && StringUtils.contains(pageContent, param.getMobile().toString())) {
                    logger.info("登陆成功,param={}", param);
                    return result.success();
                } else {
                    String msg = StringUtils.EMPTY;
                    List<String> msgList = XPathUtil.getXpath("//root/msg/text()", pageContent);
                    if (!CollectionUtils.isEmpty(msgList)) {
                        msg = msgList.get(0);
                    }
                    logger.error("登陆失败,param={},msg={}", param, msg);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForUserInfo(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
            String templateUrl = "http://gx.189.cn/service/bill/getRand.jsp";
            String templateData = "MOBILE_NAME={}&RAND_TYPE=025&OPER_TYPE=CR1&PRODTYPE=2020966";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_005").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "<flag>0</flag>")) {
                logger.info("个人信息-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("个人信息-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("个人信息-->短信验证码-->刷新失败,param={},response={}", param, e, response);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForUserInfo(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
            String templateUrl = "http://gx.189.cn/public/realname/checkRealName.jsp";
            String templateData = "NUM={}&V_PASSWORD={}&RAND_TYPE=025";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getSmsCode());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_006").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "对不起,您输入的名字或证件号码不正确，请重新输入")) {
                logger.info("个人信息-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.warn("个人信息-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("个人信息-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
            String templateUrl = "http://gx.189.cn/chaxun/iframe/qdcx.jsp";
            String templateData = "ACC_NBR={}&PROD_TYPE=2020966";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_007").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            String bureauCode = "2400";
            if (StringUtils.isNotBlank(bureauCode)) {
                bureauCode = PatternUtils.group(pageContent, "BureauCode\" name=\"BureauCode\" value=\"(\\d+)\"", 1);
            }

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
            String currentMonth = format.format(date);
            referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
            templateUrl = "http://gx.189.cn/service/bill/getRand.jsp";
            templateData = "PRODTYPE=2020966&RAND_TYPE=002&BureauCode={}&ACC_NBR={}&PROD_TYPE=2020966&PROD_PWD=&REFRESH_FLAG=1&BEGIN_DATE=&END_DATE" +
                    "=&SERV_NO=&QRY_FLAG=1&MOBILE_NAME={}&OPER_TYPE=CR1&FIND_TYPE=1031&radioQryType=on&ACCT_DATE={}&ACCT_DATE_1={}&PASSWORD" +
                    "=&CUST_NAME=&CARD_TYPE=1&CARD_NO=";
            data = TemplateUtils.format(templateData, bureauCode, param.getMobile(), param.getMobile(), currentMonth, currentMonth);
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_008").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "<flag>0</flag>")) {
                TaskUtils.addTaskShare(param.getTaskId(), "bureauCode", bureauCode);

                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, e, response);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
            String templateUrl = "http://gx.189.cn/public/realname/checkRealName.jsp";
            String templateData = "NUM={}&V_PASSWORD={}&CUST_NAME={}&CARD_NO={}&CARD_TYPE=1&RAND_TYPE=002";
            String data = TemplateUtils
                    .format(templateData, param.getMobile(), param.getSmsCode(), URLEncoder.encode(param.getRealName(), "UTF-8"), param.getIdCard());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_009").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "<Tips>")) {
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
                String currentMonth = format.format(date);
                String bureauCode = TaskUtils.getTaskShare(param.getTaskId(), "bureauCode");

                referer = "http://gx.189.cn/chaxun/iframe/user_center.jsp";
                templateUrl = "http://gx.189.cn/chaxun/iframe/inxxall_new.jsp";
                templateData = "PRODTYPE=2020966&RAND_TYPE=002&BureauCode={}&ACC_NBR={}&PROD_TYPE=2020966&PROD_PWD=&REFRESH_FLAG=1&BEGIN_DATE" +
                        "=&END_DATE=&SERV_NO=&QRY_FLAG=1&MOBILE_NAME={}&OPER_TYPE=CR1&FIND_TYPE=1031&radioQryType=on&ACCT_DATE={}&ACCT_DATE_1" +
                        "={}&PASSWORD={}&CUST_NAME={}&CARD_TYPE=1&CARD_NO={}";
                data = TemplateUtils
                        .format(templateData, bureauCode, param.getMobile(), param.getMobile(), currentMonth, currentMonth, param.getSmsCode(),
                                URLEncoder.encode(param.getRealName(), "UTF-8"), param.getIdCard());
                response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10000_web_010").setFullUrl(templateUrl).setRequestBody(data)
                        .setReferer(referer).invoke();
                pageContent = response.getPageContent();
                if (StringUtils.contains(pageContent, param.getMobile().toString())) {
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                } else {
                    logger.warn("详单-->短信验证码错误,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                }
            } else {
                logger.warn("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}