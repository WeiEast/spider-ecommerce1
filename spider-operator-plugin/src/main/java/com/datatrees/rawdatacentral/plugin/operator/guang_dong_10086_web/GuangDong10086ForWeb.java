package com.datatrees.rawdatacentral.plugin.operator.guang_dong_10086_web;

import javax.script.Invocable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.format.unit.TimeUnit;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.ScriptEngineUtil;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.gson.reflect.TypeToken;
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

    private static final Logger logger = LoggerFactory.getLogger(GuangDong10086ForWeb.class);

    public static byte[] unZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            logger.info("解压缩出错", ex);
        }
        return b;
    }

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            //登陆页没有获取任何cookie,不用登陆
            String templateUrl = "https://gd.ac.10086.cn/ucs/ucs/weblogin.jsps?backURL=http://gd.10086.cn/commodity/index.shtml";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
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
        switch (param.getFormType()) {
            case "CALL_DETAILS":
                return processForDetails(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://gd.ac.10086.cn/ucs/ucs/getSmsCode.jsps";
            String templateData = "mobile={}";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();
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
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();
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
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

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
                String pageContent = response.getPageContent();

                String saType = PatternUtils.group(pageContent, "saType\":\\s*\"([^\"]*)\"", 1);
                String channel = PatternUtils.group(pageContent, "channel\":\\s*\"([^\"]*)\"", 1);
                String st = PatternUtils.group(pageContent, "st\":\\s*\"([^\"]*)\"", 1);
                String sign = PatternUtils.group(pageContent, "sign\":\\s*\"([^\"]*)\"", 1);
                String token = PatternUtils.group(pageContent, "token\":\\s*\"([^\"]*)\"", 1);
                String appid = PatternUtils.group(pageContent, "appid\":\\s*\"([^\"]*)\"", 1);
                String backURL = PatternUtils.group(pageContent, "backURL\":\\s*\"([^\"]*)\"", 1);

                templateUrl = "https://gd.ac.10086.cn/ucs/ucs/decryptToken/generate.jsps";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
                json = response.getPageContentForJSON();

                String decryptToken = json.getString("decryptToken");
                String publicKey = json.getString("publicKey");
                Invocable invocable = ScriptEngineUtil.createInvocable("guang_dong_10086_web", "des.js", "GBK");
                String encryptPassword = invocable.invokeFunction("encryptData", param.getPassword(), decryptToken, publicKey).toString();

                templateUrl = "https://gd.ac.10086.cn/ucs/ucs/secondAuth.jsps";
                templateData
                        = "mobile={}&encryptItems={}&saType={}&channel={}&st={}&sign={}&token={}&appid={}&backURL=http://gd.10086.cn/my/myService/myBasicInfo.shtml";
                data = TemplateUtils.format(templateData, param.getMobile(), encryptPassword, saType, channel, st, sign, token, appid, backURL);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();
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

    private HttpResult<Object> processForDetails(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();

        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String[] times = paramMap.get("page_content").split(":");

        Response response = null;
        try {
            /**
             * 获取通话记录
             */
            String templateUrl = "http://gd.10086.cn/commodity/servicio/nostandardserv/realtimeListSearch/downLoad.jsps";
            String templateData = "downloadBeginTime={}000000&downloadEneTime={}235959&downloadType=1&uniqueTagDown=";
            String data = TemplateUtils.format(templateData, times[1], times[2]);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            String pageContent = new String(response.getResponse(), "GBK");
            String checkPageContent = new String(response.getResponse(), "UTF-8");
            if (StringUtils.contains(checkPageContent, "发生错误")) {
                TimeUnit.SECOND.toMillis(1);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                        .setRequestBody(data).invoke();
                pageContent = new String(response.getResponse(), "GBK");
                checkPageContent = new String(response.getResponse(), "UTF-8");
                logger.info("发生错误,重试一下!pageContent={},checkPageContent={},taskId={}", pageContent, checkPageContent, param.getTaskId());
            }
            if (StringUtils.contains(checkPageContent, "发生错误")) {
                logger.info("依然发生错误，继续查询下一个月,taskId={}", param.getTaskId());
                return result.success(pageContent);
            }
            if (!StringUtils.contains(pageContent, "清单数据")) {
                logger.info("当前附件为压缩文件，需先解压缩，taskId={}", param.getTaskId());
                pageContent = new String(unZip(response.getResponse()), "GBK");
            }
            if (!StringUtils.contains(pageContent, "清单数据")) {
                logger.error("详单依然乱码,taskid={},通话详单byte数组={}", param.getTaskId(), response.getResponse());
            }
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("通话记录页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
}
