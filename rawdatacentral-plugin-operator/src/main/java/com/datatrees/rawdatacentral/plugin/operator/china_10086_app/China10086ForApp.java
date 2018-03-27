package com.datatrees.rawdatacentral.plugin.operator.china_10086_app;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.NameValue;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean.BillReqBean;
import com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean.DetailReqBean;
import com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean.UserInfoLoginReq;
import com.datatrees.rawdatacentral.plugin.operator.china_10086_app.utils.MD5Util;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 18/3/26.
 */
public class China10086ForApp implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(China10086ForApp.class);

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
                //return refeshSmsCodeForBillDetail(param);
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
                //return submitForBillDetail(param);
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

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://clientaccess.10086.cn/biz-orange/LN/uamrandcode/sendMsgLogin";

            UserInfoLoginReq obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", "F4AA34B89513F0D087CA0EF11A3277469DC74905");
            params.put("cid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("city", "0668");
            params.put("ctid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("cv", "4.3.0");
            params.put("en", "0");
            params.put("imei", "869782021770311");
            params.put("nt", "3");
            params.put("prov", "200");
            params.put("reqBody", obj);
            params.put("sb", "Xiaomi");
            params.put("sn", "Mi Note 2");
            params.put("sp", "1080x1920");
            params.put("st", "1");
            params.put("sv", "7.0");
            params.put("t", "");
            params.put("tel", "99999999999");
            params.put("xc", "A2081");
            params.put("xk", "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");

            String xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity("", "60002", obj, "")) + "_Leadeon/SecurityOrganization", 32);

            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_app_001").setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("User-Agent", "okhttp/3.9.0")
                    .addHeader("xs", xs).invoke();

            logger.info("输出：{},taskId={}", response, param.getTaskId());
            return result.success();
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
            /**
             * 获取手机号加密
             */
            String getUrl = "http://192.168.202.143:8088/?str=" + param.getMobile().toString();
            response = TaskHttpClient.create(param, RequestType.GET, "").setFullUrl(getUrl).invoke();

            String templateUrl = "https://clientaccess.10086.cn/biz-orange/LN/uamrandcodelogin/login";

            UserInfoLoginReq obj = new UserInfoLoginReq();
            obj.setCellNum(response.getPageContent());
            obj.setImei("869782021770311");
            obj.setSendSmsFlag("0");
            obj.setVerifyCode(param.getSmsCode());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ak", "F4AA34B89513F0D087CA0EF11A3277469DC74905");
            params.put("cid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("city", "0668");
            params.put("ctid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("cv", "4.3.0");
            params.put("en", "0");
            params.put("imei", "869782021770311");
            params.put("nt", "3");
            params.put("prov", "200");
            params.put("reqBody", obj);
            params.put("sb", "Xiaomi");
            params.put("sn", "Mi Note 2");
            params.put("sp", "1080x1920");
            params.put("st", "1");
            params.put("sv", "7.0");
            params.put("t", "");
            params.put("tel", "99999999999");
            params.put("xc", "A2081");
            params.put("xk", "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");

            String xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity("", "60002", obj, "")) + "_Leadeon/SecurityOrganization", 32);

            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_app_002").setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("xs", xs).invoke();

            logger.info("输出：{},taskId={}", response.getPageContent(), param.getTaskId());

            String cookieString = StringUtils.EMPTY;
            List<NameValue> list = response.getHeaders();
            for (NameValue nameValue:list) {
                if (StringUtils.equals(nameValue.getName(),"Set-Cookie")) {
                    cookieString = nameValue.getValue();
                }
            }

            logger.info("输出：cookie={},taskId={}", cookieString, param.getTaskId());
            //String JSESSIONID = PatternUtils.group(cookieString,"JSESSIONID=([^;]+);",1);
            //String UID = PatternUtils.group(cookieString,"UID=([^;]+);",1);
            //String Comment = PatternUtils.group(cookieString,"Comment=([^;]+);",1);
            //Cookie cookie1 = new Cookie();
            //cookie1.setDomain("clientaccess.10086.cn");
            //cookie1.setName("JSESSIONID");
            //cookie1.setValue(JSESSIONID);
            //Cookie cookie2 = new Cookie();
            //cookie2.setDomain("clientaccess.10086.cn");
            //cookie2.setName("UID");
            //cookie2.setValue(UID);
            //Cookie cookie3 = new Cookie();
            //cookie3.setDomain("clientaccess.10086.cn");
            //cookie3.setName("Comment");
            //cookie3.setValue(Comment);
            //List<Cookie> cookies = new ArrayList<>();
            //cookies.add(cookie1);
            //cookies.add(cookie2);
            //cookies.add(cookie3);
            //
            //TaskUtils.saveCookie(param.getTaskId(),cookies);

            /**
             * 查询信用分
             */
            templateUrl = "https://clientaccess.10086.cn/biz-orange/SHS/userCredit/getCredit";
            obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());
            obj.setPageFlag("2");

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            params = new LinkedHashMap<>();
            params.put("ak", "F4AA34B89513F0D087CA0EF11A3277469DC74905");
            params.put("cid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("city", "0668");
            params.put("ctid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("cv", "4.3.0");
            params.put("en", "0");
            params.put("imei", "869782021770311");
            params.put("nt", "3");
            params.put("prov", "200");
            params.put("reqBody", obj);
            params.put("sb", "Xiaomi");
            params.put("sn", "Mi Note 2");
            params.put("sp", "1080x1920");
            params.put("st", "1");
            params.put("sv", "7.0");
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", "A2081");
            params.put("xk", "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");

            xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "5044", obj, param.getMobile().toString())) +
                    "_Leadeon/SecurityOrganization", 32);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_app_003").setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("xs", xs).addHeader("Cookie",cookieString)
                    .invoke();

            logger.info("输出(信用分)：{},taskId={}", response.getPageContent(), param.getTaskId());

            /**
             * 查询个人信息
             */
            templateUrl = "https://clientaccess.10086.cn/biz-orange/BN/userInformationService/getUserInformation";
            obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            params = new LinkedHashMap<>();
            params.put("ak", "F4AA34B89513F0D087CA0EF11A3277469DC74905");
            params.put("cid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("city", "0668");
            params.put("ctid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("cv", "4.3.0");
            params.put("en", "0");
            params.put("imei", "869782021770311");
            params.put("nt", "3");
            params.put("prov", "200");
            params.put("reqBody", obj);
            params.put("sb", "Xiaomi");
            params.put("sn", "Mi Note 2");
            params.put("sp", "1080x1920");
            params.put("st", "1");
            params.put("sv", "7.0");
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", "A2081");
            params.put("xk", "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");

            xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20007", obj, param.getMobile().toString())) +
                    "_Leadeon/SecurityOrganization", 32);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_app_004").setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("xs", xs).addHeader("Cookie",cookieString)
                    .invoke();

            logger.info("输出(个人信息)：{},taskId={}", response.getPageContent(), param.getTaskId());

            /**
             * 查询余额
             */
            templateUrl = "https://clientaccess.10086.cn/biz-orange/BN/realFeeQuery/getRealFee";
            obj = new UserInfoLoginReq();
            obj.setCellNum(param.getMobile().toString());

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            params = new LinkedHashMap<>();
            params.put("ak", "F4AA34B89513F0D087CA0EF11A3277469DC74905");
            params.put("cid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("city", "0668");
            params.put("ctid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("cv", "4.3.0");
            params.put("en", "0");
            params.put("imei", "869782021770311");
            params.put("nt", "3");
            params.put("prov", "200");
            params.put("reqBody", obj);
            params.put("sb", "Xiaomi");
            params.put("sn", "Mi Note 2");
            params.put("sp", "1080x1920");
            params.put("st", "1");
            params.put("sv", "7.0");
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", "A2081");
            params.put("xk", "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");

            xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20016", obj, param.getMobile().toString())) +
                    "_Leadeon/SecurityOrganization", 32);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_app_005").setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("xs", xs).addHeader("Cookie",cookieString)
                    .invoke();

            logger.info("输出(余额)：{},taskId={}", response.getPageContent(), param.getTaskId());

            /**
             * 查询账单
             */
            templateUrl = "https://clientaccess.10086.cn/biz-orange/BN/historyBillsService/getHistoryBills";
            BillReqBean billObj = new BillReqBean();
            billObj.setBgnMonth("2017-10");
            billObj.setCellNum(param.getMobile().toString());
            billObj.setEndMonth("2018-02");

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            params = new LinkedHashMap<>();
            params.put("ak", "F4AA34B89513F0D087CA0EF11A3277469DC74905");
            params.put("cid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("city", "0668");
            params.put("ctid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("cv", "4.3.0");
            params.put("en", "0");
            params.put("imei", "869782021770311");
            params.put("nt", "3");
            params.put("prov", "200");
            params.put("reqBody", billObj);
            params.put("sb", "Xiaomi");
            params.put("sn", "Mi Note 2");
            params.put("sp", "1080x1920");
            params.put("st", "1");
            params.put("sv", "7.0");
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", "A2081");
            params.put("xk", "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");

            xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20009", billObj, param.getMobile().toString())) +
                    "_Leadeon/SecurityOrganization", 32);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_app_006").setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("xs", xs).addHeader("Cookie",cookieString)
                    .invoke();

            logger.info("输出(账单)：{},taskId={}", response.getPageContent(), param.getTaskId());

            /**
             * 查询详单
             */
            templateUrl = "https://clientaccess.10086.cn/biz-orange/BN/queryDetail/getDetail";
            DetailReqBean detailObj = new DetailReqBean();
            detailObj.setBillMonth("2018-02");
            detailObj.setCellNum(param.getMobile().toString());
            detailObj.setPage(1);
            detailObj.setTmemType("02");
            detailObj.setUnit(2000);

            TaskUtils.addTaskShare(param.getTaskId(), "cookieString", cookieString);

            params = new LinkedHashMap<>();
            params.put("ak", "F4AA34B89513F0D087CA0EF11A3277469DC74905");
            params.put("cid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("city", "0668");
            params.put("ctid", "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=");
            params.put("cv", "4.3.0");
            params.put("en", "0");
            params.put("imei", "869782021770311");
            params.put("nt", "3");
            params.put("prov", "200");
            params.put("reqBody", detailObj);
            params.put("sb", "Xiaomi");
            params.put("sn", "Mi Note 2");
            params.put("sp", "1080x1920");
            params.put("st", "1");
            params.put("sv", "7.0");
            params.put("t", MD5Util.MD5(cookieString, 32));
            params.put("tel", param.getMobile().toString());
            params.put("xc", "A2081");
            params.put("xk", "2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");

            xs = MD5Util.MD5(templateUrl + "_" + JSON.toJSONString(getEntity(cookieString, "20012", detailObj, param.getMobile().toString())) +
                    "_Leadeon/SecurityOrganization", 32);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10086_app_007").setFullUrl(templateUrl)
                    .setRequestBody(JSON.toJSONString(params), ContentType.APPLICATION_JSON).addHeader("xs", xs).addHeader("Cookie",cookieString)
                    .invoke();

            logger.info("输出(详单)：{},taskId={}", response.getPageContent(), param.getTaskId());

            return result.failure();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    public RequestBean getEntity(String cookie, String code, Object obj, String phoneNum) {
        String cid = "lTCBX3oN8dvUy3/GSR2Sm/Gf9AdcsF2yq1wiQSBUBZUlOEkaHhg8ZBANqxIrb2JuIOkYB9E2REpDNcWnBzyqABIyveyVYD/0ap+sx0AGqj8=";
        RequestBean requestBean = new RequestBean();
        try {
            requestBean.setCid(cid);
            requestBean.setCtid(cid);
            requestBean.setCv("4.3.0");
            requestBean.setXc("A2081");
            requestBean.setXk("2b6b8c9c7c4ced5301d618797b94a6b5a20c021545c62b9a4ad15568591693d7968bbb73");
            if ("60001".equals(code) || "60010".equals(code)) {
                requestBean.setEn("3");
            } else {
                requestBean.setEn("0");
            }
            requestBean.setSn("Mi Note 2");
            requestBean.setSp("1080x1920");
            requestBean.setSt("1");
            requestBean.setSv("7.0");
            requestBean.setAk("F4AA34B89513F0D087CA0EF11A3277469DC74905");
            if ("".equals(cookie)) {
                requestBean.setT("");
            } else {
                requestBean.setT(MD5Util.MD5(cookie, 32));
            }
            if (StringUtils.isEmpty(phoneNum)) {
                requestBean.setTel("99999999999");
            } else {
                requestBean.setTel(phoneNum);
            }
            requestBean.setImei("869782021770311");
            requestBean.setSb("Xiaomi");
            requestBean.setNt("3");
            String cityId = "0668";
            String proId = "200";
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

    public static class RequestBean {
        private String ak;
        private String cid;
        private String city;
        private String ctid;
        private String cv;
        private String en;
        private String imei;
        private String nt;
        private String prov;
        private Object reqBody;
        private String sb;
        private String sn;
        private String sp;
        private String st;
        private String sv;
        private String f4306t;
        private String tel;
        private String xc;
        private String xk;

        public String getCtid() {
            return this.ctid;
        }

        public void setCtid(String ctid) {
            this.ctid = ctid;
        }

        public String getAk() {
            return this.ak;
        }

        public void setAk(String ak) {
            this.ak = ak;
        }

        public String getCid() {
            return this.cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getEn() {
            return this.en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        public String getT() {
            return this.f4306t;
        }

        public void setT(String t) {
            this.f4306t = t;
        }

        public String getSn() {
            return this.sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getCv() {
            return this.cv;
        }

        public void setCv(String cv) {
            this.cv = cv;
        }

        public String getSt() {
            return this.st;
        }

        public void setSt(String st) {
            this.st = st;
        }

        public String getSv() {
            return this.sv;
        }

        public void setSv(String sv) {
            this.sv = sv;
        }

        public String getSp() {
            return this.sp;
        }

        public void setSp(String sp) {
            this.sp = sp;
        }

        public String getXk() {
            return this.xk;
        }

        public void setXk(String xk) {
            this.xk = xk;
        }

        public String getXc() {
            return this.xc;
        }

        public void setXc(String xc) {
            this.xc = xc;
        }

        public Object getReqBody() {
            return this.reqBody;
        }

        public void setReqBody(Object reqBody) {
            this.reqBody = reqBody;
        }

        public String getImei() {
            return this.imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getSb() {
            return this.sb;
        }

        public void setSb(String sb) {
            this.sb = sb;
        }

        public String getNt() {
            return this.nt;
        }

        public void setNt(String nt) {
            this.nt = nt;
        }

        public String getTel() {
            return this.tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getProv() {
            return this.prov;
        }

        public void setProv(String prov) {
            this.prov = prov;
        }

        public String getCity() {
            return this.city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}
