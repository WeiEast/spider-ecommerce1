package com.datatrees.rawdatacentral.plugin.operator.jiang_xi_10086_web;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.*;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * Created by guimeichao on 17/11/7.
 */
public class JiangXi10086ForWeb implements OperatorPluginService {

    private static Logger logger = LoggerFactory.getLogger(JiangXi10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://jx.ac.10086.cn/login";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            String form = XPathUtil.getXpath("//form[@id='normal-user']", pageContent).get(0);

            String from = "yanhuang";
            String relayState = "type=A;backurl=http%3A%2F%2Fwww.jx.10086.cn%2Fmy%2F;nl=3";
            String backurl = "https://jx.ac.10086.cn/4login/backPage.jsp";
            String errorurl = "https://jx.ac.10086.cn/4login/errorPage.jsp";
            String sid = "AC5CD1B95C0862C1A3BAFE34B3682685";
            String spid = "40288b8b3627308901362a56fd1b0002";
            String type = "B";

            List<String> fromList = XPathUtil.getXpath("//input[@name='from']/@value", form);
            if (!CollectionUtils.isEmpty(fromList)) {
                from = fromList.get(0);
            }
            List<String> backurlList = XPathUtil.getXpath("//input[@name='backurl']/@value", form);
            if (!CollectionUtils.isEmpty(backurlList)) {
                backurl = backurlList.get(0);
            }
            List<String> errorurlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", form);
            if (!CollectionUtils.isEmpty(errorurlList)) {
                errorurl = errorurlList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@name='spid']/@value", form);
            if (!CollectionUtils.isEmpty(spidList)) {
                spid = spidList.get(0);
            }
            List<String> sidList = XPathUtil.getXpath("//input[@name='sid']/@value", form);
            if (!CollectionUtils.isEmpty(sidList)) {
                sid = sidList.get(0);
            }
            List<String> typeList = XPathUtil.getXpath("//input[@name='type']/@value", form);
            if (!CollectionUtils.isEmpty(typeList)) {
                type = typeList.get(0);
            }
            List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", form);
            if (!CollectionUtils.isEmpty(relayStateList)) {
                relayState = relayStateList.get(0);
            }

            TaskUtils.addTaskShare(param.getTaskId(), "from", from);
            TaskUtils.addTaskShare(param.getTaskId(), "backurl", backurl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl", errorurl);
            TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);
            TaskUtils.addTaskShare(param.getTaskId(), "sid", sid);
            TaskUtils.addTaskShare(param.getTaskId(), "type", type);
            TaskUtils.addTaskShare(param.getTaskId(), "relayState", relayState);
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
            String templateUrl = "https://jx.ac.10086.cn/common/image.jsp";
            String referer = "https://jx.ac.10086.cn/POST";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
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
        String templateUrl = StringUtils.EMPTY;
        try {
            String from = TaskUtils.getTaskShare(param.getTaskId(), "from");
            String backurl = TaskUtils.getTaskShare(param.getTaskId(), "backurl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String sid = TaskUtils.getTaskShare(param.getTaskId(), "sid");
            String type = TaskUtils.getTaskShare(param.getTaskId(), "type");
            String relayState = TaskUtils.getTaskShare(param.getTaskId(), "relayState");

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptMobile = invocable.invokeFunction("enString", param.getMobile().toString()).toString();
            String encryptPassword = invocable.invokeFunction("enString", param.getPassword()).toString();

            String referer = "https://jx.ac.10086.cn/POST";
            templateUrl = "https://jx.ac.10086.cn/Login";
            String templateData
                    = "from={}&sid={}&type={}&backurl={}&errorurl={}&spid={}&RelayState={}&mobileNum={}&servicePassword={}&smsValidCode=&validCode={}";
            String data = TemplateUtils
                    .format(templateData, from, sid, type, URLEncoder.encode(backurl, "UTF-8"), URLEncoder.encode(errorurl, "UTF-8"), spid,
                            URLEncoder.encode(relayState, "UTF-8"), encryptMobile, encryptPassword, param.getPicCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            if (!StringUtils.contains(pageContent, "callBackurl")) {
                String errorMsg = PatternUtils.group(pageContent, "\\sparent.form_Msg\\(\"([^\"]+)\"\\);", 1);
                loginOut(param, templateUrl);
                logger.error("登陆失败,param={},errorMsg={}", param, errorMsg);
                return result.failure(errorMsg);
            }
            String SAMLart = PatternUtils.group(pageContent, "callBackurl\\('([^']+)'\\)", 1);
            templateUrl = "http://www.jx.10086.cn/my/";
            templateData = "SAMLart={}&RelayState={}";
            data = TemplateUtils.format(templateData, SAMLart, URLEncoder.encode(relayState, "UTF-8"));
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                loginOut(param, templateUrl);
                logger.error("登陆失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            loginOut(param, templateUrl);
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        String templateUrl = StringUtils.EMPTY;
        try {
            templateUrl = "https://jx.ac.10086.cn/common/image.jsp?l=";
            String referer = "http://service.jx.10086.cn/service/checkSmsPassN.action?menuid=000200010003";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            loginOut(param, templateUrl);
            loginOutForBillDetails(param, templateUrl);
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        String templateUrl = StringUtils.EMPTY;
        try {
            String referer = "http://www.jx.10086.cn/my/queryXXNew.do";
            templateUrl = "http://service.jx.10086.cn/service/showBillDetail!queryShowBillDatailN.action?menuid=000200010003";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }

            String spid_sms = "ff8080812a38aae6012a38b4796c0004";
            String backurl_sms = "http://service.jx.10086.cn/service/backAction.action?menuid=000200010003";
            String errorurl_sms = "http://service.jx.10086.cn/service/common/ssoPrompt.jsp";
            String sid_sms = "C29AA702B8042C46A7B559CC1242394D";
            String ssoImageUrl_sms = "https://jx.ac.10086.cn/common/image.jsp";
            String ssoSmsUrl_sms = "https://jx.ac.10086.cn/SMSCodeSend";
            List<String> backurlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backurlList)) {
                backurl_sms = backurlList.get(0);
            }
            List<String> errorurlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorurlList)) {
                errorurl_sms = errorurlList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@name='spid']/@value", pageContent);
            if (!CollectionUtils.isEmpty(spidList)) {
                spid_sms = spidList.get(0);
            }
            List<String> sidList = XPathUtil.getXpath("//input[@name='sid']/@value", pageContent);
            if (!CollectionUtils.isEmpty(sidList)) {
                sid_sms = sidList.get(0);
            }
            List<String> ssoImageUrlList = XPathUtil.getXpath("//input[@name='ssoImageUrl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(ssoImageUrlList)) {
                ssoImageUrl_sms = ssoImageUrlList.get(0);
            }
            List<String> ssoSmsUrlList = XPathUtil.getXpath("//input[@name='ssoSmsUrl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(ssoSmsUrlList)) {
                ssoSmsUrl_sms = ssoSmsUrlList.get(0);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "backurl_sms", backurl_sms);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl_sms", errorurl_sms);
            TaskUtils.addTaskShare(param.getTaskId(), "spid_sms", spid_sms);
            TaskUtils.addTaskShare(param.getTaskId(), "sid_sms", sid_sms);
            TaskUtils.addTaskShare(param.getTaskId(), "ssoImageUrl_sms", ssoImageUrl_sms);
            TaskUtils.addTaskShare(param.getTaskId(), "ssoSmsUrl_sms", ssoSmsUrl_sms);

            templateUrl = "https://jx.ac.10086.cn/SMSCodeSend?mobileNum={}&errorurl=http://service.jx.10086.cn/service/common/ssoPrompt" +
                    ".jsp&spid={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), spid_sms).setReferer(referer).invoke();
            //if (response.getPageContent().contains("随机短信验证码已发送成功")) {
            logger.info("详单-->短信验证码-->刷新成功,param={}", param);
            return result.success();
            //} else {
            //    logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response);
            //    return result.failure(ErrorCode.REFESH_SMS_FAIL);
            //}
        } catch (Exception e) {
            templateUrl = "http://service.jx.10086.cn/service/checkSmsPassN.action?menuid=000200010003";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            loginOut(param, templateUrl);
            loginOutForBillDetails(param, templateUrl);
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        String templateUrl = StringUtils.EMPTY;
        try {
            String backurl_sms = TaskUtils.getTaskShare(param.getTaskId(), "backurl_sms");
            String errorurl_sms = TaskUtils.getTaskShare(param.getTaskId(), "errorurl_sms");
            String spid_sms = TaskUtils.getTaskShare(param.getTaskId(), "spid_sms");
            String sid_sms = TaskUtils.getTaskShare(param.getTaskId(), "sid_sms");
            String ssoImageUrl_sms = TaskUtils.getTaskShare(param.getTaskId(), "ssoImageUrl_sms");
            String ssoSmsUrl_sms = TaskUtils.getTaskShare(param.getTaskId(), "ssoSmsUrl_sms");

            String referer = "http://service.jx.10086.cn/service/checkSmsPassN.action?menuid=000200010003";
            templateUrl = "https://jx.ac.10086.cn/Login";
            String templateData = "smsValidCode={}&validCode={}&submitBtn=%E7%A1%AE%E5%AE%9A&type=A&loginStatus=A&loginFlag=false&spid={}&backurl" +
                    "={}&errorurl={}&sid={}&mobileNum={}&ssoImageUrl={}&ssoSmsUrl={}&menuid=000200010003";
            String data = TemplateUtils
                    .format(templateData, param.getSmsCode(), param.getPicCode(), spid_sms, URLEncoder.encode(backurl_sms, "UTF-8"),
                            URLEncoder.encode(errorurl_sms, "UTF-8"), sid_sms, param.getMobile(), URLEncoder.encode(ssoImageUrl_sms, "UTF-8"),
                            URLEncoder.encode(ssoSmsUrl_sms, "UTF-8"));
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "replace")) {
                referer = "https://jx.ac.10086.cn/Login";
                templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl)
                        .setReferer(referer).invoke();

                templateUrl
                        = "http://service.jx.10086.cn/service/showBillDetail!importExcel.action?menuid=00890201&billType=202&startDate=20171101&endDate=20171109";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

                templateUrl = "http://service.jx.10086.cn/service/showBillDetail!queryIndex" +
                        ".action?billType=202&startDate=20170601&endDate=20170630&clientDate=&menuid=00890201&requestStartTime=";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl)
                        .setReferer(referer).invoke();

                templateUrl = "http://service.jx.10086.cn/service/service/queryPersonalInfoN!executeForAjax.action";
                data = "menuid=000200030004";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setRequestBody(data).invoke();
                templateUrl = "http://service.jx.10086.cn/service/service/queryCurrFeeAjax.action";
                data = "menuid=000200010001";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setRequestBody(data).invoke();
                templateUrl = "http://service.jx.10086.cn/service/queryWebPageInfo" +
                        ".action?requestStartTime=&menuid=00890104&queryMonth=201710&s={}";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, Math.random())
                        .invoke();
                loginOut(param, templateUrl);
                loginOutForBillDetails(param, templateUrl);
                logger.error("详单-->短信验证码正确,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);

            } else {
                loginOut(param, templateUrl);
                loginOutForBillDetails(param, templateUrl);
                logger.error("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            }
            //if (response.getPageContent().contains("短信验证码错误")) {
            //    logger.warn("详单-->短信验证码错误,param={}", param);
            //    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            //} else {
            //    logger.info("详单-->校验成功,param={}", param);
            //    return result.success();
            //}
        } catch (Exception e) {
            loginOut(param, templateUrl);
            loginOutForBillDetails(param, templateUrl);
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    /**
     * 处理跳转服务
     * @param pageContent
     * @return
     */
    private String executeScriptSubmit(Long taskId, String websiteName, String remark, String pageContent) {
        String action = JsoupXpathUtils.selectFirst(pageContent, "//form/@action");
        String method = JsoupXpathUtils.selectFirst(pageContent, "//form/@method");
        List<Map<String, String>> list = JsoupXpathUtils.selectAttributes(pageContent, "//input");
        String url = action.replaceAll("\\?", "");
        Map<String, Object> params = new HashMap<>();
        if (null != list && !list.isEmpty()) {
            for (Map<String, String> map : list) {
                if (map.containsKey("name") && map.containsKey("value")) {
                    params.put(map.get("name"), map.get("value"));
                }
            }
        }
        RequestType requestType = StringUtils.equalsIgnoreCase("post", method) ? RequestType.POST : RequestType.GET;
        Response response = TaskHttpClient.create(taskId, websiteName, requestType).setUrl(url).setParams(params).invoke();
        return response.getPageContent();
    }

    private void loginOut(OperatorParam param, String referer) {
        String templateUrl = "https://jx.ac.10086.cn/logout";
        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl)
                .setReferer(referer).invoke();
        String pageContent = response.getPageContent();
        templateUrl = "http://www.jx.10086.cn/";
        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                .setRequestBody("display=0").invoke();
        RedisUtils.del(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId()));
        logger.info("退出登录-->成功");
    }

    private void loginOutForBillDetails(OperatorParam param, String referer) {
        String templateUrl = "https://jx.ac.10086.cn/logout";
        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl)
                .setReferer(referer).invoke();
        String pageContent = response.getPageContent();
        templateUrl = "http://www.jx.10086.cn/";
        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                .setRequestBody("display=0").invoke();
        templateUrl = "http://www1.10086.cn/service/sso/logout.jsp?channelID=12027&backUrl=http%3A%2F%2Fwww.10086.cn%2Fjx%2Findex_791_791.html";
        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
        RedisUtils.del(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId()));
        logger.info("退出登录-->成功");
    }

}
