package com.datatrees.rawdatacentral.plugin.operator.jiang_su_10000_web;

import java.util.List;
import java.util.Map;

import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.operator.domain.model.FormType;
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
 * Created by guimeichao on 17/9/18.
 */
public class JiangSu10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger     = LoggerFactory.getLogger(JiangSu10000ForWeb.class);
    private              LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            TaskHttpClient.create(param, RequestType.GET, "jiang_su_10000_web_001").setFullUrl("http://js.189.cn/nservice/login/toLogin").invoke();
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
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://js.189.cn/nservice/login/toLogin";
            String templateUrl = "http://js.189.cn/nservice/verication/getCodeImage";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "jiang_su_10000_web_002")
                    .setFullUrl(templateUrl).setReferer(referer).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://js.189.cn/nservice/login/toLogin";
            String templateUrl = "http://js.189.cn/nservice/login/doLogin?menuType=0";
            String templteData = "userType=2000004&logonPattern=1&favurl=&newUamType=-1&productId={}&userPwd={}&validateCodeNumber={}";
            String data = TemplateUtils.format(templteData, param.getMobile(), param.getPassword(), param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "jiang_su_10000_web_003").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            String errorMsg = null;
            List<String> errorMsgList = XPathUtil.getXpath("span#showTimeMsgPupup/text()", pageContent);
            if (!errorMsgList.isEmpty()) {
                errorMsg = errorMsgList.get(0);
            }
            if (StringUtils.contains(pageContent, "已登录") && StringUtils.isBlank(errorMsg)) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else if (StringUtils.isNotBlank(errorMsg)) {
                logger.error("登陆失败,param={},errorMsg={}", param, errorMsg);
                return result.failure(errorMsg);
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
