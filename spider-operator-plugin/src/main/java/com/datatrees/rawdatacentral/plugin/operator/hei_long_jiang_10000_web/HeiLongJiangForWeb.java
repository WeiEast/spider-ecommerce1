package com.datatrees.rawdatacentral.plugin.operator.hei_long_jiang_10000_web;

import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
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
public class HeiLongJiangForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(HeiLongJiangForWeb.class);
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

            String templateUrl = "http://www.189.cn/hl/";
            response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_002").setFullUrl(templateUrl).invoke();

            String referer = "http://www.189.cn/hl/";
            templateUrl = "http://www.189.cn/dqmh/cms/index/login_jx.jsp?ifindex=index";
            response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_003").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            templateUrl = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00520481";
            response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_004").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();

            referer = templateUrl;
            templateUrl
                    = "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10010&toStUrl=http://hl.189.cn/service/zzfw.do?method=fycx&id=6&fastcode=00520481";
            response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_005").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            templateUrl = "http://hl.189.cn/service/zzfw.do?method=fycx&id=6&fastcode=00520481";
            response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_006").setFullUrl(templateUrl).invoke();

            referer = templateUrl;
            templateUrl = "http://hl.189.cn/service/selectBallance.do?method=ballance";
            response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_007").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent) && !StringUtils.contains(pageContent, param.getMobile().toString())) {
                referer = "http://hl.189.cn/service/selectBallance.do?method=ballance";
                templateUrl
                        = "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10010&toStUrl=http://hl.189.cn/service/selectBallance.do?method=ballance";
                response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_008").setFullUrl(templateUrl).setReferer(referer)
                        .invoke();
                templateUrl = "http://hl.189.cn/service/selectBallance.do?method=ballance";
                response = TaskHttpClient.create(param, RequestType.GET, "hei_long_jiang_10000_web_009").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
            }

            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
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
            String referer = "http://hl.189.cn/service/zzfw.do?method=fycx&id=10&fastcode=00520485&cityCode=hl";
            String templateUrl = "http://hl.189.cn/service/userCheck.do?method=sendMsg";
            response = TaskHttpClient.create(param, RequestType.POST, "hei_long_jiang_10000_web_010").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "1")) {
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
            String referer = "ttp://hl.189.cn/service/zzfw.do?method=fycx&id=10&fastcode=00520485&cityCode=hl";
            String templateUrl = "http://hl.189.cn/service/zzfw.do?method=checkDX&yzm={}";
            response = TaskHttpClient.create(param, RequestType.POST, "hei_long_jiang_10000_web_011").setFullUrl(templateUrl, param.getSmsCode())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent)) {
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
