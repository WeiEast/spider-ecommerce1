package com.datatrees.rawdatacentral.plugin.operator.he_nan_10086_wap;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/11/16.
 */
public class HeNan10086ForWap implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(HeNan10086ForWap.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wap.ha.10086.cn/login.action";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            String onceTag = StringUtils.EMPTY;
            String returnUrl = StringUtils.EMPTY;
            String checkContract = StringUtils.EMPTY;
            String loginRandom = StringUtils.EMPTY;
            String loginInsert = StringUtils.EMPTY;
            List<String> onceTagList = XPathUtil.getXpath("//input[@name='onceTag']/@value", pageContent);
            if (!onceTagList.isEmpty()) {
                onceTag = onceTagList.get(0);
            }
            List<String> returnUrlList = XPathUtil.getXpath("//input[@name='returnUrl']/@value", pageContent);
            if (!returnUrlList.isEmpty()) {
                returnUrl = returnUrlList.get(0);
            }
            List<String> checkContractList = XPathUtil.getXpath("//input[@name='checkContract']/@value", pageContent);
            if (!checkContractList.isEmpty()) {
                checkContract = checkContractList.get(0);
            }
            List<String> loginRandomList = XPathUtil.getXpath("//input[@name='loginRandom']/@value", pageContent);
            if (!loginRandomList.isEmpty()) {
                loginRandom = loginRandomList.get(0);
            }
            List<String> loginInsertList = XPathUtil.getXpath("//input[@name='loginInsert']/@value", pageContent);
            if (!loginInsertList.isEmpty()) {
                loginInsert = loginInsertList.get(0);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "onceTag", onceTag);
            TaskUtils.addTaskShare(param.getTaskId(), "returnUrl", returnUrl);
            TaskUtils.addTaskShare(param.getTaskId(), "checkContract", checkContract);
            TaskUtils.addTaskShare(param.getTaskId(), "loginRandom", loginRandom);
            TaskUtils.addTaskShare(param.getTaskId(), "loginInsert", loginInsert);
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
            String templateUrl = "http://wap.ha.10086.cn/sso!img2.action?d={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    public HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String onceTag = TaskUtils.getTaskShare(param.getTaskId(), "onceTag");
            String returnUrl = TaskUtils.getTaskShare(param.getTaskId(), "returnUrl");
            String checkContract = TaskUtils.getTaskShare(param.getTaskId(), "checkContract");
            String loginRandom = TaskUtils.getTaskShare(param.getTaskId(), "loginRandom");
            String loginInsert = TaskUtils.getTaskShare(param.getTaskId(), "loginInsert");

            String referer = "http://wap.ha.10086.cn/login.action";
            String templateUrl = "http://wap.ha.10086.cn/login!loginAct.action";
            String templateData = "onceTag={}&returnUrl={}&checkContract={}&onceTag={}&svcNum={}&numbc={}&passwd={}&validateCode={}";

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String svcNum = invocable.invokeFunction("encyptSvcNum", param.getMobile().toString()).toString();
            String numbc = invocable.invokeFunction("encyptNumbc", param.getMobile().toString()).toString();
            String passwd = invocable.invokeFunction("encyptPwd", param.getPassword(), loginInsert, loginRandom).toString();

            String data = TemplateUtils
                    .format(templateData, onceTag, URLEncoder.encode(returnUrl, "UTF-8"), checkContract, onceTag, URLEncoder.encode(svcNum, "UTF-8"),
                            URLEncoder.encode(numbc, "UTF-8"), URLEncoder.encode(passwd, "UTF-8"), param.getPicCode().toLowerCase());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "系统检测到不安全的请求方式，请重新登录。")) {
                logger.error("登陆失败,系统检测到不安全的请求方式,{},param={},response={}", param, response);
                init(param);
                return result.failure(ErrorCode.LOGIN_ERROR);
            }
            String errorMsg = StringUtils.EMPTY;
            List<String> errorMsgList = XPathUtil.getXpath("div.error span/text()", pageContent);
            if (!errorMsgList.isEmpty()) {
                errorMsg = errorMsgList.get(0);
            }
            if (StringUtils.isBlank(errorMsg)) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,{},param={},response={}", errorMsg, param, response);
                init(param);
                return result.failure(errorMsg);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            init(param);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        String pageContent = null;
        try {
            String referer = "http://wap.ha.10086.cn/";
            String templateUrl = "http://wap.ha.10086.cn/fee/query-now-detail.action?menuCode=61037";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "验证码已发送，请注意查收") || StringUtils.contains(pageContent, "尊敬的客户，您之前的验证码仍在有效期内")) {
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
            String referer = "http://wap.ha.10086.cn/fee/query-now-detail.action?menuCode=61037";
            String templateUrl = "http://wap.ha.10086.cn/fee/query-now-detail!ver.action?menuCode=61037";
            String templateData = "verCode={}";
            String data = TemplateUtils.format(templateData, param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (!StringUtils.contains(pageContent, "随机码错误，请输入正确的随机码")) {
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

}
