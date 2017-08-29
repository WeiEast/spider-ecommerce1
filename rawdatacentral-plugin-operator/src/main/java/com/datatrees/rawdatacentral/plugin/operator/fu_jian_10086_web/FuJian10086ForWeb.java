package com.datatrees.rawdatacentral.plugin.operator.fu_jian_10086_web;

import javax.script.Invocable;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * Created by guimeichao on 17/8/29.
 */
public class FuJian10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(FuJian10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            String templateUrl = "https://fj.ac.10086.cn/login";
            Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "fu_jian_10086_web_001").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "fu_jian_10086_web_002").setFullUrl(templateUrl).invoke();

            String backUrl = "https://fj.ac.10086.cn/4login/backPage.jsp";
            String errorurl = "https://fj.ac.10086.cn/4login/errorPage.jsp";
            String relayStateId = StringUtils.EMPTY;

            String loginType = PatternUtils.group(pageContent, "id=\"loginType\"\\s*name=\"type\"\\s*value=\"([^\"]+)\"", 1);
            List<String> backUrlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backUrlList)) {
                backUrl = backUrlList.get(0);
            }
            List<String> errorUrlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorUrlList)) {
                errorurl = errorUrlList.get(0);
            }
            String spid = PatternUtils.group(pageContent, "name=\"spid\"\\s*value=\"([^\"]+)\"", 1);
            List<String> relayStateIdList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
            if (!CollectionUtils.isEmpty(relayStateIdList)) {
                relayStateId = relayStateIdList.get(0);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "loginType", loginType);
            TaskUtils.addTaskShare(param.getTaskId(), "backUrl", backUrl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl", errorurl);
            TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);
            TaskUtils.addTaskShare(param.getTaskId(), "relayStateId", relayStateId);
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
        return null;
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
            String templateUrl = "https://fj.ac.10086.cn/common/image.jsp?r_{}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "fu_jian_10086_web_003").setFullUrl(templateUrl, db.setScale(16, BigDecimal.ROUND_HALF_UP)).invoke();
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
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("fu_jian_10086_web/des.js");
            Invocable invocable = ScriptEngineUtil.createInvocable(inputStream, "UTF-8");
            String encryptPwd = invocable.invokeFunction("enString", param.getPassword()).toString();

            String loginType = TaskUtils.getTaskShare(param.getTaskId(), "loginType");
            String backUrl = TaskUtils.getTaskShare(param.getTaskId(), "backUrl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String relayStateId = TaskUtils.getTaskShare(param.getTaskId(), "relayStateId");

            String templateUrl = "https://fj.ac.10086.cn/Login";
            String templateData = "type={}&backurl={}&errorurl={}&spid={}&RelayState={}&mobileNum={}&servicePassword={}&smsValidCode=&validCode" + "={}&Password-type=&button=%E7%99%BB++%E5%BD%95";
            String data = TemplateUtils.format(templateData, loginType, URLEncoder.encode(backUrl, "UTF-8"), URLEncoder.encode(errorurl, "UTF-8"), spid, URLEncoder.encode(relayStateId, "UTF-8"), param.getMobile(), encryptPwd, param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "fu_jian_10086_web_004").setFullUrl(templateUrl).setRequestBody(data).invoke();
            //TODO
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            String errorMessage = PatternUtils.group(pageContent, "errorMsg=([^\"]+)", 1);
            if (StringUtils.isNotBlank(errorMessage)) {
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_005").setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();

            String samLart = PatternUtils.group(pageContent, "callBackurlAll\\('([^']+)'", 1);

            templateUrl = "http://www.fj.10086.cn/my/?SAMLart={}&RelayState=";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_006").setFullUrl(templateUrl, samLart).invoke();
            pageContent = response.getPageContent();

            if (StringUtils.isNotBlank(pageContent) && pageContent.contains(param.getMobile().toString())) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
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
            String templateUrl = "https://fj.ac.10086.cn/SMSCodeSend?spid={}&mobileNum={}&validCode=0000&errorurl=http://www" + ".fj.10086.cn:80/my/login/send.jsp";
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_007").setFullUrl(templateUrl, spid, param.getMobile()).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("短信验证码已发送到您的手机")) {
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
        String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
        Response response = null;
        try {
            String templateUrl = "https://fj.ac.10086.cn/Login";
            String templateData = "s02=false&Password=&Password-type=&spid={}&validCode=0000&servicePassword=&n1=1&sso=0&RelayState=1&ocs_url=&s02" + "=false&sp_id=&do_login_type=&isValidateCode=1&type=A&smscode={}&mobileNum={}&agentcode=&backurl=http%3A%2F%2Fwww" + ".fj.10086.cn%3A80%2Fmy%2FssoAssert.jsp%3Ftypesso%3DC%26CALLBACK_URL%3Dhttp%3A%2F%2Fwww" + ".fj.10086.cn%3A80%2Fmy%2Fuser%2FgetUserInfo.do&errorurl=http%3A%2F%2Fwww.fj.10086.cn%3A80%2Fmy%2Flogin%2Fsend" + ".jsp&smsValidCode={}&smscode1={}";
            String data = TemplateUtils.format(templateData, spid, param.getSmsCode(), param.getMobile(), param.getSmsCode(), param.getSmsCode());
            response = TaskHttpClient.create(param, RequestType.POST, "fu_jian_10086_web_008").setFullUrl(templateUrl).setRequestBody(data).invoke();
            String pageContent = response.getPageContent();

            templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_009").setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();

            templateUrl = PatternUtils.group(pageContent, "href = \"([^\"]+)\"", 1);
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10086_web_010").setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();

            if (pageContent.contains(param.getMobile().toString())) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
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
