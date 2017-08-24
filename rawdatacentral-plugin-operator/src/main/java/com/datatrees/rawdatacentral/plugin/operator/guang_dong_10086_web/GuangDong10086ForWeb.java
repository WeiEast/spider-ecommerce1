package com.datatrees.rawdatacentral.plugin.operator.guang_dong_10086_web;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 广东移动
 * 登陆地址:http://gd.ac.10086.cn/login/
 * 登陆方式:短信验证码登陆
 * 短信验证码:支持
 * 登陆后需验证服务密码，获取后续查询权限
 * Created by guimeichao on 17/8/23.
 */
public class GuangDong10086ForWeb implements OperatorPluginService {

    private static final Logger       logger       = LoggerFactory.getLogger(GuangDong10086ForWeb.class);
    private static       RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //登陆页没有获取任何cookie,不用登陆
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={}", param, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshSmsCodeForLogin(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
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
        logger.warn("defineProcess fail,params={}", param);
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://gd.ac.10086.cn/ucs/ucs/getSmsCode.jsps";
            String templateData = "mobile={}";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10086_web_001").setFullUrl(templateUrl).setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();
            JSONObject json = response.getPageContentForJSON();
            String returnCode = json.getString("returnCode");
            if (StringUtils.equals("1000", returnCode)) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://gd.ac.10086.cn/ucs/ucs/webForm.jsps";
            String templateData = "mobile={}&smsPwd={}&loginType=1&cookieMobile=on&backURL=http://gd.10086.cn/commodity/index.shtml";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getSmsCode());
            response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10086_web_002").setFullUrl(templateUrl).setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();
            /**
             * 结果枚举:
             * 登陆成功:{"backUrl":"http:\/\/gd.10086.cn\/commodity\/index.shtml","failMsg":"成功[0]","returnCode":"1000"}
             * 短信验证码不正确:{"backUrl":"","failMsg":"动态密码错误[login.fail.wrong.password&1][9080010007]","returnCode":"9080010007"}
             */
            JSONObject json = response.getPageContentForJSON();
            String returnCode = json.getString("returnCode");
            if (StringUtils.equals("1000", returnCode)) {
                logger.info("登陆成功,param={}", param);

                /**
                 * 访问http://gd.10086.cn/commodity/servicio/nostandardserv/mobileInfoQuery/index.jsps?operaType=QUERY&servCode=MY_BASICINFO
                 * 获取校验服务密码请求所需要的参数
                 */
                templateUrl = "http://gd.10086.cn/commodity/servicio/nostandardserv/mobileInfoQuery/index.jsps?operaType=QUERY&servCode=MY_BASICINFO";
                response = TaskHttpClient.create(param, RequestType.GET, "guang_dong_10086_web_003").setFullUrl(templateUrl).invoke();

                /**
                 * 获取参数列表
                 * {"content":"https://gd.ac.10086.cn/ucs/ucs/second/login.jsps?reqType=0&saType=2&mobile=18219491713&channel=bsacNB&st=20170823161811Inq4n5rB49t4X5wXQn
                 * &sign=0395FAAC1D4BB52128746F49D8490EEECBD8640134B37AF081199EAD82A0C114BB018994CED92C26&token=5011430823161811R0ZUmFqgP88ZmWBI&appid=501143
                 * &backURL=http%3A%2F%2Fgd.10086.cn%2Fmy%2FmyService%2FmyBasicInfo.shtml","type":"ucs.client.error.unauthorized"}
                 *
                 * 校验请求的参数
                 * mobile=18219491713&serPwd=11223344&saType=2&channel=bsacNB&st=20170823161811Inq4n5rB49t4X5wXQn&sign=0395FAAC1D4BB52128746F49D8490EEECBD8640134B37AF081199EAD82A0C114BB018994CED92C26
                 * &token=5011430823161811R0ZUmFqgP88ZmWBI&appid=501143&backURL=http%3A%2F%2Fgd.10086.cn%2Fmy%2FmyService
                 * %2FmyBasicInfo.shtml
                 */
                templateUrl = "https://gd.ac.10086.cn/ucs/ucs/secondAuth.jsps";
                String pageContent = response.getPageContent();

                String saType = PatternUtils.group(pageContent, "saType\":\\s*\"([^\"]*)\"", 1);
                String channel = PatternUtils.group(pageContent, "channel\":\\s*\"([^\"]*)\"", 1);
                String st = PatternUtils.group(pageContent, "st\":\\s*\"([^\"]*)\"", 1);
                String sign = PatternUtils.group(pageContent, "sign\":\\s*\"([^\"]*)\"", 1);
                String token = PatternUtils.group(pageContent, "token\":\\s*\"([^\"]*)\"", 1);
                String appid = PatternUtils.group(pageContent, "appid\":\\s*\"([^\"]*)\"", 1);
                String backURL = PatternUtils.group(pageContent, "backURL\":\\s*\"([^\"]*)\"", 1);

                templateData = "mobile={}&serPwd={}&saType={}&channel={}&st={}&sign={}&token={}&appid={}&backURL=http://gd.10086.cn/my/myService/myBasicInfo.shtml";
                data = TemplateUtils.format(templateData, param.getMobile(), param.getPassword(), saType, channel, st, sign, token, appid, backURL);
                response = TaskHttpClient.create(param, RequestType.POST, "guang_dong_10086_web_004").setFullUrl(templateUrl).setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();

                json = response.getPageContentForJSON();
                returnCode = json.getString("returnCode");
                if (StringUtils.equals("1000", returnCode)) {
                    return result.success();
                }
                switch (returnCode) {
                    case "0337004003":
                        logger.warn("登录失败-->密码验证错误,param={}", param);
                        return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                    default:
                        logger.error("登陆失败,param={},data={},pageContent={}", param, data, response.getPageContent());
                        return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }
            switch (returnCode) {
                case "9080010007":
                    logger.warn("登录失败-->动态密码错误,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
