package com.datatrees.rawdatacentral.plugin.operator.guang_dong_10000_web;

import java.net.URLEncoder;
import java.nio.charset.Charset;
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
import com.datatrees.spider.operator.domain.model.FormType;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/18.
 */
public class GuangDong10000ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(GuangDong10000ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://gd.189.cn/common/login.jsp?UATicket=-1&loginOldUri=";
            response = TaskHttpClient.create(param, RequestType.GET, "").setFullUrl(templateUrl).invoke();
            logger.info("广东电信初始化成功！{}", param.getTaskId());
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
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshPicCodeForBillDetail(param);
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
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return validatePicCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://gd.189.cn/nCheckCode?kkadf={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "guang_dong_10000_web_001")
                    .setFullUrl(templateUrl, Math.random()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer =
                    "https://gd.189.cn/common/newLogin/newLogin.htm?SSOArea=0000&SSOAccount=null&SSOProType=null&SSORetryTimes=null&SSOError=null&SSOCustType=0&loginOldUri=&SSOOldAccount=null" +
                            "&SSOProTypePre=null";
            String templateUrl = "https://gd.189.cn/dwr/exec/newLoginDwr.goLogin.dwr";
            String templateData = "callCount=1&c0-scriptName=newLoginDwr&c0-methodName=goLogin&c0-id={}&c0-param0=boolean:false&c0-param1=boolean" +
                    ":false&c0-param2=string:{}&c0-param3=string:&c0-param4=string:2000004&c0-param5=string:{}&c0-param6=string:00&c0-param7=string" +
                    ":{}&c0-param8=string:&c0-param9=string:&xml=true";
            String data = TemplateUtils
                    .format(templateData, RandomUtils.nextInt(10000) + "_" + System.currentTimeMillis(), param.getPicCode(), param.getMobile(),
                            param.getPassword());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_002").setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data, ContentType.TEXT_PLAIN).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            String ssoRequestXML = PatternUtils.group(pageContent, "s4=\"([^\\\"]+)\"", 1);
            if (StringUtils.isBlank(ssoRequestXML)) {
                String errorMessage = PatternUtils.group(pageContent, "s2=\"([^\\\"]+)\"", 1);
                if (StringUtils.isEmpty(errorMessage)) {
                    logger.error("登陆失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                } else {
                    logger.error("登陆失败,param={},errorMessage={}", param, errorMessage);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }

            referer = templateUrl;
            templateUrl = "https://uam.gd.ct10000.com/portal/SSOLoginForWT.do";
            templateData = "area=&accountType=2000004&passwordType=00&loginOldUri=%2Fservice%2Fhome%2F&IFdebug=null&errorMsgType=&SSORequestXML" +
                    "={}&sysType=2&from=new&isShowLoginRand=Y&areaSel=020&accountTypeSel=2000004&account={}&mobilePassword=custPassword&password" +
                    "={}&smsCode=&loginCodeRand={}";
            data = TemplateUtils
                    .format(templateData, URLEncoder.encode(ssoRequestXML, "UTF-8"), param.getMobile(), param.getPassword(), param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_003").setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            referer = templateUrl;
            templateUrl = PatternUtils.group(pageContent, "location.replace\\(\"(https://gd.189.cn/receiveUATiecket.do\\?UATicket=[^\"]+)\"", 1);
            if (StringUtils.isBlank(templateUrl)) {
                List<String> errorMessageList = XPathUtil.getXpath("//form[@id='redirectForm']/input[@name='Msg']/@value/text()", pageContent);
                if (CollectionUtils.isEmpty(errorMessageList)) {
                    logger.error("登陆失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                } else {
                    logger.error("登陆失败,param={},errorMessage={}", param, errorMessageList.get(0));
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }
            response = TaskHttpClient.create(param, RequestType.GET, "guang_dong_10000_web_004").setFullUrl(templateUrl).setReferer(referer).invoke();
            if (StringUtils.contains(response.getPageContent(), "/service/home/")) {
                referer = templateUrl;
                templateUrl = "https://gd.189.cn/service/home/";
                data = "loginRedirect=true";
                response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_005").setFullUrl(templateUrl).setReferer(referer)
                        .setRequestBody(data).invoke();
                if (StringUtils.contains(response.getPageContent(Charset.forName("GB2312")), "上次登录")) {
                    logger.info("登陆成功,param={}", param);
                    return result.success();
                } else {
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent(Charset.forName("UTF-8")));
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            } else {
                logger.error("登陆失败,param={},response={}", param, response);
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
            //String id = StringUtils.substring(String.valueOf((int) (Math.random() * 10001) + 10000), 1, 5) + "_" +
            //        String.valueOf(System.currentTimeMillis());
            String areaCode = TaskUtils.getTaskContext(param.getTaskId(), "TelNumAttribution");
            //String phoneType = TaskUtils.getTaskContext(param.getTaskId(), "ServiceType");
            String templateUrl = "https://gd.189.cn/volidate/validateSendMsg.action";
            String templateData = "number=" + param.getMobile() + "&latnId=" + areaCode + "&typeCode=LIST_QRY";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_006").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.containsNone(pageContent, "允许发送短信")) {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
            templateUrl = "https://gd.189.cn/volidate/insertSendSmg.action";
            templateData = "validaterResult=0&resultmsg=%E5%85%81%E8%AE%B8%E5%8F%91%E9%80%81%E7%9F%AD%E4%BF%A1&YXBS=LIST_QRY&number=" +
                    param.getMobile() + "&latnId=" + areaCode;
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_007").setFullUrl(templateUrl).setRequestBody(templateData)
                    .invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "保存信息成功")) {
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
        //HttpResult<Map<String, Object>> result = new HttpResult<>();
        //Response response = null;
        //try {
        //    String id = StringUtils.substring(String.valueOf((int) (Math.random() * 10001) + 10000), 1, 5) + "_" +
        //            String.valueOf(System.currentTimeMillis());
        //    String areaCode = TaskUtils.getTaskContext(param.getTaskId(), "TelNumAttribution");
        //    String phoneType = TaskUtils.getTaskContext(param.getTaskId(), "ServiceType");
        //    String templateUrl = "http://gd.189.cn/dwr/exec/commonAjax.getRandomCodeOper.dwr";
        //    String templateData = "callCount=1&c0-scriptName=commonAjax&c0-methodName=getRandomCodeOper&c0-id={}&c0-param0=boolean:false&c0-param1" +
        //            "=boolean:false&c0-param2=string:{}&c0-param3=string:{}&c0-param4=string:{}&xml=true";
        //    String data = TemplateUtils.format(templateData, id, areaCode, param.getMobile(), phoneType);
        //    String referer = "http://gd.189.cn/service/home/query/xd_index.html";
        //    response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_006").setFullUrl(templateUrl)
        //            .setRequestBody(data, ContentType.TEXT_PLAIN).setReferer(referer).invoke();
        //    if (StringUtils.contains(response.getPageContent(), "DWREngine._handleResponse")) {
        //        logger.info("详单-->短信验证码-->刷新成功,param={}", param);
        //        return result.success();
        //    } else {
        //        logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
        //        return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
        //    }
        //} catch (Exception e) {
        //    logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
        //    return result.failure(ErrorCode.REFESH_SMS_ERROR);
        //}
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            DateTime firstDateTime = new LocalDate().dayOfMonth().withMinimumValue().toDateTimeAtStartOfDay();
            DateTime nowDateTime = new DateTime();
            String firstDay = firstDateTime.toString("yyyyMMdd");
            String nowDay = nowDateTime.toString("yyyyMMdd");
            String nowMonth = nowDateTime.toString("yyyyMM");

            String referer = "http://gd.189.cn/service/home/query/xd_index.html";
            String templateUrl = "http://gd.189.cn/J/J10009.j";
            String templateData = "a.c=0&a.u=user&a.p=pass&a.s=ECSS&c.n=\u7487\ue162\u7176\u5a13\u546d\u5d1f&c.t=02&c.i=02-005-04&d.d01=call&d" +
                    ".d02={}&d.d03={}&d.d04={}&d.d05=1000000&d.d06=1&d.d07={}&d.d08=1";
            String data = TemplateUtils.format(templateData, nowMonth, firstDay, nowDay, param.getSmsCode());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_008").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "\"msg\":\"成功") || StringUtils.contains(pageContent, "\"msg\":\"没有数据")) {
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

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://gd.189.cn/service/home/query/xd_index.html?SESSIONID=1e28383e-8cc8-4234-8a75-1831cc31c7f5";
            String templateUrl = "http://gd.189.cn/code";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "guang_dong_10000_web_009")
                    .setFullUrl(templateUrl).setReferer(referer).invoke();
            logger.info("详单-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Date date = new Date();
            SimpleDateFormat format_1 = new SimpleDateFormat("yyyyMM");
            String currentMonth = format_1.format(date);
            String startDate = currentMonth + "01";
            SimpleDateFormat format_2 = new SimpleDateFormat("yyyyMMdd");
            String endDate = format_2.format(date);

            String referer = "http://gd.189.cn/service/home/query/xd_index.html?SESSIONID=1e28383e-8cc8-4234-8a75-1831cc31c7f5";
            String templateUrl = "http://gd.189.cn/J/J10009.j";
            String templateData = "a.c=0&a.u=user&a.p=pass&a.s=ECSS&c.n=\u93c1\u7248\u5d41\u5a13\u546d\u5d1f&c.t=02&c.i=02-005-02&d.d01=data&d" +
                    ".d02={}&d.d03={}&d.d04={}&d.d05=20&d.d06=1&d.d07={}&d.d08=1";
            String data = TemplateUtils.format(templateData, currentMonth, startDate, endDate, param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10000_web_010").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            if (StringUtils.contains(response.getPageContent(), "验证码不正确")) {
                logger.error("详单-->图片验证码-->校验失败,param={}", param);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            } else {
                logger.info("详单-->图片验证码-->校验成功,param={}", param);
                return result.success();
            }
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }
}
