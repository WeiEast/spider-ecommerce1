package com.datatrees.rawdatacentral.plugin.operator.fu_jian_10000_web;

import javax.script.Invocable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.spider.share.service.utils.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.service.utils.ScriptEngineUtil;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 因web版个人信息不足，需从wap版查询 姓名、身份证、入网时间
 * Created by guimeichao on 17/9/19.
 */
public class FuJian10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(FuJian10000ForWeb.class);

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
            case "QUERY_BASEINFO":
                return refeshPicCodeForBaseinfo(param);
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
            case "QUERY_BASEINFO":
                return submitForBaseinfo(param);
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

    private HttpResult<String> refeshPicCodeForBaseinfo(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wapfj.189.cn/pad/service/info/loginSms/toUser_Wap.shtml";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            String rsaModule = "";
            String rsaPublicExponent = "";
            List<String> rsaModuleList = XPathUtil.getXpath("//input[@name='rsaModule']/@value", pageContent);
            if (!CollectionUtils.isEmpty(rsaModuleList)) {
                rsaModule = rsaModuleList.get(0);
            }
            List<String> rsaPublicExponenteList = XPathUtil.getXpath("//input[@name='rsaPublicExponent']/@value", pageContent);
            if (!CollectionUtils.isEmpty(rsaPublicExponenteList)) {
                rsaPublicExponent = rsaPublicExponenteList.get(0);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "rsaModule", rsaModule);
            TaskUtils.addTaskShare(param.getTaskId(), "rsaPublicExponent", rsaPublicExponent);

            templateUrl = "http://wapfj.189.cn/pad/wapimagecode?" + Math.random();
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            logger.info("个人信息-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("个人信息-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBaseinfo(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            /**
             * 通过登录wap版获取 姓名、身份证、入网时间
             */
            String rsaModule = TaskUtils.getTaskShare(param.getTaskId(), "rsaModule");
            String rsaPublicExponent = TaskUtils.getTaskShare(param.getTaskId(), "rsaPublicExponent");

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_wap.js", "GBK");
            String encryptMobile = invocable.invokeFunction("encryptedString", rsaModule, param.getMobile().toString()).toString();
            String encryptPassword = invocable.invokeFunction("encryptedString", rsaModule, param.getPassword()).toString();

            String referer = "http://wapfj.189.cn/pad/loginPad/toLoginPage.shtml?promptMsg=%D1%E9%D6%A4%C2%EB%B4%ED%CE%F3%A3%A1";
            String templateUrl = "http://wapfj.189.cn/pad/loginPad/waploginnew.shtml";
            String templateData = "rsaModule=" + rsaModule + "&rsaPublicExponent=" + rsaPublicExponent + "&returnUrl=&city=&logintag=1&acc_num=" +
                    encryptMobile + "&ver_code=" + param.getPicCode() + "&password=" + encryptPassword + "&ServType=50";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(templateData).invoke();
            referer = "http://wapfj.189.cn/pad/";
            templateUrl = "http://wapfj.189.cn/pad/queryWapInfo.shtml";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            logger.info(pageContent);
            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
                List<String> customerNameList = XPathUtil.getXpath("//div[@class='myinfo_box']/p[1]/span/text()", pageContent);
                if (!CollectionUtils.isEmpty(customerNameList)) {
                    String customerName = customerNameList.get(0);
                    TaskUtils.addTaskShare(param.getTaskId(), "customerName", customerName);
                    logger.info("******************customerName" + customerName);
                }
                List<String> idCardNoList = XPathUtil.getXpath("//div[@class='myinfo_box']/p[6]/span/text()", pageContent);
                if (!CollectionUtils.isEmpty(idCardNoList)) {
                    String idCardNo = idCardNoList.get(0);
                    TaskUtils.addTaskShare(param.getTaskId(), "idCardNo", idCardNo);
                    logger.info("******************idCardNo" + idCardNo);
                }
                List<String> joinDateList = XPathUtil.getXpath("//dl[@class='myinfo_dl']/dd/p[3]/text()", pageContent);
                if (!CollectionUtils.isEmpty(joinDateList)) {
                    String joinDate = PatternUtils.group(joinDateList.get(0), "开通时间([^：]+)", 1);
                    TaskUtils.addTaskShare(param.getTaskId(), "joinDate", joinDate);
                    logger.info("******************joinDate" + joinDate);
                }
            } else {
                logger.error("个人信息-->校验失败,wap版登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
            logger.info("个人信息-->校验成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("个人信息-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            /**
             * 登录web版
             */
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }

            String referer = "http://www.189.cn/fj/";
            String templateUrl = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=01420648";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=01420648";
            templateUrl = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10014&toStUrl=http://fj.189.cn/newcmsweb/commonIframe" +
                    ".jsp?URLPATH=/service/bill/realtime.jsp&fastcode=01420648&cityCode=fj";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            referer = "http://fj.189.cn/service/bill/realtime.jsp";
            templateUrl = "http://fj.189.cn/BillAjaxServlet.do?method=realtime&PRODNO={}&PRODTYPE=50";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, param.getMobile())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, "获取话费成功") || StringUtils.contains(pageContent, "暂不提供数据")) {
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

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String cityCode = TaskUtils.getTaskContext(param.getTaskId(), "cityCode");
            String templateUrl = "http://fj.189.cn/service/bill/detail.jsp";
            String referer = "http://fj.189.cn/service/smdj/checkSmdj.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            String billMonth = sf.format(c.getTime());

            referer = "http://fj.189.cn/service/bill/detail.jsp";
            templateUrl = "http://fj.189.cn/service/bill/tanChu.jsp?PRODNO={}&PRODTYPE=50&CITYCODE={}&MONTH={}&SELTYPE=1";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), cityCode, billMonth).setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.isNotBlank(pageContent)) {
                cityCode = PatternUtils.group(pageContent, "id=\"CITYCODE\" value=\"([^\"]+)\"", 1);
            } else {
                cityCode = "0" + cityCode;
            }

            TaskUtils.addTaskShare(param.getTaskId(), "fullcityCode", cityCode);

            referer = templateUrl;
            templateUrl = "http://fj.189.cn/BUFFALO/buffalo/QueryAllAjax";
            String templateData = "<buffalo-call><method>getCDMASmsCode</method><map><type>java.util" +
                    ".HashMap</type><string>PHONENUM</string><string>{}</string><string>PRODUCTID</string><string>50</string><string>CITYCODE" +
                    "</string><string>{}</string><string>I_ISLIMIT</string><string>1</string><string>QUERYTYPE</string><string>BILL</string></map" +
                    "></buffalo-call>";
            String data = TemplateUtils.format(templateData, param.getMobile(), cityCode);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "短信随机密码已经发到您的手机")) {
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
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            String billMonth = sf.format(c.getTime());

            String fullcityCode = TaskUtils.getTaskShare(param.getTaskId(), "fullcityCode");
            String puridID = "0";
            String emailEmpoent = "10001";
            String emailModule
                    = "863581c5892cdfe8a67b95c7abb47ead8b102e9620994ae95637f637fa22acac173b91015574507362816b30a884632d8562bf20de621d31d745291aaec7ca6f";
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_wap.js", "GBK");
            String encryptPassword = invocable.invokeFunction("encryptedString", emailModule, param.getPassword()).toString();

            String templateUrl = "http://fj.189.cn/service/bill/trans.jsp";
            String templateData = "PRODNO={}&PRODTYPE=50&CITYCODE={}&SELTYPE=1&MONTH={}&PURID={}&email_empoent={}&email_module={}&serPwd50" +
                    "={}&randomPwd={}";
            String data = TemplateUtils
                    .format(templateData, param.getMobile(), fullcityCode, billMonth, puridID, emailEmpoent, emailModule, encryptPassword,
                            param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "暂无您所查询的数据清单") || StringUtils.contains(pageContent, "客户姓名")) {
                String encryptMobile = PatternUtils.group(pageContent, "PRODNO=([^\"=]+)=", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "encryptMobile", encryptMobile);
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
