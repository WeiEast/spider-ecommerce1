package com.datatrees.rawdatacentral.plugin.operator.yun_nan_10000_wap;

import javax.script.Invocable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云南电信--wap版
 * 登陆地址:http://wapyn.189.cn/initLogin.do
 * 登录(服务密码登陆):手机号,密码,图片验证码(必填)
 * Created by zhouxinghai on 2017/7/17.
 */
public class YunNan10000ForWap implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(YunNan10000ForWap.class);

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
        logger.warn("defineProcess fail,params={}", param);
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        /**
         * 这里不一定有图片验证码,随机出现
         */
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String url = "http://wapyn.189.cn/vcImage.do";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setResponseContentType(ContentType.create("image/png")).setFullUrl(url).invoke();
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
            String templateUrl = "http://wapyn.189.cn/sendSms.do";
            String templateData = "accNbr=" + param.getMobile();
            String referer = "http://wapyn.189.cn/initLogin.do";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent) && StringUtils.contains(pageContent, "\"respCode\":\"0\"")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            }
            logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);

        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }

    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {

            //InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yun_nan_10000_wap/des.js");
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encodeMobile = invocable.invokeFunction("strEnc", param.getMobile().toString(), "wap_accnbr_2016", "", "").toString();
            Object encodePassword = invocable.invokeFunction("strEnc", param.getSmsCode(), "wap_password_2016", "", "");

            String templateUrl = "http://wapyn.189.cn/loginValidate.do";
            String templateData = "loginPwdType=B&nodeId=72&mode=other&enAccNbr=" + encodeMobile + "&enPassword=" + encodePassword + "&valid=" +
                    param.getPicCode();
            String referer = "http://wapyn.189.cn/initLogin.do";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            templateUrl = "http://wapyn.189.cn/self/info/custInfo.do?nodeId=319";
            referer = "http://wapyn.189.cn/self/index.do";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent) && pageContent.contains(String.valueOf(param.getMobile()))) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},errorMsg={}", param);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            //String errorMsg = JsoupXpathUtils.selectFirst(pageContent, "//div[@id='valcellphoneLoginFormMsgId']/text()");
            //if (StringUtils.isBlank(errorMsg)) {
            //    logger.info("登陆成功,param={}", param);
            //    return result.success();
            //}
            //if (StringUtils.contains(errorMsg, "验证码")) {
            //    logger.warn("登录失败-->图片验证码校验失败,param={},errorMsg={}", param, errorMsg);
            //    return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            //}
            //if (StringUtils.contains(errorMsg, "密码")) {
            //    logger.warn("登录失败-->密码错误,param={},errorMsg={}", param, errorMsg);
            //    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
            //}
            //logger.error("登陆失败,param={},errorMsg={}", param, errorMsg);
            //return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://wapyn.189.cn/self/fee/detailQuery?nodeId=86";
            String templateUrl = "http://wapyn.189.cn/self/fee/sendSms.do";
            String templateData = "accNbr=";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent) && StringUtils.contains(pageContent, "\"respCode\":\"0\"")) {
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

            String referer = "http://wapyn.189.cn/self/fee/detailQuery?nodeId=86";
            String templateUrl = "http://wapyn.189.cn/self/fee/detailRecord.do";
            String templateData = "date=" + billMonth + "&cdrtype=10&deailsms=" + param.getSmsCode() + "&nodeId=86";
            response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent)) {
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
