package com.datatrees.spider.bank.plugin.spdb.com.cn;

import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.share.service.plugin.CommonPlugin;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 2018/8/13.
 */
public class SPDBPlugin implements CommonPlugin {

    private static final Logger logger = LoggerFactory.getLogger(SPDBPlugin.class);

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return new HttpResult<>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Object> refeshSmsCodeForBillDetail(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        String seedurl = TaskUtils.getTaskContext(param.getTaskId(), "seedurl");
        Response response = null;
        try {
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(seedurl).invoke();
            String requestSmsUrl = "https://ebill.spdbccc.com.cn/cloudbank-portal/loginController/sendStatisticalCode.action";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(requestSmsUrl)
                    .setReferer(seedurl).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "\"msg\":\"0000\"")) {
                logger.info("浦发网银-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("浦发网银-->短信验证码-->刷新失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("浦发网银-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Object> submitForBillDetail(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        String seedurl = TaskUtils.getTaskContext(param.getTaskId(), "seedurl");
        Response response = null;
        try {
            String requestUrl = "https://ebill.spdbccc.com.cn/cloudbank-portal/loginController/checkRandomStcCode.action";
            String templateData = "randomCode={}";
            String data = TemplateUtils.format(templateData, param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(requestUrl).setRequestBody(data)
                    .setReferer(seedurl).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            String pageContent = response.getPageContent();
            if ((StringUtils.contains(pageContent, "\"msg\":\"0000\"")) || (StringUtils.contains(pageContent, "\"msg\":\"0001\""))) {
                logger.info("浦发网银-->校验成功,param={}", param);
                return result.success();
            }
            logger.error("浦发网银-->校验失败,param={},pageContent={}", param, pageContent);
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("浦发网银-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}