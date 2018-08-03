package com.datatrees.rawdatacentral.plugin.operator.guang_dong_10000_wap;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.common.util.StringUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.plugin.OperatorPlugin;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.ibm.icu.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: yand
 * Date: 2017/11/20
 */
public class GuangDong10000ForWap implements OperatorPlugin {

    private static final Logger logger = LoggerFactory.getLogger(GuangDong10000ForWap.class);

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
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://wapgd.189.cn/nCheckCode";
            String referer = "http://wapgd.189.cn/login/other_phone_login.jsp?choose_v=3G";
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
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            // 归属地验证，获取区号
            String templateUrl = "http://gd.189.cn/J/J10138.j?a.c=0&a.u=user&a.p=pass&a.s=ECSS";
            String templateData = "d.d01=" + param.getMobile();
            String referer = "http://gd.189.cn/TS/cx/gsdcx.htm?cssid=sy-bmfw-gsdcx";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(templateData).setReferer(referer).invoke();
            JSONObject json = response.getPageContentForJSON();
            String r01 = (String) JSONPath.eval(json, "$.r.r01");
            if (StringUtils.isBlank(r01)) {
                logger.error("input mobile error!非广东手机号");
                return result.failure(ErrorCode.LOGIN_ERROR);
            }
            //登录
            templateUrl = "https://wapgd.189.cn/OtherPhoneLogin.do";
            templateData = "code=" + param.getPicCode() + "&data=" + param.getMobile() + "&getpwdurl=%2Flogin%2Fget_login_code.jsp&latn_id=" + r01 +
                    "&loginOldUri=%2Flogin%2Fother_phone_login.jsp&originalURL=null&password=" + param.getPassword() +
                    "&pwdtype_name=%E6%98%BE%E7%A4%BA%E5%AF%86%E7%A0%81&search=%20%E7%99%BB%20%E5%BD%95%20";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(templateData).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent) || pageContent.contains("请输入您要登录的手机号码")) {
                if (pageContent.contains("提示")) {
                    String errorMsg = PatternUtils.group(pageContent, "提示：([^<]+)", 1);
                    logger.error("登陆失败,param={},response={},errorMsg={}", param, response, errorMsg);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        try {
            String templateUrl = "http://wapgd.189.cn/Querylogin.do?originalURL=/Querylogin.do";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            String servId = XPathUtil.getXpath("//input[@name='servId']/@value", pageContent).get(0);

            templateUrl = "http://wapgd.189.cn/getPassWordCode.do";
            String templateData = "billType=callW&qryType=1&searchDateStr=" + simpleDateFormat.format(new Date()) + "&sl_day=1&nbrNo=" +
                    param.getMobile() + "&servId=" + servId + "&Icode=&refreshCode=%E8%8E%B7%E5%8F%96%E7%9F%AD%E4%BF%A1%E9%AA%8C%E8%AF%81%E7%A0%81";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(templateData).invoke();
            pageContent = response.getPageContent();
            if (pageContent.contains("短信验证码已成功发送")) {
                TaskUtils.addTaskShare(param.getTaskId(), "servId", servId);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        try {
            String servId = TaskUtils.getTaskContext(param.getTaskId(), "servId");
            String templateUrl = "http://wapgd.189.cn/cloudbill/qryCloudbill.action";
            String templateData = "billType=callW&qryType=1&searchDateStr=" + simpleDateFormat.format(new Date()) + "&sl_day=1&nbrNo=" +
                    param.getMobile() + "&servId=" + servId + "&Icode=" + param.getSmsCode();
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(templateData).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("没有您要查询的数据") || pageContent.contains("语音通话详单")) {
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
