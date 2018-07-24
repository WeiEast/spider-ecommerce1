package com.datatrees.rawdatacentral.plugin.operator.hu_nan_10000_wap;

import javax.script.Invocable;
import java.util.List;
import java.util.Map;

import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/25.
 */
public class HuNan10000ForWap implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(HuNan10000ForWap.class);

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
            String templateUrl = "http://waphn.189.cn/page/common/login/imagelogin.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable_mobile = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_mobile.js", "GBK");
            String encryptMobile = invocable_mobile.invokeFunction("encryptStr", param.getMobile().toString()).toString();

            Invocable invocable_password = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des_password.js", "GBK");
            String encryptPassword = invocable_password.invokeFunction("encyptPwd", param.getPassword()).toString();

            String referer = "http://waphn.189.cn/user/login/toLogin.action";
            String templateUrl = "http://waphn.189.cn/user/login/userLogin.action";
            String templateData = "loginModel=&reUrl=&phoneNum={}&servicePwd={}&accountType=2000004&areaCode=&pwdType=1&vicode={}";
            String data = TemplateUtils.format(templateData, encryptMobile, encryptPassword, param.getPicCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "你好")) {
                logger.warn("登录成功,params={}", param);
                return result.success();
            } else {
                String errorMsg = pageContent;
                List<String> errorMessageList = XPathUtil.getXpath("//p[@id='msg_p']/text()", pageContent);
                if (CollectionUtils.isNotEmpty(errorMessageList)) {
                    errorMsg = errorMessageList.get(0);
                }
                logger.warn("登录失败,param={},errorMsg={}", param, errorMsg);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://waphn.189.cn/page/common/image.jsp?t={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            logger.info("详单-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://waphn.189.cn/hnselfservice/billquery/queryBilly.action?number={}&vicode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), param.getPicCode()).invoke();
            if (StringUtils.contains(response.getPageContent(), "success")) {
                logger.info("详单-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            logger.info("详单-->短信验证码-->刷新成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={}", param, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String queryMonth = new DateTime().toString("yyyy-MM");
            String lastMonth = new DateTime().withFieldAdded(DurationFieldType.months(), -1).toString("yyyy-MM");

            String referer = "http://waphn.189.cn/hnselfservice/billquery/queryBillList.action?patitype=2";
            String templateUrl = "http://waphn.189.cn/hnselfservice/billquery/queryBillListx" +
                    ".action?tm=&tabIndex=2&queryMonth={}&patitype=2&code={}&accNbr={}&chargeType=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, queryMonth, param.getSmsCode(), param.getMobile()).setReferer(referer).invoke();
            if (!StringUtils.contains(response.getPageContent(), "验证码错误!")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                //response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.GET)
                //        .setFullUrl(templateUrl, lastMonth, param.getSmsCode(), param.getMobile()).setReferer(referer).invoke();
                //if (StringUtils.contains(response.getPageContent(), "费用")) {
                //    logger.info("详单-->校验成功,param={}", param);
                //    return result.success();
                //} else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
                //}
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
