package com.datatrees.rawdatacentral.plugin.operator.chong_qing_10086_web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/14.
 */
public class ChongQing10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(ChongQing10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://service.cq.10086.cn/httpsFiles/pageLogin.html";
            response = TaskHttpClient.create(param, RequestType.GET, "chong_qing_10086_web_001").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
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

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
            String referer = "https://service.cq.10086.cn/httpsFiles/pageLogin.html";
            String templateUrl = "https://service.cq.10086.cn/servlet/ImageServlet?random={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chong_qing_10086_web_002")
                    .setFullUrl(templateUrl, db.setScale(15, BigDecimal.ROUND_HALF_UP)).setReferer(referer).invoke();
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
            BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
            String random = db.setScale(17, BigDecimal.ROUND_HALF_UP).toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String date = format.format(new Date());

            String referer = "https://service.cq.10086.cn/httpsFiles/pageLogin.html";
            String templateUrl = "https://service" +
                    ".cq.10086.cn/ics?service=ajaxDirect/1/login/login/javascript/&pagename=login&eventname=interfaceSendSMS&cond_SERIAL_NUMBER" +
                    "={}&ajaxSubmitType=post&ajax_randomcode={}";
            response = TaskHttpClient.create(param, RequestType.POST, "chong_qing_10086_web_003")
                    .setFullUrl(templateUrl, param.getMobile(), date, random.substring(1, 19)).setReferer(referer).invoke();
            if (!StringUtils.contains(response.getPageContent(), "\"FLAG\":\"false\"")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
            String random = db.setScale(17, BigDecimal.ROUND_HALF_UP).toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String date = format.format(new Date());

            String referer = "https://service.cq.10086.cn/httpsFiles/pageLogin.html";
            String templateUrl =
                    "https://service.cq.10086.cn/ics?service=ajaxDirect/1/login/login/javascript/&pagename=login&eventname=interfaceLogin&cond_REMEMBER_TAG" +
                            "=true&cond_LOGIN_TYPE=0&cond_SERIAL_NUMBER={}&cond_USER_PASSWD=&cond_USER_PASSSMS={}&cond_VALIDATE_CODE={}&ajaxSubmitType=post" +
                            "&ajax_randomcode={}";
            response = TaskHttpClient.create(param, RequestType.POST, "chong_qing_10086_web_004")
                    .setFullUrl(templateUrl, param.getMobile(), param.getSmsCode(), param.getPicCode(), date, random.substring(1, 19))
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            String resultMsg = "";
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            } else {
                List<String> list = XPathUtil.getXpath("//DATASETDATA/text()", pageContent);
                if (list != null && list.size() > 0) {
                    Matcher matcher = PatternUtils.matcher("<!\\[CDATA\\[\\[(.*)\\]\\]\\]>", list.get(0));
                    if (matcher.find()) {
                        for (int i = 0; i <= matcher.groupCount(); i++) {
                            resultMsg = matcher.group(i);
                        }
                    }
                } else {
                    logger.error("登陆失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }
            resultMsg = JsonPathUtil.readAsString(resultMsg, "$.RESULTINFO");

            // get next url params
            //if (!StringUtils.contains(resultMsg, "url")) {
            //    logger.error("登陆失败,param={},response={}", param, response);
            //    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            //}
            //templateUrl = JsonPathUtil.readAsList(resultMsg, "$.url").get(0);
            //if (StringUtils.isBlank(templateUrl)) {
            //    logger.error("登陆失败,param={},response={}", param, response);
            //    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            //}
            //
            //response = TaskHttpClient.create(param, RequestType.GET, "chong_qing_10086_web_005").setFullUrl(templateUrl).invoke();
            //pageContent = response.getPageContent();

            if (StringUtils.isBlank(resultMsg)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            if (StringUtils.contains(resultMsg, "登陆成功")) {
                logger.info("登陆成功,param={}", param);

                String base64Pwd = Base64.encodeBase64String(param.getPassword().getBytes());
                TaskUtils.addTaskShare(param.getTaskId(), "base64Pwd", base64Pwd);

                return result.success();
            } else {
                logger.error("登陆失败,{},param={},response={}", resultMsg, param, response);
                return result.failure(resultMsg);
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
            String templateUrl
                    = "http://service.cq.10086.cn/ics?service=ajaxDirect/1/secondValidate/secondValidate/javascript/&pagename=secondValidate&eventname=getTwoVerification&GOODSNAME=用户详单&DOWHAT=QUE&ajaxSubmitType=post&ajax_randomcode=";
            response = TaskHttpClient.create(param, RequestType.GET, "chong_qing_10086_web_006").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "\"FLAG\":\"true\"")) {
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
            String referer = "http://service.cq.10086.cn/myMobile/detailBill.html";
            String templateUrl = "http://service.cq.10086.cn/ics?service=ajaxDirect/1/secondValidate/secondValidate/javascript/&pagename" +
                    "=secondValidate&eventname=checkSMSINFO&cond_USER_PASSSMS={}&cond_CHECK_TYPE=DETAIL_BILL&cond_loginType=2&ajaxSubmitType=post&ajax_randomcode=";
            response = TaskHttpClient.create(param, RequestType.GET, "chong_qing_10086_web_007").setFullUrl(templateUrl, param.getSmsCode())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "验证成功")) {
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
}
