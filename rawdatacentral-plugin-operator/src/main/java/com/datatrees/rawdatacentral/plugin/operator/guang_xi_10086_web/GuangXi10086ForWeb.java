package com.datatrees.rawdatacentral.plugin.operator.guang_xi_10086_web;

import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * https://gx.ac.10086.cn/login
 * Created by guimeichao on 17/9/5.LiaoNing10086ForWeb.java
 */
public class GuangXi10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(GuangXi10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://gx.ac.10086.cn/login";
            response = TaskHttpClient.create(param, RequestType.GET, "guang_xi_10086_web_001").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.matches("replace\\('([^']+)'\\)")) {
                templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param, RequestType.GET, "guang_xi_10086_web_002").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }
            //获取登录所需参数
            String type = "B";
            String backurl = "https://gx.ac.10086.cn/4logingx/backPage.jsp";
            String errorurl = "https://gx.ac.10086.cn/4logingx/errorPage.jsp";
            String spid = "";
            String relayState
                    = "type=A;backurl=http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp;nl=3;loginFrom=http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp";
            String isValidateCode = "0";
            String myaction = "http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp";
            String netaction = "http://www.gx.10086.cn/padhallclient/netclient/customer/businessDealing";

            List<String> typeList = XPathUtil.getXpath("//input[@id='loginType']/@value", pageContent);
            if (!CollectionUtils.isEmpty(typeList)) {
                type = typeList.get(0);
            }
            List<String> backurlList = XPathUtil.getXpath("//input[@name='backurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backurlList)) {
                backurl = backurlList.get(0);
            }
            List<String> errorurlList = XPathUtil.getXpath("//input[@id='errorurl']/@value", pageContent);
            if (!CollectionUtils.isEmpty(errorurlList)) {
                errorurl = errorurlList.get(0);
            }
            List<String> spidList = XPathUtil.getXpath("//input[@name='spid']/@value", pageContent);
            if (!CollectionUtils.isEmpty(spidList)) {
                spid = spidList.get(0);
            }
            List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
            if (!CollectionUtils.isEmpty(relayStateList)) {
                relayState = relayStateList.get(1);
            }
            List<String> isValidateCodeList = XPathUtil.getXpath("//input[@id='isValidateCode']/@value", pageContent);
            if (!CollectionUtils.isEmpty(isValidateCodeList)) {
                isValidateCode = isValidateCodeList.get(0);
            }
            List<String> myactionList = XPathUtil.getXpath("//input[@id='myaction']/@value", pageContent);
            if (!CollectionUtils.isEmpty(myactionList)) {
                myaction = myactionList.get(0);
            }
            List<String> netactionList = XPathUtil.getXpath("//input[@id='netaction']/@value", pageContent);
            if (!CollectionUtils.isEmpty(netactionList)) {
                netaction = netactionList.get(0);
            }

            TaskUtils.addTaskShare(param.getTaskId(), "type", type);
            TaskUtils.addTaskShare(param.getTaskId(), "backurl", backurl);
            TaskUtils.addTaskShare(param.getTaskId(), "errorurl", errorurl);
            TaskUtils.addTaskShare(param.getTaskId(), "relayState", relayState);
            TaskUtils.addTaskShare(param.getTaskId(), "spid", spid);
            TaskUtils.addTaskShare(param.getTaskId(), "isValidateCode", isValidateCode);
            TaskUtils.addTaskShare(param.getTaskId(), "myaction", myaction);
            TaskUtils.addTaskShare(param.getTaskId(), "netaction", netaction);

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
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return validatePicCodeForLogin(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
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
            String templateUrl = "https://gx.ac.10086.cn/common/image.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "guang_xi_10086_web_003")
                    .setFullUrl(templateUrl).invoke();
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
            String templateUrl = "https://gx.ac.10086.cn/validImageCode?r_{}&imageCode={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "guang_xi_10086_web_004")
                    .setFullUrl(templateUrl, Math.random(), param.getPicCode()).invoke();
            if (response.getPageContent().contains("1")) {
                logger.info("登录-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->图片验证码-->校验失败,param={}", param);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            }
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = validatePicCodeForLogin(param);
        if (!result.getStatus()) {
            return result;
        }
        Response response = null;
        try {
            String templateUrl = "https://gx.ac.10086.cn/Login?type={}&backurl={}&errorurl={}&spid={}&RelayState={}&mobileNum={}&servicePassword" +
                    "={}&smsValidCode=&validCode={}&isValidateCode={}";
            String type = TaskUtils.getTaskShare(param.getTaskId(), "type");
            String backurl = TaskUtils.getTaskShare(param.getTaskId(), "backurl");
            String errorurl = TaskUtils.getTaskShare(param.getTaskId(), "errorurl");
            String spid = TaskUtils.getTaskShare(param.getTaskId(), "spid");
            String relayState = TaskUtils.getTaskShare(param.getTaskId(), "relayState");
            String isValidateCode = TaskUtils.getTaskShare(param.getTaskId(), "isValidateCode");

            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_005")
                    .setFullUrl(templateUrl, type, backurl, errorurl, spid, relayState, param.getMobile(), param.getPassword(), param.getPicCode(),
                            isValidateCode).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("replace\\('([^']+)'\\)")) {
                templateUrl = PatternUtils.group(response.getPageContent(), "replace\\('([^']+)'\\)", 1);
                response = TaskHttpClient.create(param, RequestType.GET, "guang_xi_10086_web_006").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }
            String samLart = PatternUtils.group(pageContent, "name=\"SAMLart\" value=\"([^\"]+)\"", 1);
            String relayValue = PatternUtils.group(pageContent, "name=\"RelayState\" value=\"([^\"]+)\"", 1);
            templateUrl = "https://gx.ac.10086.cn/4logingx/backPage.jsp?RelayState={}&SAMLart={}&displayPic=1";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_007").setFullUrl(templateUrl, relayValue, samLart).invoke();
            pageContent = response.getPageContent();

            String sAMLart = PatternUtils.group(pageContent, "callAssert\\('([^\"]+)'\\)", 1);
            templateUrl = "http://www.gx.10086.cn/wodeyidong/indexMyMob.jsp?SAMLart={}&RelayState={}&myaction={}&netaction={}";
            String myaction = TaskUtils.getTaskShare(param.getTaskId(), "myaction");
            String netaction = TaskUtils.getTaskShare(param.getTaskId(), "netaction");
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_008")
                    .setFullUrl(templateUrl, sAMLart, relayState, myaction, netaction).invoke();
            templateUrl = "http://www.gx.10086.cn/wodeyidong";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_009").setFullUrl(templateUrl).invoke();
            response = TaskHttpClient.create(param, RequestType.GET, "guang_xi_10086_web_010").setFullUrl(templateUrl).invoke();

            templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryexistbusi/QueryExistBusiAction/initBusi" +
                    ".menu?is_first_render=true&_menuId=410900003564&=&_lastCombineChild=false&_zoneId=busimain&_tmpDate=&_buttonId=";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_011").setFullUrl(templateUrl).invoke();

            templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryexistbusi/QueryExistBusiAction/queryBusi" +
                    ".menu?phoneId={}&numberType=1&selectedType=600&chooseNumType=1&is_first_render=true&_zoneId=ui-queryresult&_tmpDate=&_menuId=410900003564&_buttonId=step-1-btn";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_011").setFullUrl(templateUrl, param.getMobile()).invoke();
            if (response.getPageContent().contains("客户信息")) {
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
            String referer = "http://www.gx.10086.cn/wodeyidong/mymob/xiangdan.jsp";
            String templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryDetailInfo/QueryDetailInfoAction/initBusi" +
                    ".menu?is_first_render=true&_menuId=410900003558&=&_lastCombineChild=false&_zoneId=busimain&_tmpDate=&_buttonId=";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_004").setFullUrl(templateUrl).setReferer(referer).invoke();

            templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryDetailInfo/QueryDetailInfoAction/sendSecondPsw" +
                    ".menu?ajaxType=json&_tmpDate=&_menuId=410900003558&_buttonId=";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_012").setFullUrl(templateUrl).setReferer(referer).invoke();
            if (response.getPageContent().contains("随机短信验证码已发送成功")) {
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

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.gx.10086.cn/wodeyidong/ecrm/queryDetailInfo/QueryDetailInfoAction/checkSecondPsw" +
                    ".menu?input_random_code={}&input_svr_pass={}&is_first_render=true&_zoneId=_sign_errzone&_tmpDate=&_menuId=410900003558" +
                    "&_buttonId=other_sign_btn";
            response = TaskHttpClient.create(param, RequestType.POST, "guang_xi_10086_web_013")
                    .setFullUrl(templateUrl, param.getSmsCode(), param.getPassword()).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
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
}
