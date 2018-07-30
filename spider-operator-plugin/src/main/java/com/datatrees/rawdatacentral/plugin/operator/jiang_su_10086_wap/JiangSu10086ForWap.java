package com.datatrees.rawdatacentral.plugin.operator.jiang_su_10086_wap;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.DateUtils;
import com.datatrees.spider.share.common.utils.RegexpUtils;
import com.datatrees.spider.share.common.http.ScriptEngineUtil;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 江苏移动--wap
 * 登陆地址:http://wap.js.10086.cn/login.thtml
 * 登陆(服务密码登陆):手机号,服务密码,图片验证码(不支持验证)
 * 详单:短信验证码
 * Created by zhouxinghai on 2017/8/28
 */
public class JiangSu10086ForWap implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(JiangSu10086ForWap.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //获取imgReqSeq
            Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl("http://wap.js.10086.cn/login.thtml").invoke();
            String pageContent = response.getPageContent();
            String imgReqSeq = RegexpUtils.select(pageContent, "id=\\\\\"imgReqSeq\\\\\" value=\\\\\"(.+?)\\\\\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "imgReqSeq", imgReqSeq);
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
            String imgReqSeq = TaskUtils.getTaskShare(param.getTaskId(), "imgReqSeq");
            String templateUrl = "http://wap.js.10086.cn/imageVerifyCode.do?t=0.{}&imgReqSeq={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis(), imgReqSeq)
                    .addExtralCookie("wap.js.10086.cn", "mywaytoopen", "18f7a4cd2b229eb8adbcce7be537c59f79").invoke();
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
            // TODO: 2017/8/28 验证码在5分钟内有效，3次输入错误后失效。
            String templateUrl = "http://wap.js.10086.cn/actionDispatcher.do?reqUrl=smsVerifyCode&busiNum=QDCX";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();
            JSONObject json = response.getPageContentForJSON();
            if (json.getBoolean("success")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            }
            String logicCode = json.getString("logicCode");
            switch (logicCode) {
                default:
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
            String queryMonth = DateUtils.format(new Date(), "yyyyMM");
            String templateUrl = "http://wap.js.10086.cn/actionDispatcher" +
                    ".do?reqUrl=billDetailTQry&busiNum=QDCX&currentPage=1&queryMonth={}&password_str=&ver=t&queryItem=1&browserFinger" +
                    "=&confirm_smsPassword={}&confirmFlg=1";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, queryMonth, param.getSmsCode()).invoke();
            String pageContent = response.getPageContent();
            JSONObject json = response.getPageContentForJSON();
            if (json.getBoolean("success")) {
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

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {

            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("encryptByDES", param.getPassword()).toString();

            String imgReqSeq = TaskUtils.getTaskShare(param.getTaskId(), "imgReqSeq");

            String templateUrl = "http://wap.js.10086.cn/actionDispatcher" +
                    ".do?reqUrl=loginTouch&busiNum=login&mobile={}&password={}&isSavePasswordVal=1&verifyCode={}&isSms=0&ver=t&imgReqSeq" +
                    "={}&loginType=0&mywaytoopen=18f7a4cd2b229eb8adbcce7be537c59f79&browserUA=Mozilla%2F5.0+" +
                    "(Macintosh%3B+Intel+Mac+OS+X+10.13%3B+rv%3A52.0)+Gecko%2F20100101+Firefox%2F52.0";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, param.getMobile(), URLEncoder.encode(encryptPassword, "UTF-8"), param.getPicCode(), imgReqSeq)
                    .addHeader("hgvhv", "18f7a4cd2b229eb8adbcce7be537c59f79").invoke();
            JSONObject json = response.getPageContentForJSON();
            if (json.getBoolean("success")) {
                logger.warn("登录成功,params={}", param);
                return result.success();
            }
            String logicCode = json.getString("logicCode");
            switch (logicCode) {
                case "-4003":
                    logger.warn("登录失败-->图片验证码校验失败!param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
                default:
                    logger.warn("登录失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

}
