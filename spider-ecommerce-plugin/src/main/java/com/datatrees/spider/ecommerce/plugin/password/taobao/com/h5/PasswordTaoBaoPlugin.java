package com.datatrees.spider.ecommerce.plugin.password.taobao.com.h5;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin.QRLoginMonitor;
import com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin.QRStatusManager;
import com.datatrees.spider.share.common.http.ProxyUtils;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.LoginMessage;
import com.datatrees.spider.share.domain.QRStatus;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.share.service.plugin.CommonPlugin;
import com.treefinance.crawler.framework.util.xpath.XPathUtil;
import com.treefinance.crawler.plugin.util.ScriptEngineUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guimeichao
 * @date 2019/2/25
 */
public class PasswordTaoBaoPlugin implements CommonPlugin {

    private static final Logger logger = LoggerFactory.getLogger(PasswordTaoBaoPlugin.class);
    private static final String MAIN_URL = "https://login.taobao.com/member/login.jhtml?style=taobao&goto=https://consumeprod.alipay.com/record/index.htm%3Fsign_from%3D3000";
    private static final String LOGIN_URL = "https://login.taobao.com/member/login.jhtml?redirectURL=https://consumeprod.alipay.com/record/index.htm?sign_from=3000";
    private static final String REDIRECT_URL = "https://login.taobao.com/member/login.jhtml?tpl_redirect_url=https://authet15.alipay"
        + ".com:443/login/trustLoginResultDispatch.htm?redirectType=&sign_from=3000&goto=https%3A%2F%2Fconsumeprod.alipay.com%2Frecord%2Findex.htm%3Fnull%3D&from_alipay=1";
    private static final String RECORD_URL = "https://consumeprod.alipay.com/record/index.htm?null=";

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={}", param, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Object> submitForLogin(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            ProxyUtils.setProxyEnable(param.getTaskId(), true);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(MAIN_URL).invoke();
            String pageContent = response.getPageContent();
            String jNcoToken = XPathUtil.getXpath("//input[@id='J_NcoToken']/@value", pageContent).get(0);
            String jPBK = XPathUtil.getXpath("//input[@id='J_PBK']/@value", pageContent).get(0);

            String encryptPassWord = (String)ScriptEngineUtil.evalScript("js/rsa.js", "doRSAEncrypt", param.getPassword(), jPBK);
            String templateData =
                "TPL_username={}&ncoToken={}&style=taobao&from=tb&TPL_password_2={}&loginASR=1&loginASRSuc=1&osPF=MacIntel&appkey=00000000&mobileLoginLink=" + URLEncoder.encode(
                    "https://login.taobao.com/member/login.jhtml?style=taobao&goto=https://consumeprod.alipay.com/record/index.htm?sign_from=3000&useMobile=trueshowAssistantLink",
                    "UTF-8");
            String data = TemplateUtils.format(templateData, param.getUsername().toString(), jNcoToken, encryptPassWord);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(LOGIN_URL)
                .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "页面跳转中") && StringUtils.contains(pageContent, "self.location.href")) {
                String selfUrl = PatternUtils.group(pageContent, "self.location.href = \"([^\"]+)\";", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "selfUrl", selfUrl);
                logger.info("登陆成功,但需要进行手机短信验证,param={},selfUrl={}", param, selfUrl);
                triggerAfterLogin(param);
                return result.success();
            } else if (StringUtils.contains(pageContent, "页面跳转中") && StringUtils.contains(pageContent, "top.location.href")) {
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(REDIRECT_URL).invoke();
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(RECORD_URL).invoke();
                triggerAfterLogin(param);
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                List<String> errorMsgList = XPathUtil.getXpath("div#J_Message p.error/text()", pageContent);
                String errorMsg = null;
                if (errorMsgList != null && !errorMsgList.isEmpty()) {
                    errorMsg = errorMsgList.get(0);
                    String error = PatternUtils.group(errorMsg, "^([^，]+)，", 1);
                    if (StringUtils.isNotBlank(error)) {
                        errorMsg = error;
                    }
                }
                if (StringUtils.isNotBlank(errorMsg)) {
                    logger.error("登陆失败,param={},pageContent={}", param, pageContent);
                    return result.failure(errorMsg);
                }
            }
            logger.error("登陆失败,param={},response={}", param, response);
            return result.failure(ErrorCode.LOGIN_ERROR);
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Object> refeshSmsCodeForBillDetail(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String selfUrl = TaskUtils.getTaskShare(param.getTaskId(), "selfUrl");
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(selfUrl).invoke();
            String pageContent = response.getPageContent();

            String checkUrl = XPathUtil.getXpath("//div[@class='login-check-left']/iframe/@src", pageContent).get(0);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(checkUrl).invoke();
            pageContent = response.getPageContent();

            checkUrl = PatternUtils.group(pageContent, "window.location.href = \"([^\"]+)\"", 1);
            String htoken = PatternUtils.group(checkUrl, "htoken=([^&]+)", 1);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(checkUrl).invoke();
//
//            checkUrl = "https://passport.taobao.com/iv/identity_verify.htm?htoken={}&tag=8";
//            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(checkUrl, htoken).invoke();
            pageContent = response.getPageContent();

            String mobile = XPathUtil.getXpath("//input[@id='J_MobileVal']/@value", pageContent).get(0);
            String token = XPathUtil.getXpath("//input[@name='_tb_token_']/@value", pageContent).get(0);
            TaskUtils.addTaskShare(param.getTaskId(), "token", token);
            TaskUtils.addTaskShare(param.getTaskId(), "htoken", htoken);
            TaskUtils.addTaskShare(param.getTaskId(), "mobileString", mobile);
            String sendSmsUrl = "https://passport.taobao.com/iv/phone/send_code.do?htoken={}&phone={}&type=phone&area=86&tag=86&_={}";
            response =
                TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(sendSmsUrl, htoken, mobile, System.currentTimeMillis()).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "验证码发送成功")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            }
            logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, pageContent);
            return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Object> submitForBillDetail(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            String token = TaskUtils.getTaskShare(param.getTaskId(), "token");
            String htoken = TaskUtils.getTaskShare(param.getTaskId(), "htoken");
            String mobile = TaskUtils.getTaskShare(param.getTaskId(), "mobileString");
            String validSmsUrl = "https://passport.taobao.com/iv/identity_verify.htm?tag=8&htoken={}&app_name=";
            Map<String, Object> map = new HashMap<>(11);
            map.put("_fm.v._0.a", "86");
            map.put("action", "verify_action");
            map.put("event_submit_do_validate", "notNull");
            map.put("_fm.v._0.t", "8");
            map.put("_fm.v._0.h", htoken);
            map.put("_fm.v._0.ty", "8");
            map.put("_fm.v._0.c", "pc");
            map.put("_fm.v._0.p", mobile);
            map.put("_fm.v._0.ph", param.getSmsCode());
            map.put("_fm.v._0.pho", "sms");
            map.put("_tb_token_", token);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(validSmsUrl, htoken).setParams(map).invoke();
            if (!StringUtils.contains(response.getPageContent(), "top.location.href")) {
                logger.error("详单-->校验失败,param={},response={}", param, response);
                return result.failure(ErrorCode.VALIDATE_ERROR);
            }
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(REDIRECT_URL).invoke();
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(RECORD_URL).invoke();
            logger.info("详单-->校验成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private void triggerAfterLogin(CommonPluginParam param) {
        QRStatusManager.setStatus(param.getTaskId(), QRStatus.SUCCESS);
        logger.info("用户完成登录，发送登录成功消息，taskId={}, website={}", param.getTaskId(), param.getWebsiteName());
        QRLoginMonitor.notifyLogger(param, "登录成功", "校验-->成功");
        String cookieString = TaskUtils.getCookieString(param.getTaskId());
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setTaskId(param.getTaskId());
        loginMessage.setWebsiteName(param.getWebsiteName());
        loginMessage.setCookie(cookieString);
        loginMessage.setAccountNo(StringUtils.defaultString(param.getUsername()));
        BeanFactoryUtils.getBean(CommonPluginService.class).sendLoginSuccessMsg(TopicEnum.SPIDER_ECOMMERCE.getCode(), loginMessage);
    }

}
