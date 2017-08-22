package com.datatrees.rawdatacentral.plugin.operator.china_10000_app;

import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 中国电信App--部分省份通用
 * 目前已知（2017.8.18）
 * 支持：
 * 甘肃、黑龙江、北京、江苏、山东、湖南、天津、四川、重庆、安徽、浙江、江西、福建、山西
 *
 * 注：天津电信的账单中无法获取姓名
 *
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
 * 内蒙古无账单详单查询
 * <p>
 * 登陆地址:https://login.10086.cn/html/login/login.html
 * 登陆方式:服务密码登陆
 * 图片验证码:支持
 * 验证图片验证码:支持
 * 短信验证码:支持
 * <p>
 * Created by guimeichao on 17/8/17.
 */
public class China10000ForApp implements OperatorPluginService {
    private static final Logger logger = LoggerFactory.getLogger(China10000ForApp.class);
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    private static RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);

    /**
     * 公用请求url模板
     */
    private String templateUrl = "http://cservice.client.189.cn:8004/map/clientXML?encrypted=true";

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
        logger.warn("defineProcess fail,params={}", param);
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String token = redisService.getString(AttributeKey.TOKEN);
        Response response = null;
        try {
            String templateData = "<Request><HeaderInfos><Code>getRandomV2</Code><Timestamp>{}</Timestamp><ClientType>#6.0.0#channel38#Xiaomi Mi Note 2#</ClientType>"
                    + "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos>"
                    + "<Content><Attach>test</Attach><FieldData><PhoneNbr>{}</PhoneNbr></FieldData></Content></Request>";
            String data = String.format(templateData, format.format(new Date()), token, param.getMobile(), param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_005")
                    .setFullUrl(templateUrl)
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
        String token = redisService.getString(AttributeKey.TOKEN);
        Response response = null;
        try {
            String templateData = "<Request><HeaderInfos><Code>jfyBillDetail</Code><Timestamp>{}</Timestamp><ClientType>#6.0.0#channel38#Xiaomi Mi Note 2#</ClientType>" +
                    "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos><Content>" +
                    "<Attach>test</Attach><FieldData><StartTime>{}</StartTime><Type>1</Type><Random>{}</Random><PhoneNum>{}</PhoneNum><EndTime>{}</EndTime></FieldData></Content></Request>";
            String data = String.format(templateData, format.format(new Date()), token, param.getMobile(),
                    format2.format(new Date()), param.getSmsCode(), param.getMobile(), format2.format(new Date()));
            response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_006")
                    .setFullUrl(templateUrl)
                    .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
            String pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
            String resultCode = XPathUtil.getXpath("//ResultCode/text()", pageContent).get(0);
            switch (resultCode) {
                case "0000":
                    logger.info("详单-->校验成功,param={}", param);
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
            String templateData = "<Request><HeaderInfos><Code>loginInfo</Code><Timestamp>{}</Timestamp><ClientType>#6.0.0#channel38#Xiaomi Mi Note 2#</ClientType>"
                    + "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>null</Token><UserLoginName>{}</UserLoginName></HeaderInfos><Content>"
                    + "<Attach>test</Attach><FieldData><PswType>01</PswType><PhonePsw>{}</PhonePsw><PhoneNbr>{}</PhoneNbr><AccountType>c2000004</AccountType><Token></Token></FieldData></Content></Request>";
            String data = TemplateUtils.format(templateData, format.format(new Date()), param.getMobile(),
                    param.getPassword(), param.getMobile());

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

                redisService.addTaskShare(param.getTaskId(), AttributeKey.TOKEN, token);
                if (StringUtils.isBlank(provinceName)) {
                    logger.error("获取省份名失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                redisService.addTaskShare(param.getTaskId(), AttributeKey.PROVINCE_NAME, provinceName);

                logger.info("登陆成功,param={}", param);
                logger.info("--开始查询个人信息--");

                templateData = "<Request><HeaderInfos><Code>custInfo</Code><Timestamp>{}</Timestamp><ClientType>#6.0.3#channel38#Xiaomi Mi Note 2#</ClientType>"
                        + "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos>"
                        + "<Content><Attach>test</Attach><FieldData><PhoneNbr>{}</PhoneNbr></FieldData></Content></Request>";
                data = TemplateUtils.format(templateData, format.format(new Date()), token, param.getMobile(),
                        param.getMobile());
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
                        templateData = "<Request><HeaderInfos><Code>jfyHisBill</Code><Timestamp>{}</Timestamp><ClientType>#6.0.0#channel38#Xiaomi Mi Note 2#</ClientType>"
                                + "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos><Content>"
                                + "<Attach>test</Attach><FieldData><Random>123456</Random><Month>{}</Month><PhoneNum>{}</PhoneNum><Type>1</Type></FieldData></Content></Request>";
                        data = TemplateUtils.format(templateData, format.format(new Date()), token, param.getMobile(),
                                format2.format(calendar.getTime()), param.getMobile());
                        response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_003")
                                .setFullUrl(templateUrl)
                                .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
                        pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
                        realName = XPathUtil.getXpath("//AcctName/text()", pageContent).get(0);
                        if (StringUtils.isNotBlank(realName)) {
                            break;
                        }
                    }

                }
                if (StringUtils.isBlank(realName)) {
                    logger.error("获取姓名失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                redisService.addTaskShare(param.getTaskId(), AttributeKey.REAL_NAME, realName);

                /**
                 * 获取余额
                 */
                templateData = "<Request><HeaderInfos><Code>hgoBillInfo</Code><Timestamp>{}</Timestamp><ClientType>#6.0.3#channel38#Xiaomi Mi Note 2#</ClientType>" +
                        "<Source>110003</Source><SourcePassword>Sid98s</SourcePassword><Token>{}</Token><UserLoginName>{}</UserLoginName></HeaderInfos>" +
                        "<Content><Attach>test</Attach><FieldData><PhoneNum>{}</PhoneNum><IsDirectCon>1</IsDirectCon><PhoneType>6</PhoneType></FieldData></Content></Request>";
                data = TemplateUtils.format(templateData, format.format(new Date()), token, param.getMobile(), param.getMobile());
                response = TaskHttpClient.create(param, RequestType.POST, "china_10000_app_004")
                        .setFullUrl(templateUrl)
                        .setRequestBody(EncryptUtilsForChina10000App.encrypt(data), ContentType.TEXT_XML).invoke();
                pageContent = EncryptUtilsForChina10000App.decrypt(response.getPageContent());
                String balance = XPathUtil.getXpath("//TotalBalance/text()", pageContent).get(0);
                if (StringUtils.isBlank(balance)) {
                    logger.error("获取余额失败,param={},pageContent={}", param, pageContent);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
                redisService.addTaskShare(param.getTaskId(), AttributeKey.ACCOUNT_BALANCE, balance);
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

}
