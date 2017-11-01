package com.datatrees.rawdatacentral.plugin.operator.china_10010_app;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/10/27.
 */
public class China10010ForApp implements OperatorPluginService {

    private static Logger logger = LoggerFactory.getLogger(China10010ForApp.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
        switch (param.getFormType()) {
            case "BILL_DETAILS":
                return processForBill(param);
            case "CALL_DETAILS":
                return processForCallDetails(param);
            case "SMS_DETAILS":
                return processForSmsDetails(param);
            case "NET_DETAILS":
                return processForNetDetails(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String mobile = param.getMobile().toString();
            String passWord = param.getPassword();
            Random T = new Random();
            String U = (new StringBuilder()).append(T.nextInt(9)).append("").append(T.nextInt(9)).append("").append(T.nextInt(9)).append("")
                    .append(T.nextInt(9)).append("").append(T.nextInt(9)).append("").append(T.nextInt(9)).toString();
            mobile = (new StringBuilder()).append(mobile).append(U).toString();
            passWord = (new StringBuilder()).append(passWord).append(U).toString();
            String templateUrl = "http://m.client.10010.com/mobileService/login.htm";
            String templateData = "deviceOS=android6.0.1&mobile={}&netWay=WIFI&deviceCode=&isRemberPwd=false&version=android%405.2&deviceId" +
                    "=&password={}&keyVersion=&provinceChanel=general&deviceModel=Mi+Note+2&deviceBrand=Xiaomi&timestamp={}";
            String data = TemplateUtils
                    .format(templateData, EncryptUtilForChina10010App.encryString(mobile), EncryptUtilForChina10010App.encryString(passWord),
                            System.currentTimeMillis());
            response = TaskHttpClient.create(param, RequestType.POST, "china_10010_app_001").setFullUrl(templateUrl).setRequestBody(data).invoke();
            JSONObject json = response.getPageContentForJSON();
            String code = json.getString("code");
            if (StringUtils.equals(code, "0")) {
                String provinceName = PatternUtils.group(response.getPageContent(), "proName\":\"([^\"]+)\"", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "provinceName", provinceName);
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("基本信息-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        }
    }

    private HttpResult<Object> processForBill(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String billMonth = paramMap.get("page_content");
        Response response = null;
        try {
            /**
             * 获取月账单
             */
            String templateUrl
                    = "https://m.client.10010.com/mobileService/query/queryRealFeeHistroyDetail.htm?desmobile=&version=android@5.4&menuId=000200010005&month={}&randm=";
            response = TaskHttpClient.create(param, RequestType.GET, "china_10010_app_002").setFullUrl(templateUrl, billMonth).invoke();
            String pageContent = response.getPageContent();
            return result.success(pageContent);
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForCallDetails(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String billMonth = paramMap.get("page_content");
        String year = PatternUtils.group(billMonth, "(\\d{4})\\d{2}", 1);
        String month = PatternUtils.group(billMonth, "\\d{4}(\\d{2})", 1);
        Response response = null;
        try {
            /**
             * 获取通话详单
             */
            String templateUrl = "https://m.client.10010.com/mobileService/query/getPhoneByDetailContent.htm";
            String templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=";
            String data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10010_app_003").setFullUrl(templateUrl).setRequestBody(data).invoke();
            String pageContent = response.getPageContent();

            Map<String, Object> map = new HashMap<>();
            List<String> list = null;
            if (StringUtils.contains(pageContent, "thxd_more_list")) {
                String totalRaw = "0";
                List<String> totalRawList = XPathUtil.getXpath("p:contains(总通话):not(:has(p)) span/text()", pageContent);
                if (!totalRawList.isEmpty()) {
                    String temp = totalRawList.get(0);
                    String str = PatternUtils.group(temp, "(\\d+)", 1);
                    if (StringUtils.isNotBlank(str)) {
                        totalRaw = str;
                    }
                }
                logger.info("当月通话详单总条数： " + totalRaw);
                int pages = Integer.parseInt(totalRaw) / 40;
                list = new ArrayList<>();
                for (int i = 0; i <= pages; i++) {
                    templateUrl = "https://m.client.10010.com/mobileService/view/client/query/xdcx/thxd_more_list" +
                            ".jsp?1=1&t={}&beginrow={}&endrow={}&pagenum={}";
                    response = TaskHttpClient.create(param, RequestType.GET, "china_10010_app_004")
                            .setFullUrl(templateUrl, System.currentTimeMillis(), (40 * i), (40 * (i + 1)), (i + 1)).invoke();
                    list.add(response.getPageContent());
                }
            } else if (StringUtils.contains(pageContent, "id=\"totalNum\"")) {
                int totalPage = Integer.parseInt(XPathUtil.getXpath("//input[@id='totalNum']/@value", pageContent).get(0));
                list = new ArrayList<>();
                list.add(pageContent);
                for (int i = 2; i <= totalPage; i++) {
                    templateUrl = "https://m.client.10010.com/mobileService/query/getPhoneByDetailContent.htm";
                    templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=000200010005&currNum={}";
                    data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
                    response = TaskHttpClient.create(param, RequestType.POST, "china_10010_app_005").setFullUrl(templateUrl).setRequestBody(data)
                            .invoke();
                    list.add(response.getPageContent());
                }
            }
            map.put("data", list);
            return result.success(JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForSmsDetails(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String billMonth = paramMap.get("page_content");
        String year = PatternUtils.group(billMonth, "(\\d{4})\\d{2}", 1);
        String month = PatternUtils.group(billMonth, "\\d{4}(\\d{2})", 1);
        Response response = null;
        try {
            /**
             * 获取短信详单
             */
            String templateUrl = "https://m.client.10010.com/mobileService/query/querySmsByDetailContent.htm";
            String templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=";
            String data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10010_app_006").setFullUrl(templateUrl).setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            String totalRaw = "0";
            List<String> totalRawList = XPathUtil.getXpath("p:contains(总发送):not(:has(p)) span/text()", pageContent);
            if (!totalRawList.isEmpty()) {
                String temp = totalRawList.get(0);
                String str = PatternUtils.group(temp, "(\\d+)", 1);
                if (StringUtils.isNotBlank(str)) {
                    totalRaw = str;
                }
            }
            int pages = Integer.parseInt(totalRaw) / 100;
            Map<String, Object> map = new HashMap<>();
            List<String> list = new ArrayList<>();
            for (int i = 0; i <= pages; i++) {
                templateUrl = "https://m.client.10010.com/mobileService/view/client/query/xdcx/sms_more_list.jsp?1=1&t=" +
                        System.currentTimeMillis() + "&beginrow=" + (100 * i) + "&endrow=" + (100 * (i + 1)) + "&pagenum=" + (i + 1);
                response = TaskHttpClient.create(param, RequestType.GET, "china_10010_app_007")
                        .setFullUrl(templateUrl, System.currentTimeMillis(), (100 * i), (100 * (i + 1)), (i + 1)).invoke();
                list.add(response.getPageContent());
            }
            map.put("data", list);
            return result.success(JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private HttpResult<Object> processForNetDetails(OperatorParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String billMonth = paramMap.get("page_content");
        String year = PatternUtils.group(billMonth, "(\\d{4})\\d{2}", 1);
        String month = PatternUtils.group(billMonth, "\\d{4}(\\d{2})", 1);
        Response response = null;
        try {
            /**
             * 获取流量详单
             */
            String templateUrl = "https://m.client.10010.com/mobileService/query/queryNetWorkDetailContent.htm";
            String templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=";
            String data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
            response = TaskHttpClient.create(param, RequestType.POST, "china_10010_app_008").setFullUrl(templateUrl).setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            int totalRaw = 0;
            String str = PatternUtils.group(pageContent, "点击加载更多（(\\d+)条）", 1);
            if (StringUtils.isNotBlank(str)) {
                totalRaw = 40 + Integer.parseInt(str);
            }
            Map<String, Object> map = new HashMap<>();
            List<String> list = new ArrayList<>();
            if (totalRaw > 40) {
                int pages = totalRaw / 40;
                for (int i = 0; i <= pages; i++) {
                    templateUrl = "https://m.client.10010.com/mobileService/view/client/query/xdcx/net_more_list.jsp?1=1&t=" +
                            System.currentTimeMillis() + "&beginrow=" + (40 * i) + "&endrow=" + (40 * (i + 1)) + "&pagenum=" + (i + 1);
                    response = TaskHttpClient.create(param, RequestType.GET, "china_10010_app_009")
                            .setFullUrl(templateUrl, System.currentTimeMillis(), (40 * i), (40 * (i + 1)), (i + 1)).invoke();
                    list.add(response.getPageContent());
                }
            } else {
                list.add(pageContent);
            }
            map.put("data", list);
            return result.success(JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
}
