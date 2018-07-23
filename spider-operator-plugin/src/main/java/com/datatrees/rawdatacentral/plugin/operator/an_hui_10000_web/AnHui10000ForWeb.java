package com.datatrees.rawdatacentral.plugin.operator.an_hui_10000_web;

import javax.script.Invocable;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安徽电信web端
 * 登录 手机号 服务密码 图片验证码
 * 查询详单 短信验证码
 * User: yand
 * Date: 2017/9/25
 */
public class AnHui10000ForWeb implements OperatorPluginService {

    private static Logger logger = LoggerFactory.getLogger(AnHui10000ForWeb.class);

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
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshPicCodeForLogin(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        switch (param.getFormType()) {
            case "EXPORT_PDF":
                return processForPdf(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
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

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://ah.189.cn/sso/VImage.servlet?random=" + System.currentTimeMillis();
            String referer = "http://ah.189.cn/sso/login?returnUrl=%2Fservice%2Faccount%2Finit.action";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET, "an_hui_10000_web_001").setFullUrl(templateUrl).setReferer(referer).invoke();
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
            String templateUrl = "http://ah.189.cn/sso/ValidateRandom?validCode=" + param.getPicCode();
            String referer = "http://ah.189.cn/sso/login?returnUrl=%2Fservice%2Faccount%2Finit.action";
            response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10000_web_002").setFullUrl(templateUrl).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "true")) {
                logger.info("登录-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = validatePicCodeForLogin(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("encryptedString", param.getPassword()).toString();

            String templateUrl = "http://ah.189.cn/sso/LoginServlet";
            String referer = "http://ah.189.cn/sso/login?returnUrl=%2Fservice%2Faccount%2Finit.action";
            String templateData =
                    "ssoAuth=0&returnUrl=%2Fservice%2Faccount%2Finit.action&sysId=1003&loginType=4&accountType=9&latnId=551&loginName={}" +
                            "&passType=0&passWord={}&validCode={}&csrftoken=";
            String data = TemplateUtils.format(templateData, param.getMobile(), encryptPassword, param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10000_web_003").setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            String errorMsg = PatternUtils.group(pageContent, "var returnmsg = \"([^。]+。)", 1);
            if (StringUtils.isNotBlank(errorMsg)) {
                logger.error("登陆失败,{},param={},response={}", errorMsg, param, response);
                return result.failure(errorMsg);
            }
            String uuid = invocable.invokeFunction("encryptedString", "serviceNbr=" + param.getMobile()).toString();
            if (StringUtils.isBlank(uuid)) {
                logger.error("获取uuid失败");
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "uuid", uuid);
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
        String mobile = String.valueOf(param.getMobile());
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String data = invocable.invokeFunction("encryptedString",
                    "mobileNum=" + param.getMobile() + "&key=" + mobile.substring(1, 2) + mobile.substring(3, 4) + mobile.substring(6, 7) +
                            mobile.substring(8, 10)).toString();
            String referer = "http://ah.189.cn/service/bill/fee.action?type=ticket";
            String templateUrl = "http://ah.189.cn/service/bill/sendValidReq.action?_v=" + data;
            response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10000_web_006").setFullUrl(templateUrl).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "发送成功")) {
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
            String templateUrl = "http://ah.189.cn/service/bill/phoneAndInternetDetail.action?rnd=" + Math.random();
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET, "an_hui_10000_web_007").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            String macCode = PatternUtils.group(pageContent, "id=\"macCode\"\\s*value=\"([^ ]+)\"", 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String data = "currentPage=&pageSize=10&effDate=" + format.format(new Date()) + "&expDate=" + format.format(new Date()) + "&serviceNbr=" +
                    param.getMobile() + "&operListID=2&isPrepay=0&pOffrType=481&random=" + param.getSmsCode() + "&macCode=" + macCode;
            logger.info("###########" + data);
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            templateUrl = "http://ah.189.cn/service/bill/feeDetailrecordList.action?";
            String body = "_v=" + invocable.invokeFunction("encryptedString", data).toString();
            String referer = "http://ah.189.cn/service/bill/fee.action?type=phoneAndInternetDetail";
            response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10000_web_008").setFullUrl(templateUrl).setRequestBody(body)
                    .setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "没有符合条件的记录") || StringUtils.contains(pageContent, "导出查询结果")) {
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

    private HttpResult<Object> processForPdf(OperatorParam param) {
        if (logger.isDebugEnabled()) {
            logger.debug("start run exportPdf plugin!");
        }
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String[] params = paramMap.get("page_content").split("\\\"");
        Response response = null;
        try {
            response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10000_web_009").setFullUrl(params[0]).setRequestBody(params[1])
                    .invoke();
            if (StringUtils.contains(params[1], "operListId=6")) {
                response = TaskHttpClient.create(param, RequestType.POST, "an_hui_10000_web_009").setFullUrl(params[0]).setRequestBody(params[1])
                        .invoke();
            }
            byte[] bytes = response.getResponse();
            String htmlContent = StringUtils.EMPTY;
            if (StringUtils.isNotBlank(new String(bytes))) {
                htmlContent = PdfUtils.pdfToHtml(new ByteArrayInputStream(bytes));
            } else {
                htmlContent = "no data";
            }
            //resultMap.put(PluginConstants.FIELD, htmlContent);
            //if (logger.isDebugEnabled()){
            //    logger.debug("exportPdf plugin completed! resultMap:" + resultMap);
            //    return result.success(GsonUtils.toJson(resultMap));
            //}else{
            //    logger.error("详单打印失败,param={},response={}", param, htmlContent);
            //    return result.failure(ErrorCode.UNKNOWN_REASON);
            //}
            logger.info("exportPdf plugin completed! ");
            return result.success(htmlContent);
        } catch (Exception e) {
            logger.error("详单打印失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }

    }

}
