package com.datatrees.rawdatacentral.plugin.operator.china_10086_app;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.rawdatacentral.domain.vo.NameValue;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean.*;
import com.datatrees.rawdatacentral.plugin.operator.china_10086_app.utils.MD5Util;
import com.datatrees.rawdatacentral.plugin.operator.common.KpiUtils;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginPostService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 18/3/26.
 */
public class China10086ForApp3 implements OperatorPluginPostService {

    private static final Logger logger = LoggerFactory.getLogger(China10086ForApp3.class);

    private static final String P_AK   = "F4AA34B89513F0D087CA0EF11A3277469DC74905";

    private static final String P_CID
                                       = "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=";

    private static final String P_CITY = "0668";

    private static final String P_CTID
                                       = "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=";

    private static final String P_CV   = "4.3.0";

    private static final String P_EN   = "0";

    private static final String P_NT   = "3";

    private static final String P_PROV = "200";

    private static final String P_SB   = "Xiaomi";

    private static final String P_SN   = "Mi Note 2";

    private static final String P_SP   = "1080x1920";

    private static final String P_ST   = "1";

    private static final String P_SV   = "7.0";

    private static final String P_TEL  = "99999999999";

    private static final String P_XC   = "A2081";

    private static final String P_XK   = "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73";

    String P_IMEI = "869782021770311";

    private String encryptUrls = PropertiesConfiguration.getInstance().get("china.10086.app.encrypt.urls");

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        return result.success();
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
                return processForCallDetails(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            P_IMEI = "8697" + param.getMobile();
            String templateUrl = "https://app.10086.cn/biz-orange/LN/uamrandcode/sendMsgLogin";

            UserInfoLoginReq obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", obj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", "");
            params.put("tel", P_TEL);
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            String xs = MD5Util
                    .MD5(templateUrl + "_" + JSON.toJSONString(getEntity("", "60002", obj, "", param)) + "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, null);

            logger.info("输出：{},taskId={}", response, param.getTaskId());
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            String retDesc = json.getString("retDesc");
            switch (retCode) {
                case "000000":
                    logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                default:
                    logger.error("登录-->短信验证码-->刷新失败,{},param={},pateContent={}", retDesc, param, response.getPageContent());
                    return result.failure(retDesc);
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
            P_IMEI = "8697" + param.getMobile();
            /**
             * 获取手机号加密
             */

            String templateUrl = "https://app.10086.cn/biz-orange/LN/uamrandcodelogin/login";
            String encryptCellNum = encryptStr(param.getMobile().toString(), param);

            UserInfoLoginReq obj = new UserInfoLoginReq();
            obj.setCellNum(encryptCellNum);
            obj.setImei(P_IMEI);
            obj.setSendSmsFlag("0");
            obj.setVerifyCode(param.getSmsCode());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", obj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", "");
            params.put("tel", P_TEL);
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            String xs = MD5Util
                    .MD5(templateUrl + "_" + JSON.toJSONString(getEntity("", "60002", obj, "", param)) + "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, null);

            logger.info("输出：{},taskId={}", response.getPageContent(), param.getTaskId());

            String cookieString = StringUtils.EMPTY;
            List<NameValue> list = response.getHeaders();
            for (NameValue nameValue : list) {
                if (StringUtils.equals(nameValue.getName(), "Set-Cookie")) {
                    cookieString = nameValue.getValue();
                }
            }

            logger.info("输出：cookie={},taskId={}", cookieString, param.getTaskId());
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            String retDesc = json.getString("retDesc");
            switch (retCode) {
                case "000000":
                    String provinceName = (String) JSONPath.eval(json, "$.rspBody.provinceName");
                    TaskUtils.addTaskShare(param.getTaskId(), "provinceName", provinceName);
                    String cookieString2 = cookieString.replaceAll(" ", "%20");
                    /**
                     * 校验一下服务密码是否正确
                     */
                    encryptCellNum = encryptStr(param.getMobile().toString(), param);
                    String encryptPasswd = encryptStr(param.getPassword(), param);

                    templateUrl = "https://app.10086.cn/biz-orange/LN/tempIdentCode/getTmpIdentCode?" + cookieString2;
                    UserInfoLoginDoubleReq obj2 = new UserInfoLoginDoubleReq();
                    obj2.setBusinessCode("01");
                    obj2.setCellNum(encryptCellNum);
                    obj2.setImei(P_IMEI);
                    obj2.setPasswd(encryptPasswd);
                    obj2.setSmsPasswd("123456");

                    params = new LinkedHashMap<>();
                    params.put("ak", P_AK);
                    params.put("cid", P_CID);
                    params.put("city", P_CITY);
                    params.put("ctid", P_CTID);
                    params.put("cv", P_CV);
                    params.put("en", P_EN);
                    params.put("imei", P_IMEI);
                    params.put("nt", P_NT);
                    params.put("prov", P_PROV);
                    params.put("reqBody", obj2);
                    params.put("sb", P_SB);
                    params.put("sn", P_SN);
                    params.put("sp", P_SP);
                    params.put("st", P_ST);
                    params.put("sv", P_SV);
                    params.put("t", MD5Util.MD5(cookieString, 32));
                    params.put("tel", param.getMobile().toString());
                    params.put("xc", P_XC);
                    params.put("xk", P_XK);

                    xs = MD5Util
                            .MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "60006", obj2, param.getMobile().toString(), param)) +
                                    "_Leadeon/SecurityOrganization", 32);

                    response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

                    json = response.getPageContentForJSON();
                    retCode = json.getString("retCode");
                    logger.info("输出(校验服务密码)：{},taskId={}", response.getPageContent(), param.getTaskId());
                    if (StringUtils.equals(retCode, "213120")) {
                        retDesc = "尊敬的用户，您好，您输入的手机号码和服务密码不匹配，请检查后重新输入";
                        logger.error("登录-->校验-->失败,{},param={},pateContent={}", retDesc, param, response.getPageContent());
                        return result.failure(retDesc);
                    } else if (StringUtils.equals(retCode, "310003")) {
                        retDesc = "您输入的服务密码包含非数字字符，请检查后重新输入";
                        logger.error("登录-->校验-->失败,{},param={},pateContent={}", retDesc, param, response.getPageContent());
                        return result.failure(retDesc);
                    } else if (StringUtils.equals(retCode, "310002")) {
                        retDesc = "您输入的服务密码长度非法，请检查后重新输入";
                        logger.error("登录-->校验-->失败,{},param={},pateContent={}", retDesc, param, response.getPageContent());
                        return result.failure(retDesc);
                    } else if (StringUtils.equals(retCode, "213146")) {
                        retDesc = "尊敬的用户，您的账户已锁定，将于24小时后解锁";
                        logger.error("登录-->校验-->失败,{},param={},pateContent={}", retDesc, param, response.getPageContent());
                        return result.failure(retDesc);
                    }
                    TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);
                    logger.info("登录-->校验-->成功,param={}", param);
                    return result.success();
                default:
                    logger.error("登录-->校验-->失败,{},param={},pateContent={}", retDesc, param, response.getPageContent());
                    return result.failure(retDesc);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> loginPost(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            P_IMEI = "8697" + param.getMobile();
            String cookieString = TaskUtils.getTaskShare(param.getTaskId(), "cookieString");
            String cookieString2 = cookieString.replaceAll(" ", "%20");
            /**
             * 查询信用分
             */
            String templateUrl = "https://app.10086.cn/biz-orange/SHS/userCredit/getCredit?" + cookieString2;
            UserInfoLoginReq obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());
            obj.setPageFlag("2");

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", obj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            String xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "5044", obj, param.getMobile().toString(), param)) +
                    "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

            logger.info("输出(信用分)：{},taskId={}", response.getPageContent(), param.getTaskId());
            JSONObject json = response.getPageContentForJSON();
            String creditSumSco = (String) JSONPath.eval(json, "$.rspBody.userCreditInfos[0].creditSumSco");
            TaskUtils.addTaskShare(param.getTaskId(), "creditSumSco", creditSumSco);

            /**
             * 查询个人信息
             */
            templateUrl = "https://app.10086.cn/biz-orange/BN/userInformationService/getUserInformation?" + cookieString2;
            obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());

            params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", obj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20007", obj, param.getMobile().toString(), param)) +
                    "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

            logger.info("输出(个人信息)：{},taskId={}", response.getPageContent(), param.getTaskId());
            TaskUtils.addTaskShare(param.getTaskId(), "userInfo", response.getPageContent());

            /**
             * 查询余额
             */
            templateUrl = "https://app.10086.cn/biz-orange/BN/realFeeQuery/getRealFee?" + cookieString2;
            obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", obj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20016", obj, param.getMobile().toString(), param)) +
                    "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

            logger.info("输出(余额)：{},taskId={}", response.getPageContent(), param.getTaskId());
            json = response.getPageContentForJSON();
            String curFee = (String) JSONPath.eval(json, "$.rspBody.curFee");
            TaskUtils.addTaskShare(param.getTaskId(), "curFee", curFee);

            return result.success();
        } catch (Exception e) {
            logger.error("登陆后处理失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.SYS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            P_IMEI = "8697" + param.getMobile();
            String cookieString = TaskUtils.getTaskShare(param.getTaskId(), "cookieString");
            String cookieString2 = cookieString.replaceAll(" ", "%20");

            String templateUrl = "https://app.10086.cn/biz-orange/LN/uamrandcode/sendMsgLogin?" + cookieString2;
            UserInfoLoginReq obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", obj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            String xs = MD5Util
                    .MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "60002", obj, param.getMobile().toString(), param)) +
                            "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

            logger.info("输出(获取短信)：{},taskId={}", response.getPageContent(), param.getTaskId());
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            String retDesc = json.getString("retDesc");
            switch (retCode) {
                case "000000":
                    logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                default:
                    logger.error("详单-->短信验证码-->刷新失败,{},param={},pateContent={}", retDesc, param, response.getPageContent());
                    return result.failure(retDesc);
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
            P_IMEI = "8697" + param.getMobile();
            String cookieString = TaskUtils.getTaskShare(param.getTaskId(), "cookieString");
            String cookieString2 = cookieString.replaceAll(" ", "%20");

            String encryptCellNum = encryptStr(param.getMobile().toString(), param);
            String encryptPasswd = encryptStr(param.getPassword(), param);

            String templateUrl = "https://app.10086.cn/biz-orange/LN/tempIdentCode/getTmpIdentCode?" + cookieString2;
            UserInfoLoginDoubleReq obj = new UserInfoLoginDoubleReq();
            obj.setBusinessCode("01");
            obj.setCellNum(encryptCellNum);
            obj.setImei(P_IMEI);
            obj.setPasswd(encryptPasswd);
            obj.setSmsPasswd(param.getSmsCode());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", obj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            String xs = MD5Util
                    .MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "60006", obj, param.getMobile().toString(), param)) +
                            "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

            logger.info("输出(校验短信)：{},taskId={}", response.getPageContent(), param.getTaskId());
            JSONObject json = response.getPageContentForJSON();
            String retCode = json.getString("retCode");
            String retDesc = json.getString("retDesc");
            switch (retCode) {
                case "000000":
                    List<NameValue> list = response.getHeaders();
                    for (NameValue nameValue : list) {
                        if (StringUtils.equals(nameValue.getName(), "Set-Cookie")) {
                            cookieString = nameValue.getValue();
                        }
                    }
                    TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString.replaceAll(" ", "%20"));
                    logger.info("详单-->校验成功,param={}", param);
                    return result.success();
                default:
                    logger.error("详单-->校验失败,{},param={},pageContent={}", retDesc, param, response.getPageContent());
                    return result.failure(retDesc);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Object> processForBill(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String[] billMonth = paramMap.get("page_content").split(",");
        Response response = null;
        try {
            P_IMEI = "8697" + param.getMobile();
            String cookieString = TaskUtils.getTaskShare(param.getTaskId(), "cookieString");
            String cookieString2 = cookieString.replaceAll(" ", "%20");
            /**
             * 查询账单
             */
            String templateUrl = "https://app.10086.cn/biz-orange/BN/historyBillsService/getHistoryBills?" + cookieString2;
            BillReqBean billObj = new BillReqBean();
            billObj.setBgnMonth(billMonth[0]);
            billObj.setCellNum(param.getMobile().toString());
            billObj.setEndMonth(billMonth[1]);

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", billObj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            String xs = MD5Util
                    .MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20009", billObj, param.getMobile().toString(), param)) +
                            "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

            String pageContent = response.getPageContent();
            logger.info("输出(账单)：{},taskId={}", pageContent, param.getTaskId());
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForCallDetails(OperatorParam param) {
        MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String billMonth = paramMap.get("page_content").replace(",", "");
        Response response = null;
        try {
            P_IMEI = "8697" + param.getMobile();
            List<String> dataList = new ArrayList<>();
            String cookieString = TaskUtils.getTaskShare(param.getTaskId(), "cookieString");
            String cookieString2 = cookieString.replaceAll(" ", "%20");
            /**
             * 查询详单
             */
            String templateUrl = "https://app.10086.cn/biz-orange/BN/queryDetail/getDetail?" + cookieString2;
            DetailReqBean detailObj = new DetailReqBean();
            detailObj.setBillMonth(billMonth);
            detailObj.setCellNum(param.getMobile().toString());
            detailObj.setPage(1);
            detailObj.setTmemType("02");
            detailObj.setUnit(200);

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", P_AK);
            params.put("cid", P_CID);
            params.put("city", P_CITY);
            params.put("ctid", P_CTID);
            params.put("cv", P_CV);
            params.put("en", P_EN);
            params.put("imei", P_IMEI);
            params.put("nt", P_NT);
            params.put("prov", P_PROV);
            params.put("reqBody", detailObj);
            params.put("sb", P_SB);
            params.put("sn", P_SN);
            params.put("sp", P_SP);
            params.put("st", P_ST);
            params.put("sv", P_SV);
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", P_XC);
            params.put("xk", P_XK);

            String xs = MD5Util
                    .MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20012", detailObj, param.getMobile().toString(), param)) +
                            "_Leadeon/SecurityOrganization", 32);

            response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

            String pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, "服务异常,请稍后再试")) {
                logger.warn("详单查询：服务异常,请稍后再试！当前页：1--》进行第一次重试taskId={}", param.getTaskId());

                response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

                pageContent = response.getPageContent();
            }

            if (StringUtils.contains(pageContent, "服务异常,请稍后再试")) {
                logger.warn("详单查询：服务异常,请稍后再试！当前页：1--》进行第二次重试taskId={}", param.getTaskId());

                response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

                pageContent = response.getPageContent();
            }

            logger.info("输出(详单)：{},taskId={}", pageContent, param.getTaskId());
            dataList.add(pageContent);
            int pages = 0;
            if (StringUtils.contains(pageContent, "totalCount")) {
                String totalCount = (String) JSONPath.eval(response.getPageContentForJSON(), "$.rspBody.totalCount");
                monitorService.sendTaskLog(param.getTaskId(), "详单-->查询-->成功," + billMonth + "月份,数量-" + totalCount);
                KpiUtils.sendKpi(param, "call_month_real_size", billMonth, totalCount, null);
                pages = (Integer.parseInt(totalCount) - 1) / 200 + 1;
            } else {
                monitorService.sendTaskLog(param.getTaskId(), "详单-->查询-->失败," + billMonth + "月份，原文-" + pageContent);
            }
            /**
             * 如果超过200条，需翻页
             */
            for (int i = 2; i <= pages; i++) {
                detailObj.setPage(i);
                params = new LinkedHashMap<>();
                params.put("ak", P_AK);
                params.put("cid", P_CID);
                params.put("city", P_CITY);
                params.put("ctid", P_CTID);
                params.put("cv", P_CV);
                params.put("en", P_EN);
                params.put("imei", P_IMEI);
                params.put("nt", P_NT);
                params.put("prov", P_PROV);
                params.put("reqBody", detailObj);
                params.put("sb", P_SB);
                params.put("sn", P_SN);
                params.put("sp", P_SP);
                params.put("st", P_ST);
                params.put("sv", P_SV);
                params.put("t", MD5Util.MD5(cookieString, 32));
                params.put("tel", param.getMobile().toString());
                params.put("xc", P_XC);
                params.put("xk", P_XK);

                xs = MD5Util
                        .MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20012", detailObj, param.getMobile().toString(), param)) +
                                "_Leadeon/SecurityOrganization", 32);

                response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

                pageContent = response.getPageContent();
                if (StringUtils.contains(pageContent, "服务异常,请稍后再试")) {
                    logger.warn("详单查询：服务异常,请稍后再试！当前页：{}--》进行第一次重试taskId={}", i, param.getTaskId());

                    response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

                    pageContent = response.getPageContent();
                }

                if (StringUtils.contains(pageContent, "服务异常,请稍后再试")) {
                    logger.warn("详单查询：服务异常,请稍后再试！当前页：{}--》进行第二次重试taskId={}", i, param.getTaskId());

                    response = httpRequestAndCheck(param, templateUrl, xs, params, cookieString);

                    pageContent = response.getPageContent();
                }
                logger.info("输出(详单)：{},taskId={}", pageContent, param.getTaskId());
                dataList.add(pageContent);
            }
            return result.success(JSON.toJSONString(dataList));
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    public RequestBean getEntity(String cookie, String code, Object obj, String phoneNum, OperatorParam param) {
        RequestBean requestBean = new RequestBean();
        try {
            P_IMEI = "8697" + param.getMobile();
            requestBean.setCid(P_CID);
            requestBean.setCtid(P_CTID);
            requestBean.setCv(P_CV);
            requestBean.setXc(P_XC);
            requestBean.setXk(P_XK);
            if ("60001".equals(code) || "60010".equals(code)) {
                requestBean.setEn("3");
            } else {
                requestBean.setEn("0");
            }
            requestBean.setSn(P_SN);
            requestBean.setSp(P_SP);
            requestBean.setSt(P_ST);
            requestBean.setSv(P_SV);
            requestBean.setAk(P_AK);
            if ("".equals(cookie)) {
                requestBean.setT("");
            } else {
                requestBean.setT(MD5Util.MD5(cookie, 32));
            }
            if (StringUtils.isEmpty(phoneNum)) {
                requestBean.setTel(P_TEL);
            } else {
                requestBean.setTel(phoneNum);
            }
            requestBean.setImei(P_IMEI);
            requestBean.setSb(P_SB);
            requestBean.setNt(P_NT);
            String cityId = P_CITY;
            String proId = P_PROV;
            if (StringUtils.isEmpty(cityId)) {
                requestBean.setCity("");
            } else {
                requestBean.setCity(cityId);
            }
            if (StringUtils.isEmpty(proId)) {
                requestBean.setProv("");
            } else {
                requestBean.setProv(proId);
            }
            if (obj != null) {
                requestBean.setReqBody(obj);
            }
        } catch (Exception e) {
        }
        return requestBean;
    }

    private String encryptStr(String str, OperatorParam param) {
        Response response = null;
        String result = StringUtils.EMPTY;
        try {
            String[] urls = encryptUrls.split(",");
            int size = urls.length;
            for (int i = 0; i < size; i++) {
                int index = (int) Math.floor(Math.random() * urls.length);
                String url = "http://" + urls[index] + "?str={}";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(url, str)
                        .setProxyEnable(false).invoke();
                String pageContent = response.getPageContent();
                if (StringUtils.isNotBlank(pageContent)) {
                    result = pageContent;
                    break;
                }
                /**
                 * 删除不能加密的地址
                 */
                urls[index] = urls[urls.length - 1];
                urls = Arrays.copyOf(urls, urls.length - 1);
            }

        } catch (Exception e) {
            logger.error("调用安卓服务加密失败,response={}", response, e);
        }
        if (StringUtils.isBlank(result)) {
            logger.error("调用安卓服务加密失败,response={}", response);
        }
        return result;
    }

    private Response httpRequestAndCheck(OperatorParam param, String templateUrl, String xs, Object params, String cookieString) {
        Response response = null;
        for (int i = 0; i < 3; i++) {
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("xs", xs).addHeader("Cookie", cookieString)
                    .invoke();
            if (!StringUtils.equals(response.getStatusCode() + "", "403") && !StringUtils.equals(response.getStatusCode() + "", "500")) {
                if (i > 0) {
                    logger.info("遇到403或500请求重试有效，taskId={}", param.getTaskId());
                }
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.info("睡眠等待异常，taskId={}", param.getTaskId(), e);
            }
            if (StringUtils.equals(response.getStatusCode() + "", "403")) {
                logger.info("遇到403请求重试无效，response={},taskId={}", response, param.getTaskId());
            }
            if (StringUtils.equals(response.getStatusCode() + "", "500")) {
                logger.info("遇到500请求重试无效，response={},taskId={}", response, param.getTaskId());
            }
        }
        return response;
    }
}
