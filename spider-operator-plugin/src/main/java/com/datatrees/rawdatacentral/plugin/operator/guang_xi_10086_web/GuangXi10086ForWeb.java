package com.datatrees.rawdatacentral.plugin.operator.guang_xi_10086_web;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.JsoupXpathUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
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
import org.springframework.util.CollectionUtils;

/**
 * http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp
 * Created by guimeichao on 17/9/5.
 */
public class GuangXi10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(GuangXi10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (org.apache.commons.lang.StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }

            if (pageContent.contains("replace")) {
                templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            //获取登录所需参数
            String backurl = "https://gx.ac.10086.cn/4logingx/backPage.jsp";
            String errorurl = "https://gx.ac.10086.cn/4logingx/errorPage.jsp";
            String spid = "";
            String relayState
                    = "type=A;backurl=http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp;nl=3;loginFrom=http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp";
            String isValidateCode = "1";
            String imgUrl = "https://gx.ac.10086.cn/common/image.jsp";
            String dateTimeToken
                    = "29CDA89AF8942DFC6B57DED4510944119511D02A134839CC26FDB887C8AE48DD261AA0E11141D535B92A06E1933D2FB8E7644F61861B51ADC0013D1EBAD0B0F2";

            List<String> backurlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backurlList)) {
                backurl = backurlList.get(0);
            }
            List<String> errorurlList = XPathUtil.getXpath("//input[@name='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorurlList)) {
                errorurl = errorurlList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@name='spid']/@value", pageContent);
            if (!CollectionUtils.isEmpty(spidList)) {
                spid = spidList.get(0);
            }
            List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
            if (!CollectionUtils.isEmpty(relayStateList)) {
                relayState = relayStateList.get(0);
            }
            List<String> isValidateCodeList = XPathUtil.getXpath("//input[@name='isValidateCode']/@value", pageContent);
            if (!CollectionUtils.isEmpty(isValidateCodeList)) {
                isValidateCode = isValidateCodeList.get(0);
            }
            List<String> imgUrlList = XPathUtil.getXpath("//input[@name='cur_verify_img_url']/@value", pageContent);
            if (!CollectionUtils.isEmpty(imgUrlList)) {
                imgUrl = imgUrlList.get(0);
            }
            dateTimeToken = PatternUtils.group(pageContent, "ID\\s*var _dateTimeToken = '([^\"]+)'", 1);

            TaskUtils.addTaskShare(param.getTaskId(), "backurl", backurl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl", errorurl);
            TaskUtils.addTaskShare(param.getTaskId(), "relayState", relayState);
            TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);
            TaskUtils.addTaskShare(param.getTaskId(), "isValidateCode", isValidateCode);
            TaskUtils.addTaskShare(param.getTaskId(), "imgUrl", imgUrl);
            TaskUtils.addTaskShare(param.getTaskId(), "dateTimeToken", dateTimeToken);

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
        logger.warn("defineProcess fail,params={}", param);
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://gx.ac.10086.cn/common/image.jsp?_date={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
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
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String referer = "http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp";
            String templateUrl = "https://gx.ac.10086.cn/SMSCodeSend?mobileNum={}&spid={}&errorurl={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), spid,
                            URLEncoder.encode("http://www.gx.10086.cn/wodeyidong/public/LoginAction/showSSOErr.action", "UTF-8")).setReferer(referer)
                    .invoke();
            if (response.getPageContent().contains("短信验证码已发送到您的手机")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_SMS_FAIL);
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

            String backurl = TaskUtils.getTaskShare(param.getTaskId(), "backurl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String relayState = TaskUtils.getTaskShare(param.getTaskId(), "relayState");
            String isValidateCode = TaskUtils.getTaskShare(param.getTaskId(), "isValidateCode");
            String imgUrl = TaskUtils.getTaskShare(param.getTaskId(), "imgUrl");
            String dateTimeToken = TaskUtils.getTaskShare(param.getTaskId(), "dateTimeToken");
            relayState = relayState.replace("type=B", "type=A");

            String referer = "http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp";
            String templateUrl = "https://gx.ac.10086.cn/Login";
            String templateData = "type=A&backurl={}&errorurl={}&spid={}&RelayState={}&isEncrypt=true&cur_verify_img_url={}&loginType=smsPass" +
                    "&mobileNum={}&servicePassword=&smsValidCode={}&validCode={}&isValidateCode={}";
            String data = TemplateUtils.format(templateData, URLEncoder.encode(backurl, "UTF-8"), URLEncoder.encode(errorurl, "UTF-8"), spid,
                    URLEncoder.encode(relayState, "UTF-8"), URLEncoder.encode(imgUrl, "UTF-8"), param.getMobile(), param.getSmsCode(),
                    param.getPicCode(), isValidateCode);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("replace")) {
                templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }
            if (pageContent.contains("postartifact")) {
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "", pageContent);
            }
            if (StringUtils.isBlank(pageContent) && !pageContent.contains("跳转中")) {
                logger.error("gx10086 login valid phone error");
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            templateUrl = "http://www.gx.10086.cn/wodeyidong";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).invoke();

            //templateUrl = "http://www.gx.10086.cn/wodeyidong/public/QueryBaseBillInfoAction/refreshFee.action";
            //templateData = "ajaxType=json&_tmpDate={}&_buttonId=&_dateTimeToken={}";
            //data = TemplateUtils.format(templateData, System.currentTimeMillis(), dateTimeToken);
            //response = TaskHttpClient.create(param.getTaskId(),param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data).invoke();
            pageContent = response.getPageContent();
            if (pageContent.contains("姓名")) {
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
            String referer = "http://www.gx.10086.cn/wodeyidong/mymob/xiangdan.jsp";
            String templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryDetailInfo/QueryDetailInfoAction/initBusi" +
                    ".menu?is_first_render=true&_menuId=410900003558&=&_lastCombineChild=false&_zoneId=busimain&_tmpDate=&_buttonId=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryDetailInfo/QueryDetailInfoAction/sendSecondPsw" +
                    ".menu?ajaxType=json&_tmpDate=&_menuId=410900003558&_buttonId=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            if (response.getPageContent().contains("随机短信验证码已发送成功")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response);
                return result.failure(ErrorCode.REFESH_SMS_FAIL);
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
            String referer = "http://www.gx.10086.cn/wodeyidong/mymob/xiangdan.jsp";
            String templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryDetailInfo/QueryDetailInfoAction/checkSecondPsw" +
                    ".menu?input_random_code={}&input_svr_pass={}&is_first_render=true&_zoneId=_sign_errzone&_tmpDate=&_menuId=410900003558" +
                    "&_buttonId=other_sign_btn";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, param.getSmsCode(), param.getPassword()).setReferer(referer)
                    .addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            logger.info(response.getPageContent());
            if (response.getPageContent().contains("短信验证码错误")) {
                logger.warn("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            } else {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            }
        } catch (Exception e) {
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
}
