package com.datatrees.rawdatacentral.plugin.operator.tian_jin_10000_web;

import java.math.BigDecimal;
import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/10/10.
 */
public class TianJin10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(TianJin10000ForWeb.class);
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
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return validatePicCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
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

            String referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=02251357";
            String templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10002&toStUrl=http://tj.189.cn/tj/service/bill/feeQueryIndex.action?tab=3&fastcode=02251357";
            response = TaskHttpClient.create(param, RequestType.GET, "tian_jin_10000_web_002").setFullUrl(templateUrl).setReferer(referer).invoke();

            templateUrl = "http://www.189.cn/tj/";
            response = TaskHttpClient.create(param, RequestType.GET, "tian_jin_10000_web_003").setFullUrl(templateUrl).invoke();

            referer = "http://tj.189.cn/tj/service/bill/feeQueryIndex.action?tab=3&amp;fastcode=02251357";
            templateUrl = "http://tj.189.cn/tj/service/bill/feeQueryIndex.action?tab=tab3&time=";
            response = TaskHttpClient.create(param, RequestType.POST, "tian_jin_10000_web_004").setFullUrl(templateUrl, System.currentTimeMillis())
                    .setReferer(referer).invoke();

            templateUrl = "http://tj.189.cn/tj/service/bill/balanceQuery.action?requestFlag=asynchronism&shijian=";
            response = TaskHttpClient.create(param, RequestType.POST, "tian_jin_10000_web_005").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent)) {
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

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            BigDecimal db = new BigDecimal(Math.random() * (1 - 0) + 0);
            String referer = "http://tj.189.cn/tj/service/bill/detailBillQuery.action";
            String templateUrl = "http://tj.189.cn/tj/authImg?{}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "tian_jin_10000_web_006")
                    .setFullUrl(templateUrl, db.setScale(16, BigDecimal.ROUND_HALF_UP)).setReferer(referer).invoke();
            logger.info("详单-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://tj.189.cn/tj/service/bill/detailBillQuery.action";
            String templateUrl = "http://tj.189.cn/tj/checkrand/checkRand.action?randValue={}";
            response = TaskHttpClient.create(param, RequestType.POST, "tian_jin_10000_web_007").setFullUrl(templateUrl, param.getPicCode())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "\"ret\":0")) {
                logger.info("详单-->图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://tj.189.cn/tj/service/bill/detailBillQuery.action";
            String templateUrl = "http://tj.189.cn/tj/service/bill/sendRandomSmscode.action?randomMode=2&funcType=detail&checkCode={}";
            String picCode = param.getPicCode();
            if (StringUtils.isBlank(picCode)) {
                picCode = TaskUtils.getTaskShare(param.getTaskId(), "picCode");
            }
            //String templateData = "<buffalo-call><method>SendVCodeByNbr</method><string>{}</string></buffalo-call>";
            //String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "tian_jin_10000_web_008").setFullUrl(templateUrl, picCode).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "\"requestFlag\":\"success\"")) {
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
            String referer = "http://tj.189.cn/tj/service/bill/detailBillQuery.action";
            String templateUrl = "http://tj.189.cn/tj/service/bill/validateRandomcode.action?sRandomCode={}&randomMode=1&funcType=detail";
            response = TaskHttpClient.create(param, RequestType.POST, "tian_jin_10000_web_009").setFullUrl(templateUrl, param.getSmsCode())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "\"requestFlag\":\"success\"")) {
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
