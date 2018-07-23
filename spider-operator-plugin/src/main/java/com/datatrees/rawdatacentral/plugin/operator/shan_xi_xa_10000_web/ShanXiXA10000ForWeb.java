package com.datatrees.rawdatacentral.plugin.operator.shan_xi_xa_10000_web;

import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/25.
 */
public class ShanXiXA10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(ShanXiXA10000ForWeb.class);
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
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }

            String referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000197";
            String templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10027&toStUrl=http://sn.189.cn/service/bill/fee.action?type=resto&fastcode=10000197&cityCode=sn";
            response = TaskHttpClient.create(param, RequestType.GET, "shan_xi_xa_10000_web_002").setFullUrl(templateUrl).setReferer(referer).invoke();

            templateUrl = "http://sn.189.cn/service/bill/initQueryBill.action?rnd={}";
            response = TaskHttpClient.create(param, RequestType.GET, "shan_xi_xa_10000_web_003")
                    .setFullUrl(templateUrl, (int) (Math.random() * 1000000)).invoke();
            String pageContent = response.getPageContent();
            String areacode = PatternUtils.group(pageContent, "areacode=(\\d+)&amp;", 1);
            String productid = PatternUtils.group(pageContent, "productid=(\\d+)\"", 1);
            if (StringUtils.isBlank(areacode) || StringUtils.isBlank(productid)) {
                logger.warn("登录失败,areacode或productid为空,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "areacode", areacode);
            TaskUtils.addTaskShare(param.getTaskId(), "productid", productid);

            referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=10000197";
            templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10027&toStUrl=http://sn.189.cn/service/bill/fee.action?type=resto&fastcode=10000197&cityCode=sn";
            response = TaskHttpClient.create(param, RequestType.GET, "shan_xi_xa_10000_web_004").setFullUrl(templateUrl).setReferer(referer).invoke();

            templateUrl = "http://sn.189.cn/service/bill/resto.action?rnd={}";
            response = TaskHttpClient.create(param, RequestType.GET, "shan_xi_xa_10000_web_005")
                    .setFullUrl(templateUrl, (int) (Math.random() * 1000000)).invoke();

            referer = "http://sn.189.cn/service/manage/myProducts.action?fastcode=10000195&cityCode=sn";
            templateUrl = "http://sn.189.cn/service/manage/offerListView.action?currentPage=1";
            response = TaskHttpClient.create(param, RequestType.POST, "shan_xi_xa_10000_web_006").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            if (StringUtils.isNotBlank(response.getPageContent())) {
                logger.warn("登录成功,params={}", param);
                return result.success();
            } else {
                logger.warn("登录失败,param={},response={}", param, response);
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
            String referer = "http://sn.189.cn/service/bill/fee.action?type=allDetails&fastcode=10000203&cityCode=sn";
            String templateUrl = "http://sn.189.cn/service/bill/sendInternetRandom.action?mobileNum={}";
            response = TaskHttpClient.create(param, RequestType.POST, "shan_xi_xa_10000_web_007").setFullUrl(templateUrl, param.getMobile())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "随机码发送成功")) {
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
            String referer = "http://sn.189.cn/service/bill/fee.action?type=allDetails&fastcode=10000203&cityCode=sn";
            String templateUrl = "http://sn.189.cn/service/bill/validInternet.action?mobileNum={}&rondomCode={}&_={}";
            response = TaskHttpClient.create(param, RequestType.POST, "shan_xi_xa_10000_web_008")
                    .setFullUrl(templateUrl, param.getMobile(), param.getSmsCode(), System.currentTimeMillis()).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "随机码验证成功")) {
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
