package com.datatrees.spider.operator.plugin.china_10086_wap;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.plugin.OperatorPlugin;
import com.datatrees.spider.share.common.http.ScriptEngineUtil;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.http.Response;
import com.google.gson.reflect.TypeToken;
import com.treefinance.toolkit.util.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 2018/7/25.
 */
public class China10086ForWap implements OperatorPlugin {

    private static final Logger  logger       = LoggerFactory.getLogger(China10086ForWap.class);

    private static final String  URL          = "https://10086.online-cmcc.cn:20010/gfms/front/ge/general!execute";

    //这个是最小的通道
    private              Integer minChannelID = 12014;

    //这个之后没试过
    private              Integer maxChannelID = 12014;

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //短信有效通道:12002-1208
            //登陆页没有获取任何cookie,不用登陆
            TaskUtils.addTaskShare(param.getTaskId(), "channelID", minChannelID.toString());
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
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(param);
            case FormType.VALIDATE_USER_INFO:
                return refeshSmsCodeForUserInfo(param);
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
            case FormType.VALIDATE_USER_INFO:
                return submitForUserInfo(param);
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
            case "BILL_DETAILS":
                return processForBill(param);
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
            Integer channelID = Integer.valueOf(TaskUtils.getTaskShare(param.getTaskId(), "channelID"));
            String templateUrl = "https://login.10086.cn/sendRandomCodeAction.action?type=01&channelID={}&userName={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, channelID, param.getMobile()).invoke();
            switch (response.getPageContent()) {
                case "0":
                    logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                case "1":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，短信随机码暂时不能发送，请一分钟以后再试,param={}", param);
                    return result.failure(ErrorCode.REFESH_SMS_FAIL, "对不起,短信随机码暂时不能发送，请一分钟以后再试");
                case "2":
                    logger.warn("登录-->短信验证码-->刷新失败,短信下发数已达上限，您可以使用服务密码方式登录,param={}", param);
                    if (channelID <= maxChannelID) {
                        channelID++;
                        TaskUtils.addTaskShare(param.getTaskId(), "channelID", channelID.toString());
                        return refeshSmsCodeForLogin(param);
                    }
                    return result.failure(ErrorCode.REFESH_SMS_FAIL, "短信下发数已达上限");
                case "3":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，短信发送次数过于频繁,param={}", param);
                    return result.failure(ErrorCode.REFESH_SMS_FAIL, "对不起，短信发送次数过于频繁");
                case "4":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，渠道编码不能为空,param={}", param);
                    return result.failure(ErrorCode.REFESH_SMS_FAIL);
                case "5":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，渠道编码异常,param={}", param);
                    return result.failure(ErrorCode.REFESH_SMS_FAIL);
                case "4005":
                    logger.warn("登录-->短信验证码-->刷新失败,手机号码有误，请重新输入,param={}", param);
                    return result.failure(ErrorCode.REFESH_SMS_FAIL, "手机号码有误，请重新输入");
                default:
                    logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
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
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPwd = invocable.invokeFunction("encrypt", param.getSmsCode()).toString();
            String templateUrl = "https://login.10086.cn/login" +
                    ".htm?accountType=01&pwdType=02&account={}&password={}&inputCode=&backUrl=https%3A%2F%2Ftouch.10086.cn%2Fi%2F&rememberMe=0" +
                    "&channelID={}&protocol=https%3A&timestamp={}";
            //没有referer提示:Connection reset
            String referer = "https://login.10086.cn/html/login/touch.html";
            String channelID = TaskUtils.getTaskShare(param.getTaskId(), "channelID");
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, param.getMobile(), URLEncoder.encode(encryptPwd, "UTF-8"), channelID, System.currentTimeMillis())
                    .setReferer(referer).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            /**
             * 结果枚举:
             * 登陆成功:{"artifact":"3490872f8d114992b44dc4e60f595fa0","assertAcceptURL":"http://shop.10086.cn/i/v1/auth/getArtifact"
             ,"code":"0000","desc":"认证成功","islocal":false,"provinceCode":"371","result":"0","uid":"b73f1d1210d94fadaf4ba9ce8c49aef1"
             }这里的provinceCode可能会没有
             短信验证码过期:{"code":"6001","desc":"短信随机码不正确或已过期，请重新获取","islocal":false,"result":"8"}
             短信验证码不正确:{"code":"6002","desc":"短信随机码不正确或已过期，请重新获取","islocal":false,"result":"8"}
             {"assertAcceptURL":"http://shop.10086.cn/i/v1/auth/getArtifact","code":"2036","desc":"您的账户名与密码不匹配，请重
             新输入","islocal":false,"result":"2"}
             重复登陆:{"islocal":false,"result":"9"}
             */
            logger.info("帮助查询问题{},响应：{}", param.getTaskId(), response.getPageContent());
            //没有设置referer会出现connect reset
            JSONObject json = response.getPageContentForJSON();
            //重复登陆:{"islocal":false,"result":"9"}
            if (StringUtils.equals("9", json.getString("result"))) {
                logger.info("重复登陆,param={}", param);
                return result.success();
            }
            String code = json.getString("code");
            if (StringUtils.equals("0000", code)) {

                //获取权限信息,必须访问下主页,否则详单有些cookie没用
                String artifact = json.getString("artifact");
                templateUrl = "https://touch.10086.cn/i/v1/auth/getArtifact2?backUrl=http://touch.10086.cn/i/&artifact={}";
                TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, artifact)
                        .setReferer("https://login.10086.cn/html/login/touch.html")
                        .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:52.0) Gecko/20100101 Firefox/52.0")
                        .addHeader("X-Requested-With", "XMLHttpRequest").invoke();
                String provinceCode = json.getString("provinceCode");
                if (StringUtils.isBlank(provinceCode)) {
                    provinceCode = TaskUtils.getCookieValue(param.getTaskId(), "ssologinprovince");
                }
                String provinceName = getProvinceName(provinceCode);
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.PROVINCE_NAME, provinceName);
                TaskUtils.addTaskShare(param.getTaskId(), "provinceCode", provinceCode);

                templateUrl = "https://touch.10086.cn/i/v1/cust/info/{}?time={}&channel=02";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                        .setFullUrl(templateUrl, param.getMobile(), new SimpleDateFormat("yMdHms").format(new Date())).invoke();

                TaskUtils.addTaskShare(param.getTaskId(), "personalPage", response.getPageContent());

                logger.info("登陆成功,param={}", param);
                return result.success();
            }
            switch (code) {
                case "2036":
                    logger.warn("登录失败-->账户名与密码不匹配,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                case "6001":
                    logger.warn("登录失败-->短信随机码不正确或已过期,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                case "6002":
                    logger.warn("登录失败-->短信随机码不正确或已过期,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                case "3013":
                    logger.warn("登录失败-->接口参数不对(可能性比较大)/系统繁忙,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                case "2046":
                    logger.warn("登录失败-->密码输入超过次数账号已锁定，详情垂询10086,param={}", param);
                    return result.failure("密码输入超过次数账号已锁定，详情垂询10086");
                default:
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
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
            TreeMap requestParams = new TreeMap();
            requestParams.put("moduleId", "DynamicLoginActivity");
            requestParams.put("operation", "register");
            requestParams.put("phoneNumber", param.getMobile());
            requestParams.put("loginPhoneNumber", param.getMobile());
            requestParams.put("staPhoneNumber", param.getMobile());
            requestParams.put("service", "esb");

            response = httpRequestAndCheck(param, requestParams);
            JSONObject json = response.getPageContentForJSON();
            String returnCode = (String) JSONPath.eval(json, "$.object.resultCode");
            switch (returnCode) {
                case "0000":
                    logger.info("App登录-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                default:
                    logger.error("App登录-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("App登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            TreeMap requestParams = new TreeMap();
            requestParams.put("moduleId", "DynamicLoginActivity");
            requestParams.put("operation", "checkRandomNumber");
            requestParams.put("phoneNumber", DESEncrysptionUtils.encryptDES(param.getMobile().toString()));
            requestParams.put("password", DESEncrysptionUtils.encryptDES(param.getSmsCode()));
            requestParams.put("pwdType", "02");
            requestParams.put("service", "esb");
            requestParams.put("loginPhoneNumber", param.getMobile());
            requestParams.put("staPhoneNumber", param.getMobile());

            response = httpRequestAndCheck(param, requestParams);
            JSONObject json = response.getPageContentForJSON();
            String returnCode = (String) JSONPath.eval(json, "$.object.resultCode");
            switch (returnCode) {
                case "0000":
                    String token = (String) JSONPath.eval(json, "$.object.resultData.token");
                    TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.TOKEN, token);

                    String provinceCode = TaskUtils.getTaskShare(param.getTaskId(), "provinceCode");
                    requestParams = new TreeMap();
                    requestParams.put("moduleId", "GuardService");
                    requestParams.put("operation", "getRealFee");
                    requestParams.put("phoneNumber", param.getMobile());
                    requestParams.put("provinceCode", provinceCode);
                    requestParams.put("service", "esb");
                    requestParams.put("token", token);
                    requestParams.put("loginPhoneNumber", param.getMobile());
                    requestParams.put("staPhoneNumber", param.getMobile());
                    response = httpRequestAndCheck(param, requestParams);
                    JSONObject feeJson = response.getPageContentForJSON();
                    String fee = (String) JSONPath.eval(feeJson, "$.object.resultData.curFee");
                    TaskUtils.addTaskShare(param.getTaskId(), "fee", fee);

                    logger.info("App登录-->校验成功,param={}", param);
                    return result.success();
                default:
                    logger.error("App登录-->校验失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("App登录-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForUserInfo(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            java.util.concurrent.TimeUnit.SECONDS.sleep(30);
            String provinceCode = TaskUtils.getTaskShare(param.getTaskId(), "provinceCode");
            String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);

            TreeMap requestParams = new TreeMap();
            requestParams.put("moduleId", "IdentityAuthenticationActivity");
            requestParams.put("operation", "register");
            requestParams.put("phoneNumber", param.getMobile());
            requestParams.put("provinceCode", provinceCode);
            requestParams.put("ext2", provinceCode);
            requestParams.put("service", "esb");
            requestParams.put("token", token);
            requestParams.put("loginPhoneNumber", param.getMobile());
            requestParams.put("staPhoneNumber", param.getMobile());

            response = httpRequestAndCheck(param, requestParams);
            JSONObject json = response.getPageContentForJSON();
            String returnCode = (String) JSONPath.eval(json, "$.object.resultCode");
            switch (returnCode) {
                case "0000":
                    logger.info("App详单-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                default:
                    logger.error("App详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("App详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForUserInfo(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String provinceCode = TaskUtils.getTaskShare(param.getTaskId(), "provinceCode");
            String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);

            TreeMap requestParams = new TreeMap();
            requestParams.put("moduleId", "IdentityAuthenticationActivity");
            requestParams.put("operation", "authentication");
            requestParams.put("phoneNumber", DESEncrysptionUtils.encryptDES(param.getMobile().toString()));
            requestParams.put("servicePassword", DESEncrysptionUtils.encryptDES(param.getPassword()));
            requestParams.put("tattedCode", DESEncrysptionUtils.encryptDES(param.getSmsCode()));
            requestParams.put("provinceCode", provinceCode);
            requestParams.put("service", "esb");
            requestParams.put("token", token);
            requestParams.put("loginPhoneNumber", param.getMobile());
            requestParams.put("staPhoneNumber", param.getMobile());

            response = httpRequestAndCheck(param, requestParams);
            JSONObject json = response.getPageContentForJSON();
            String returnCode = (String) JSONPath.eval(json, "$.object.resultCode");
            switch (returnCode) {
                case "0000":
                    logger.info("App详单-->校验成功,param={}", param);
                    return result.success();
                default:
                    logger.error("App详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("App详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Object> processForBill(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String[] times = paramMap.get("page_content").split(",");
        Response response = null;
        try {
            /**
             * 获取月账单
             */
            String provinceCode = TaskUtils.getTaskShare(param.getTaskId(), "provinceCode");
            String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);

            TreeMap requestParams = new TreeMap();
            requestParams.put("moduleId", "DetailsPhoneActivity");
            requestParams.put("operation", "getBillSum");
            requestParams.put("beginMonth", times[0]);
            requestParams.put("endMonth", times[1]);
            requestParams.put("phoneNumber", param.getMobile());
            requestParams.put("provinceCode", provinceCode);
            requestParams.put("service", "esb");
            requestParams.put("token", token);
            requestParams.put("loginPhoneNumber", param.getMobile());
            requestParams.put("staPhoneNumber", param.getMobile());
            response = httpRequestAndCheck(param, requestParams);
            return result.success(response.getPageContent());
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForDetails(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String billMonth = paramMap.get("page_content").replace(",", "");

        Response response = null;
        try {
            /**
             * 获取通话记录
             */
            String provinceCode = TaskUtils.getTaskShare(param.getTaskId(), "provinceCode");
            String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);

            TreeMap requestParams = new TreeMap();
            requestParams.put("moduleId", "DetailRecordActivity");
            requestParams.put("operation", "getDetailedRecord");
            requestParams.put("phoneNumber", param.getMobile());
            requestParams.put("provinceCode", provinceCode);
            requestParams.put("billMonth", billMonth);
            requestParams.put("start", "0");
            requestParams.put("length", "10000");
            requestParams.put("recordType", "02");
            requestParams.put("service", "esb");
            requestParams.put("token", token);
            requestParams.put("loginPhoneNumber", param.getMobile());
            requestParams.put("staPhoneNumber", param.getMobile());
            response = httpRequestAndCheck(param, requestParams);
            return result.success(response.getPageContent());
        } catch (Exception e) {
            logger.error("通话记录页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private String getProvinceName(String provinceCode) {
        CheckUtils.checkNotBlank(provinceCode, "provinceCode is blank");
        String json = PropertiesConfiguration.getInstance().get("operator.10086.shop.province.code");
        CheckUtils.checkNotBlank(json, "propery operator.10086.shop.province.code not found");
        Map<String, String> map = JSON.parseObject(json, new TypeReference<Map<String, String>>() {});
        return map.get(provinceCode);
    }

    private Response httpRequestAndCheck(OperatorParam param, TreeMap requestParams) {
        Response response = null;
        for (int i = 0; i < 3; i++) {
            requestParams.put("imei", "8697" + param.getMobile());
            requestParams.put("imsi", "4600" + param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(URL)
                    .setRequestBody(DESEncrysptionUtils.signParams(requestParams))
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").addHeader(HttpHeaders.USER_AGENT, "okhttp/3.6.0")
                    .invoke();
            String returnMessage = response.getPageContentForJSON().getString("returnMessage");
            if (!StringUtils.equals(returnMessage, "系统运行异常！") && !StringUtils.contains(response.getPageContent(), "应用超过每分钟调用接口总上限次数")) {
                if (i > 0) {
                    logger.info("遇到异常，请求重试有效，taskId={}", param.getTaskId());
                }
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.info("睡眠等待异常，taskId={}", param.getTaskId(), e);
            }
        }
        return response;
    }
}
