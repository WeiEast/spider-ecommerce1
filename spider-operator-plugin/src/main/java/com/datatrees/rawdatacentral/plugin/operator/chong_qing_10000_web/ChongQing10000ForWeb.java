package com.datatrees.rawdatacentral.plugin.operator.chong_qing_10000_web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.datatrees.spider.share.service.utils.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/10/9.
 */
public class ChongQing10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(ChongQing10000ForWeb.class);

    private              LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCode(param);
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

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }

            String referer = "http://cq.189.cn/account/index.htm";
            String templateUrl = "http://www.189.cn/dqmh/my189/checkMy189Session.do?fastcode=02031273";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            referer = templateUrl;
            templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10004&toStUrl=http://cq.189.cn/new-bill/bill_xd?fastcode=02031273&cityCode=cq";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            templateUrl = "http://cq.189.cn/new-bill/bill_XDCX?accNbr={}&productId=208511296&billingModeId=2100";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl, param.getMobile())
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "语音详单")) {
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
            String referer = "http://cq.189.cn/new-bill/bill_xd?fastcode=02031273&cityCode=cq&ticket=";
            String templateUrl = "http://cq.189.cn/new-bill/bill_DXYZM";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "0")) {
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
            String realName = param.getRealName();
            String halfName = StringUtils.EMPTY;
            if (realName.length() == 4) {
                halfName = StringUtils.substring(realName, 1, 3);
            } else {
                halfName = StringUtils.substring(realName, 1);
            }
            String last6Id = StringUtils.right(param.getIdCard(), 6);
            last6Id = last6Id.toUpperCase();
            TaskUtils.addTaskShare(param.getTaskId(), "halfName", halfName);
            TaskUtils.addTaskShare(param.getTaskId(), "last6Id", last6Id);

            String referer = "http://cq.189.cn/new-bill/bill_xd?fastcode=02031273&cityCode=cq";
            String templateUrl = "http://cq.189.cn/new-bill/bill_SMZ";
            String templateData = "tname={}";
            String data = TemplateUtils.format(templateData, URLEncoder.encode(halfName, "UTF-8"));
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "xm\":\"1\"")) {
                logger.error("详单-->校验失败,校验姓名失败,param={},realName={},halfName={},pageContent={}", param, realName, halfName,
                        response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_FAIL);
            }

            templateUrl = "http://cq.189.cn/new-bill/bill_SMZ";
            templateData = "idcard={}";
            data = TemplateUtils.format(templateData, last6Id);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "sfz\":\"2\"")) {
                logger.error("详单-->校验失败,校验身份证失败,param={},idCard={},last6Id={},pageContent={}", param, param.getIdCard(), last6Id,
                        response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_FAIL);
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            String month = format.format(new Date());
            templateUrl = "http://cq.189.cn/new-bill/bill_XDCXNR";
            templateData = "accNbr={}&productId=208511296&month={}&callType=00&listType=300001&beginTime" +
                    "={}-01&endTime={}-28&rc={}&tname={}&idcard={}&zq=2";
            data = TemplateUtils
                    .format(templateData, param.getMobile(), month, month, month, param.getSmsCode(), URLEncoder.encode(halfName, "UTF-8"), last6Id);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "对不起，没有查到您的清单数据") || StringUtils.contains(pageContent, "费用") ||
                    StringUtils.contains(pageContent, "\"result\":\"0\"")) {
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
