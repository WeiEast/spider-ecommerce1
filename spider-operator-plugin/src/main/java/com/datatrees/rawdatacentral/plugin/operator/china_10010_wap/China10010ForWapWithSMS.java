package com.datatrees.rawdatacentral.plugin.operator.china_10010_wap;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/10/27.
 */
public class China10010ForWapWithSMS implements OperatorPluginService {

    private static Logger logger = LoggerFactory.getLogger(China10010ForWapWithSMS.class);

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

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://uac.10010.com/oauth2/new_auth?display=wap&page_type=05&app_code=ECS-YH-WAP&redirect_uri=http" +
                    "://wap.10010.com/t/loginCallBack.htm&state=http://wap.10010.com/t/myunicom.htm&channel_code=113000001";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            templateUrl = "https://uac.10010.com/portal/Service/CheckNeedVerify?callback=&userName={}&pwdType=01";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, param.getMobile())
                    .invoke();

            templateUrl = "https://uac.10010.com/portal/Service/SendCkMSG?callback=&req_time={}&mobile={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis(), param.getMobile()).invoke();
            if (StringUtils.contains(response.getPageContent(), "resultCode:\"0000\"")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            }
            logger.error("登录-->短信验证码-->刷新失败,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://uac.10010.com/oauth2/new_auth?req_time={}";
            String templateData = "app_code=ECS-YH-WAP&user_id={}&user_pwd={}&user_type=01&pwd_type=01&display=web&response_type" +
                    "=code&redirect_uri=http%3A%2F%2Fwap.10010.com%2Ft%2FloginCallBack.htm&is_check=1&verifyCKCode={}" +
                    "&state=http%3A%2F%2Fwap.10010.com%2Ft%2Fmyunicom.htm";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getPassword(), param.getSmsCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, System.currentTimeMillis()).setRequestBody(data).invoke();
            JSONObject json = response.getPageContentForJSON();
            String code = json.getString("rsp_code");
            if (!StringUtils.equals(code, "0000")) {
                String errorMessage = json.getString("rsp_desc");
                logger.error("登陆失败,{},param={}", errorMessage, param);
                return result.failure(errorMessage);
            }
            String code2 = json.getString("code");
            templateUrl = "http://wap.10010.com/t/loginCallBack.htm?code={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, code2).invoke();

            /**
             * 查询归属地
             */
            templateUrl = "http://wap.10010.com/mobileService/customerService/queryAffiliationPlace.htm?desmobile=&version=wap@4.0";
            templateData = "mobile_id={}";
            data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            if (!StringUtils.contains(response.getPageContent(), "很抱歉，暂时无法为您提供服务，请稍后再试")) {
                String provinceName = XPathUtil.getXpath("th:contains(号码归属地):not(:has(th))+td/text()", response.getPageContent()).get(0).trim();
                provinceName = PatternUtils.group(provinceName, "^([^ ]+)\\s*", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "provinceName", provinceName);
            } else {
                templateUrl = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?cb=&resource_name=guishudi&query={}&_=";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                        .setFullUrl(templateUrl, param.getMobile()).invoke();
                String prov = (String) JSONPath.eval(response.getPageContentForJSON(), "$.data[0].prov");
                String city = (String) JSONPath.eval(response.getPageContentForJSON(), "$.data[0].city");
                String provinceName = prov;
                if (StringUtils.isBlank(prov)) {
                    provinceName = city;
                } else if (StringUtils.isBlank(city)) {
                    logger.info("调用百度归属地查询失败，taskId={}，pageContent={}", param.getTaskId(), response.getPageContent());
                    return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
                }
                TaskUtils.addTaskShare(param.getTaskId(), "provinceName", provinceName);
            }
            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->校验-->失败,param={},response={}", param, response, e);
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
            String templateUrl = "http://wap.10010.com/mobileService/query/queryRealFeeHistroyDetail" +
                    ".htm?desmobile=&version=android@5.5&menuId=000200010005&month={}&randm=";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, billMonth).invoke();
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
            String templateUrl = "http://wap.10010.com/mobileService/query/getPhoneByDetailContent.htm";
            String templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=";
            String data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
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
                logger.info("当月通话详单总条数：{},taskId={}", totalRaw, param.getTaskId());
                if (Integer.parseInt(totalRaw) == 0) {
                    logger.info("无通话记录，原始页面为：{},taskId={}", pageContent, param.getTaskId());
                }
                int pages = Integer.parseInt(totalRaw) / 40;
                list = new ArrayList<>();
                for (int i = 0; i <= pages; i++) {
                    templateUrl = "http://wap.10010.com/mobileService/view/client/query/xdcx/thxd_more_list" +
                            ".jsp?1=1&t={}&beginrow={}&endrow={}&pagenum={}";
                    response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                            .setFullUrl(templateUrl, System.currentTimeMillis(), (40 * i), (40 * (i + 1)), (i + 1)).invoke();
                    list.add(response.getPageContent());
                }
            } else if (StringUtils.contains(pageContent, "id=\"totalNum\"")) {
                int totalPage = Integer.parseInt(XPathUtil.getXpath("//input[@id='totalNum']/@value", pageContent).get(0));
                list = new ArrayList<>();
                list.add(pageContent);
                for (int i = 2; i <= totalPage; i++) {
                    templateUrl = "http://wap.10010.com/mobileService/query/getPhoneByDetailContent.htm";
                    templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=000200010005&currNum={}";
                    data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
                    response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                            .setRequestBody(data).invoke();
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
            String templateUrl = "http://wap.10010.com/mobileService/query/querySmsByDetailContent.htm";
            String templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=";
            String data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
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
                templateUrl = "http://wap.10010.com/mobileService/view/client/query/xdcx/sms_more_list.jsp?1=1&t=" + System.currentTimeMillis() +
                        "&beginrow=" + (100 * i) + "&endrow=" + (100 * (i + 1)) + "&pagenum=" + (i + 1);
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
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
            String templateUrl = "http://wap.10010.com/mobileService/query/queryNetWorkDetailContent.htm";
            String templateData = "t={}&YYYY={}&MM={}&DD=&queryMonthAndDay=month&menuId=";
            String data = TemplateUtils.format(templateData, System.currentTimeMillis(), year, month);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
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
                    templateUrl = "http://wap.10010.com/mobileService/view/client/query/xdcx/net_more_list.jsp?1=1&t=" + System.currentTimeMillis() +
                            "&beginrow=" + (40 * i) + "&endrow=" + (40 * (i + 1)) + "&pagenum=" + (i + 1);
                    response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
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
