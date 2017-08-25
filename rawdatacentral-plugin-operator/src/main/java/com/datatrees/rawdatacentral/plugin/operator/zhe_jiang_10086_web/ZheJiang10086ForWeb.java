package com.datatrees.rawdatacentral.plugin.operator.zhe_jiang_10086_web;

import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.JsoupXpathUtils;
import com.datatrees.rawdatacentral.common.utils.RegexpUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中国移动--全国通用
 * 登陆地址:http://www.zj.10086.cn/my/login/login.jsp?AISSO_LOGIN=true&jumpurl=http://www.zj.10086.cn/my/index.jsp?ul_loginclient=my
 * 登陆(服务密码登陆):手机号,服务密码,图片验证码(不支持验证)
 * 详单:短信验证码
 * Created by zhouxinghai on 2017/8/25.
 */
public class ZheJiang10086ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(ZheJiang10086ForWeb.class);

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
            String templateUrl = "https://zj.ac.10086.cn/ImgDisp?tmp={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "zhe_jiang_10086_web_001")
                    .setResponseCharset("BASE64").setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        String pageContent = null;
        try {
            String templateUrl = null;
            String bid = TaskUtils.getTaskShare(param.getTaskId(), "bid");
            if (StringUtils.isBlank(bid)) {
                templateUrl = "http://service.zj.10086.cn/yw/detail/queryHisDetailBill.do?menuId=13009";
                response = TaskHttpClient.create(param, RequestType.GET, "china_10086_shop_008").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
                if (!StringUtils.contains(pageContent, "authnrequestform")) {
                    return result.failure(ErrorCode.REFESH_SMS_FAIL);
                }
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_002", pageContent, null);

                if (!StringUtils.contains(pageContent, "authnrequestform")) {
                    return result.failure(ErrorCode.REFESH_SMS_FAIL);
                }
                pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_002", pageContent, "GBK");

                bid = RegexpUtils.select(pageContent, "\"bid\":\"(.*)\"", 1);
                if (StringUtils.isBlank(bid)) {
                    logger.warn("详单-->短信验证码-->刷新失败,bid not found,param={},pateContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.REFESH_SMS_FAIL);
                }
                TaskUtils.addTaskShare(param.getTaskId(), "bid", bid);
            }
            logger.info("frefeshSmsCodeForBillDetail find bid={},param={}", bid, param);

            templateUrl = "http://service.zj.10086.cn/yw/detail/secondPassCheck.do?bid={}";
            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_shop_008").setFullUrl(templateUrl, bid).invoke();
            pageContent = response.getPageContent();
            switch (pageContent) {
                case "1":
                    logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                default:
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
            String bid = TaskUtils.getTaskShare(param.getTaskId(), "bid");
            String templateUrl = "http://service.zj.10086.cn/yw/detail/secondPassCheck.do?validateCode={}&bid={}";
            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_shop_008").setFullUrl(templateUrl, param.getSmsCode(), bid)
                    .invoke();
            String pageContent = response.getPageContent();
            switch (pageContent) {
                case "12":
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                default:
                    logger.error("详单-->校验失败,param={},pateContent={}", param, pageContent);
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://zj.ac.10086.cn/loginbox?billId={}&continue=http://www.zj.10086.cn/my/index" +
                    ".jsp?ul_loginclient=my&failurl=/login/login.jsp&loginUserType=1&passwd={}&pwdType=2&service=my&validCode={}";
            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10086_web_002")
                    .setFullUrl(templateUrl, param.getMobile(), param.getPassword(), param.getPicCode()).invoke();

            String pageContent = response.getPageContent();
            if (!StringUtils.contains(pageContent, "authnresponseform")) {
                logger.warn("登录失败,response not contains authnresponseform ,params={}", param);
                return result.failure();
            }
            pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_002", pageContent, null);
            if (!StringUtils.contains(pageContent, "authnrequestform")) {
                logger.warn("登录失败,response not contains authnrequestform ,params={}", param);
                return result.failure();
            }
            pageContent = executeScriptSubmit(param.getTaskId(), param.getWebsiteName(), "zhe_jiang_10086_web_002", pageContent, null);
            if (!StringUtils.contains(pageContent, "/my/index.do")) {
                logger.warn("登录失败,response not contains /my/index.do ,params={}", param);
                return result.failure();
            }

            TaskHttpClient.create(param, RequestType.GET, "zhe_jiang_10086_web_002")
                    .setFullUrl("http://www.zj.10086.cn/my/index.do?ul_loginclient=my").setResponseCharset("GBK").invoke();
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    /**
     * 处理跳转服务
     * @param pageContent
     * @return
     */
    private String executeScriptSubmit(Long taskId, String websiteName, String remark, String pageContent, String responseCharsetName) {
        String action = JsoupXpathUtils.selectFirst(pageContent, "//form/@action");
        String method = JsoupXpathUtils.selectFirst(pageContent, "//form/@method");
        List<Map<String, String>> list = JsoupXpathUtils.selectAttributes(pageContent, "//input");
        StringBuilder fullUrl = new StringBuilder(action);
        if (StringUtils.contains(fullUrl, "?")) {
            if (!StringUtils.endsWith(fullUrl, "?")) {
                fullUrl.append("&");
            }
        } else {
            fullUrl.append("?");
        }
        if (null != list && !list.isEmpty()) {
            for (Map<String, String> map : list) {
                if (map.containsKey("name") && map.containsKey("value")) {
                    fullUrl.append(map.get("name")).append("=").append(map.get("value")).append("&");
                }
            }
        }
        responseCharsetName = StringUtils.isBlank(responseCharsetName) ? "UTF-8" : responseCharsetName;
        String url = fullUrl.substring(0, fullUrl.length() - 1);
        RequestType requestType = StringUtils.equalsIgnoreCase("post", method) ? RequestType.POST : RequestType.GET;
        Response response = TaskHttpClient.create(taskId, websiteName, requestType, remark).setFullUrl(url).setResponseCharset(responseCharsetName)
                .invoke();
        return response.getPageContent();
    }

}
