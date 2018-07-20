package com.datatrees.rawdatacentral.plugin.operator.china_10000_app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.spider.operator.domain.model.FormType;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中国电信App--部分省份通用
 * 登陆地址:电信营业厅App
 * 登陆方式:服务密码登陆
 * 目前已知（2017.11.02）
 * 支持：
 * 甘肃、黑龙江、北京、江苏、山东、湖南、天津、四川、重庆、安徽、江西、福建、山西
 * <p>
 * 注：天津电信的账单中无法获取姓名
 * <p>
 * 不支持以及原因：
 * 广东详单暂不支持此项功能查询
 * 上海账单服务器异常
 * 湖北调用wap版请求
 * 河南账单服务器异常
 * 河北账单服务器异常
 * 吉林账单服务器异常
 * 辽宁账单服务器异常
 * 云南账单服务器异常
 * 广西无账单详单查询
 * 陕西无账单详单查询
 * 海南无账单详单查询
 * 浙江无详单查询
 * 内蒙古无账单详单查询
 * Created by guimeichao on 17/8/17.
 */
public class China10000ForApp implements OperatorPluginService {

    private static final Logger           logger      = LoggerFactory.getLogger(China10000ForApp.class);
    private static final SimpleDateFormat format      = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     * 公用请求url模板
     */
    private              String           templateUrl = "http://cservice.client.189.cn:8004/map/clientXML?encrypted=true";

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
        switch (param.getFormType()) {
            case "BILL_DETAILS":
                return processForBill(param);
            case "CALL_DETAILS":
                return processForDetails(param, "1");
            case "SMS_DETAILS":
                return processForDetails(param, "2");
            case "NET_DETAILS":
                return processForDetails(param, "3");
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);
        Response response = null;
        try {
            String templateData = "<Request><HeaderInfos><Code>getRandomV2</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi Mi " +
                    "Note 2#</ClientType><Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName" +
                    ">{}</UserLoginName></HeaderInfos><Content><Attach>test</Attach><FieldData><SceneType>7</SceneType><Imsi></Imsi><PhoneNbr>{}</PhoneNbr></FieldData></Content" +
                    "></Request>";
            String data = TemplateUtils.format(templateData, format.format(new Date()), token, param.getMobile(), param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_005").setFullUrl(templateUrl)
                    .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
            String pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
            String resultCode = XPathUtil.getXpath("//ResultCode/text()", pageContent).get(0);
            if (StringUtils.equals("0000", resultCode)) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, pageContent);
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);
        Response response = null;
        try {
            String templateData = "<Request><HeaderInfos><Code>jfyBillDetail</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi Mi " +
                    "Note 2#</ClientType><Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName" +
                    ">{}</UserLoginName></HeaderInfos><Content><Attach>test</Attach><FieldData><StartTime>{}</StartTime><Type>1</Type><Random" +
                    ">{}</Random><PhoneNum>{}</PhoneNum><EndTime>{}</EndTime></FieldData></Content></Request>";
            String data = TemplateUtils
                    .format(templateData, format.format(new Date()), token, param.getMobile(), format2.format(new Date()), param.getSmsCode(),
                            param.getMobile(), format2.format(new Date()));
            response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_006").setFullUrl(templateUrl)
                    .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
            String pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
            String resultCode = XPathUtil.getXpath("//ResultCode/text()", pageContent).get(0);
            switch (resultCode) {
                case "0000":
                    logger.info("详单-->校验成功,param={}", param);
                    TaskUtils.addTaskShare(param.getTaskId(), "smsCodeTemp", param.getSmsCode());
                    return result.success();
                case "149":
                    logger.warn("详单-->短信验证码错误,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("详单-->校验失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            //数据模板
            String templateData = "<Request><HeaderInfos><Code>loginInfo</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi Mi Note" +
                    " 2#</ClientType><Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>null</Token><UserLoginName>{}</UserLoginName></HeaderInfos><Content>" +
                    "<Attach>test</Attach><FieldData><PswType>01</PswType><PhonePsw>{}</PhonePsw><PhoneNbr>{}</PhoneNbr><AccountType>c2000004</AccountType><Token></Token></FieldData></Content></Request>";
            String data = TemplateUtils.format(templateData, format.format(new Date()), param.getMobile(), param.getPassword(), param.getMobile());

            response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_001").setFullUrl(templateUrl)
                    .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
            String pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());

            /**
             * 结果枚举:
             * 登陆成功:<Response><HeaderInfos><Code>0000</Code><Reason>成功</Reason></HeaderInfos><ResponseData><Attach>test</Attach><ResultCode>0000</ResultCode>
             * <ResultDesc>认证成功</ResultDesc><Data><Token>l5c+bhYkNrpKaVcOLXeVxKmJksr5A3THFYMCJ4+rRDRF9rTcyS6amQ==</Token><PhoneNbr>18193789187</PhoneNbr><UserId>20170000000006287744</UserId><UserType>1</UserType><Init><IsDirectCon>1</IsDirectCon><PhoneType>6</PhoneType><ProvinceCode>600401</ProvinceCode><CityCode>8620200</CityCode><ProvinceName>甘肃</ProvinceName><CityName>嘉峪关</CityName></Init></Data></ResponseData></Response>
             *
             * 账号或密码错误:<Response><HeaderInfos><Code>0000</Code><Reason>成功</Reason></HeaderInfos><ResponseData><Attach>test</Attach><ResultCode>3001</ResultCode>
             * <ResultDesc>账号或密码错误哦~</ResultDesc><Data><LoginFailTime>1</LoginFailTime></Data></ResponseData></Response>
             *
             */
            String resultCode = XPathUtil.getXpath("//ResultCode/text()", pageContent).get(0);
            if (StringUtils.equals("0000", resultCode)) {
                /**
                 * 取出token供之后的请求使用
                 * 取出运营商省份，作为个人信息的归属省份
                 */
                String token = XPathUtil.getXpath("//Token/text()", pageContent).get(0);
                String provinceName = XPathUtil.getXpath("//ProvinceName/text()", pageContent).get(0);

                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.TOKEN, token);
                if (StringUtils.isBlank(provinceName)) {
                    logger.error("获取省份名失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.PROVINCE_NAME, provinceName);

                logger.info("登陆成功,param={}", param);
                logger.info("--开始查询个人信息--");

                templateData = "<Request><HeaderInfos><Code>custInfo</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi Mi Note " +
                        "2#</ClientType><Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos>" +
                        "<Content><Attach>test</Attach><FieldData><PhoneNbr>{}</PhoneNbr></FieldData></Content></Request>";
                data = TemplateUtils.format(templateData, format.format(new Date()), token, param.getMobile(), param.getMobile());
                response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_002").setFullUrl(templateUrl)
                        .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
                /**
                 * 取出姓名
                 */
                pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
                String realName = XPathUtil.getXpath("//Cust_Name/text()", pageContent).get(0);
                if (StringUtils.isBlank(realName)) {
                    /**
                     * 如果姓名为空，则该请求无法查询姓名
                     * 备用渠道：从账单中查询出姓名
                     * 因为两个渠道查询姓名均不稳定，所以采取互补方式
                     *
                     * 备注：
                     *      例如：王小二
                     *      前面请求可抓到姓名：王*
                     *      下面请求可抓到姓名：王小二
                     */
                    SimpleDateFormat format2 = new SimpleDateFormat("yyyyMM");
                    Calendar calendar = Calendar.getInstance();
                    /**
                     * 由于可能会出现某个用户某个月查不了账单，这里遍历了最近四个月的账单
                     */
                    for (int i = 0; i < 4; i++) {
                        calendar.add(Calendar.MONTH, -1);
                        templateData = "<Request><HeaderInfos><Code>jfyHisBill</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi " +
                                "Mi Note 2#</ClientType>" +
                                "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos><Content>" +
                                "<Attach>test</Attach><FieldData><Random>123456</Random><Month>{}</Month><PhoneNum>{}</PhoneNum><Type>1</Type></FieldData></Content></Request>";
                        data = TemplateUtils
                                .format(templateData, format.format(new Date()), token, param.getMobile(), format2.format(calendar.getTime()),
                                        param.getMobile());
                        response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_003").setFullUrl(templateUrl)
                                .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
                        pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
                        realName = XPathUtil.getXpath("//AcctName/text()", pageContent).get(0);
                        if (StringUtils.isNotBlank(realName)) {
                            break;
                        }
                    }

                }
                if (StringUtils.isBlank(realName) || StringUtils.equals(realName, "null")) {
                    logger.error("获取姓名失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.REAL_NAME, realName);

                /**
                 * 获取余额
                 */
                templateData = "<Request><HeaderInfos><Code>hgoBillInfo</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi Mi Note " +
                        "2#</ClientType>" +
                        "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos>" +
                        "<Content><Attach>test</Attach><FieldData><PhoneNum>{}</PhoneNum><IsDirectCon>1</IsDirectCon><PhoneType>6</PhoneType></FieldData></Content></Request>";
                data = TemplateUtils.format(templateData, format.format(new Date()), token, param.getMobile(), param.getMobile());
                response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_004").setFullUrl(templateUrl)
                        .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
                pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
                String balance = XPathUtil.getXpath("//TotalBalance/text()", pageContent).get(0);
                if (StringUtils.isBlank(balance) || StringUtils.equals(balance, "null")) {
                    logger.error("获取余额失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.ACCOUNT_BALANCE, balance);
                return result.success();
            }
            switch (resultCode) {
                case "3001":
                    logger.warn("登录失败-->账户名与密码不匹配,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                default:
                    logger.error("登陆失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Object> processForBill(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();

        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);
        String billMonth = paramMap.get("page_content");

        Response response = null;
        try {
            /**
             * 获取月账单
             */
            String templateData = "<Request><HeaderInfos><Code>jfyHisBill</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi Mi " +
                    "Note 2#</ClientType>" +
                    "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos><Content>" +
                    "<Attach>test</Attach><FieldData><Random>123456</Random><Month>{}</Month><PhoneNum>{}</PhoneNum><Type>1</Type></FieldData></Content></Request>";
            String data = TemplateUtils.format(templateData, format.format(new Date()), token, param.getMobile(), billMonth, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_007").setFullUrl(templateUrl)
                    .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
            String pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForDetails(OperatorParam param, String queryType) {
        HttpResult<Object> result = new HttpResult<>();

        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String token = TaskUtils.getTaskShare(param.getTaskId(), AttributeKey.TOKEN);
        String[] times = paramMap.get("page_content").split("#");

        Response response = null;
        try {
            /**
             * 获取通话记录
             */
            String templateData = "<Request><HeaderInfos><Code>jfyBillDetail</Code><Timestamp>{}</Timestamp><ClientType>#6.2.1#channel38#Xiaomi Mi " +
                    "Note 2#</ClientType><Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName" +
                    ">{}</UserLoginName></HeaderInfos><Content><Attach>test</Attach><FieldData><StartTime>{}</StartTime><Type>{}</Type><Random" +
                    ">{}</Random><PhoneNum>{}</PhoneNum><EndTime>{}</EndTime></FieldData></Content></Request>";

            String smsCode = param.getExtral().get(AttributeKey.SMS_CODE) + "";
            if (StringUtils.isEmpty(smsCode) || StringUtils.equals(smsCode, "null")) {
                smsCode = TaskUtils.getTaskShare(param.getTaskId(), "smsCodeTemp");
            }
            String data = TemplateUtils
                    .format(templateData, format.format(new Date()), token, param.getMobile(), times[0], queryType, smsCode, param.getMobile(),
                            times[1]);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_008").setFullUrl(templateUrl)
                    .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
            String pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("通话记录页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

}
