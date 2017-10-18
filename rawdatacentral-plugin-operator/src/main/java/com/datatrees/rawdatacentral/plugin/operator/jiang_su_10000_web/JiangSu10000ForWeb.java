package com.datatrees.rawdatacentral.plugin.operator.jiang_su_10000_web;

import javax.script.Invocable;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
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
 * Created by guimeichao on 17/9/18.
 */
public class JiangSu10000ForWeb implements OperatorPluginService {

    private static final Logger logger     = LoggerFactory.getLogger(JiangSu10000ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();
            result = loginUtils.init(param);
            if (!result.getStatus()) {
                return result;
            }
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
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
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
        LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();
        HttpResult<String> result = loginUtils.refeshPicCode(param);
        return result;
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
           LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();
           result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }
            String referer = "http://www.189.cn/js/";
            String templateUrl = "http://www.189.cn/login/skip/uam.do?method=skip&shopId=10011&toStUrl=http://js.189.cn/service/bill?tabFlag=billing4";
            response = TaskHttpClient.create(param, RequestType.GET, "jiang_su_10000_web_002").setFullUrl(templateUrl).setReferer(referer).invoke();

            referer = "http://js.189.cn/service/bill?tabFlag=billing4";
            templateUrl = "http://js.189.cn/getSessionInfo.action";
            response = TaskHttpClient.create(param, RequestType.POST, "jiang_su_10000_web_003").setFullUrl(templateUrl).setReferer(referer).invoke();

            if (StringUtils.contains(response.getPageContent(), param.getMobile().toString())) {
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
}
