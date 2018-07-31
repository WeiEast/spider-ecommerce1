package com.datatrees.rawdatacentral.plugin.operator.hu_bei_10086_web;

import javax.script.Invocable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.http.ScriptEngineUtil;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.operator.domain.OperatorParam;
import com.datatrees.spider.operator.service.plugin.OperatorLoginPostPlugin;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.gson.reflect.TypeToken;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/8/30.
 */
public class HuBei10086ForWebWithSms implements OperatorLoginPostPlugin {

    private static final Logger logger = LoggerFactory.getLogger(HuBei10086ForWebWithSms.class);

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
            case "CALL_DETAILS":
                return processForDetails(param, "GSM");
            case "SMS_DETAILS":
                return processForDetails(param, "SMS");
            case "NET_DETAILS":
                return processForDetails(param, "GPRSWLAN");
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        /**
         * 这里不一定有图片验证码,随机出现
         */
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://hb.ac.10086.cn/SSO/img?codeType=0&rand={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET)
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://hb.ac.10086.cn/SSO/sendsms";
            String templateData = "username={}&service=my&passwordType=2";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            if (StringUtils.contains(response.getPageContent(), "随机短信已经发送到您的手机")) {
                logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("登录-->短信验证码-->刷新失败,param={}", param);
                return result.failure(ErrorCode.REFESH_SMS_ERROR);
            }
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "https://hb.ac.10086.cn/SSO/loginbox?service=servicenew&style=mymobile&continue=http://www" +
                    ".hb.10086.cn/servicenew/index.action";
            String templateUrl = "https://hb.ac.10086.cn/SSO/loginbox";
            String templateData = "accountType=0&username={}&passwordType=2&password={}&smsRandomCode={}&emailusername" +
                    "=%E8%AF%B7%E8%BE%93%E5%85%A5%E7%99%BB%E5%BD%95%E5%B8%90%E5%8F%B7&emailpassword=&validateCode={}&action" +
                    "=%2FSSO%2Floginbox&style=mymobile&service=my&continue=http%3A%2F%2Fwww.hb.10086.cn%3A80%2Fmy%2Findex" +
                    ".action&submitMode=login&guestIP=s&isCompelLogin=0";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getPassword(), param.getSmsCode(), param.getPicCode());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            String errorMsg = StringUtils.EMPTY;
            List<String> errorMsgList = XPathUtil.getXpath("//input[@id='login_title']/@value", pageContent);
            if (!errorMsgList.isEmpty()) {
                errorMsg = errorMsgList.get(0);
            }
            if (StringUtils.isBlank(errorMsg)) {
                TaskUtils.addTaskShare(param.getTaskId(), "pageContentLogin", pageContent);
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,{},param={},response={}", errorMsg, param, response);
                return result.failure(errorMsg);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private String processSSOLogin(OperatorParam param, String pageContent) throws UnsupportedEncodingException {
        String action = "";
        String relayState = "";
        String samLart = "";
        String passwordType = "";
        String accountType = "";
        String errorMsg = "";
        String errFlag = "";
        String telNum = "";
        String artifact = "";

        List<String> actionList = XPathUtil.getXpath("//form[@id='sso']/@action", pageContent);
        for (String string : actionList) {
            action = string;
        }
        List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
        for (String string : relayStateList) {
            relayState = string;
        }
        List<String> samLartList = XPathUtil.getXpath("//input[@name='SAMLart']/@value", pageContent);
        for (String string : samLartList) {
            samLart = string;
        }
        List<String> artifactList = XPathUtil.getXpath("//input[@name='artifact']/@value", pageContent);
        for (String string : artifactList) {
            artifact = string;
        }
        List<String> passwordTypeList = XPathUtil.getXpath("//input[@name='PasswordType']/@value", pageContent);
        for (String string : passwordTypeList) {
            passwordType = string;
        }
        List<String> accountTypeList = XPathUtil.getXpath("//input[@name='accountType']/@value", pageContent);
        for (String string : accountTypeList) {
            accountType = string;
        }
        List<String> errorMsgList = XPathUtil.getXpath("//input[@name='errorMsg']/@value", pageContent);
        for (String string : errorMsgList) {
            errorMsg = string;
        }
        List<String> errFlagList = XPathUtil.getXpath("//input[@name='errFlag']/@value", pageContent);
        for (String string : errFlagList) {
            errFlag = string;
        }
        List<String> telNumList = XPathUtil.getXpath("//input[@name='telNum']/@value", pageContent);
        for (String string : telNumList) {
            telNum = string;
        }
        if (StringUtils.isEmpty(samLart)) {
            logger.info("request sso login url error! samLart is empty! pageContent: " + pageContent);
            return null;
        }
        String templateUrl = "{}?timeStamp={}&RelayState={}&SAMLart={}&artifact={}&accountType={}&PasswordType={}&errorMsg={}&errFlag={}&telNum={}";
        Response response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                .setFullUrl(templateUrl, action, System.currentTimeMillis(), relayState, samLart, artifact, accountType, passwordType, errorMsg,
                        errFlag, telNum).invoke();

        return response.getPageContent();
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String count = TaskUtils.getTaskShare(param.getTaskId(), "sendSmsCount");
            if (Integer.parseInt(count) >= 2) {
                String lastSendTime = TaskUtils.getTaskShare(param.getTaskId(), "lastSendTime");
                long waitTime = Long.parseLong(lastSendTime) + 60000 - System.currentTimeMillis();
                Thread.sleep(waitTime);
            }
            String templateUrl = "http://www.hb.10086.cn/my/balance/smsRandomPass!sendSmsCheckCode.action?menuid=myDetailBill";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("\"result\":\"1\"")) {
                int newCount = Integer.parseInt(count) + 1;
                TaskUtils.addTaskShare(param.getTaskId(), "sendSmsCount", String.valueOf(newCount));
                TaskUtils.addTaskShare(param.getTaskId(), "lastSendTime", String.valueOf(System.currentTimeMillis()));
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
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
        String rsaEmpoent = TaskUtils.getTaskShare(param.getTaskId(), "rsaEmpoent");
        String rsaModule = TaskUtils.getTaskShare(param.getTaskId(), "rsaModule");
        if (StringUtils.isBlank(rsaModule) || StringUtils.isBlank(rsaEmpoent)) {
            logger.error("详单-->校验失败,没有rsaModule或rsaEmpoent,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        }
        try {
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            invocable.invokeFunction("setKey", rsaEmpoent, "", rsaModule);
            String encryptPwd = (String) invocable.invokeFunction("encryptedString", param.getPassword());
            String encryptSmsCode = (String) invocable.invokeFunction("encryptedString", param.getSmsCode());
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            String templateUrl
                    = "http://www.hb.10086.cn/my/detailbill/detailBillQry.action?postion=outer&detailBean.billcycle={}&detailBean.selecttype=0&detailBean.flag=GSM&selecttype=%E5%85%A8%E9%83%A8%E6%9F%A5%E8%AF%A2&flag=%E9%80%9A%E8%AF%9D%E8%AF%A6%E5%8D%95&detailBean.password={}&detailBean.chkey={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, sf.format(new Date()), encryptPwd, encryptSmsCode).invoke();
            String pageContent = response.getPageContent();
            if (!pageContent.contains("暂时无法为您提供服务") && !pageContent.contains("您输入的服务密码或短信验证码错误或者过期")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.warn("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Object> processForDetails(OperatorParam param, String queryType) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String[] params = paramMap.get("page_content").split(":");
        Response response = null;
        try {
            String content = "{\"data\":[";
            StringBuilder stringBuilder = new StringBuilder(content);
            String smsCode = TaskUtils.getTaskShare(param.getTaskId(), RedisKeyPrefixEnum.TASK_SMS_CODE.getRedisKey(FormType.VALIDATE_BILL_DETAIL));

            String templateUrl = "http://www.hb.10086.cn/my/detailbill/generateNewDetailExcel.action?menuid=myDetailBill&detailBean" +
                    ".billcycle={}&detailBean.password={}&detailBean.chkey={}&detailBean.startdate={}&detailBean" +
                    ".enddate={}&detailBean.flag={}&detailBean.selecttype=0";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST)
                    .setFullUrl(templateUrl, params[0], param.getPassword(), smsCode, params[1], params[2], queryType).invoke();
            byte[] bytes = response.getResponse();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            String str = getJsonBody(in, queryType);
            if (StringUtils.isNotBlank(str)) {
                stringBuilder.append(str + ",");
            }
            if (stringBuilder.length() > 9) {
                stringBuilder.substring(0, stringBuilder.length() - 1);
            }
            stringBuilder.append("]}");
            map.put("pageContent", stringBuilder.toString());
            map.put("pageContentFile", bytes);
            return result.success(map);
        } catch (Exception e) {
            logger.error("解析详单PDF失败,param={},response={}", param, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private String getJsonBody(InputStream inputStream, String queryType) throws IOException {
        try {
            // 设置读文件编码
            WorkbookSettings setEncode = new WorkbookSettings();
            // 从文件流中获取Excel工作区对象（WorkBook）
            Workbook wb = Workbook.getWorkbook(inputStream, setEncode);
            Sheet sheet = wb.getSheet(0);
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < sheet.getRows(); j++) {
                if (StringUtils.equals("GSM", queryType)) {
                    if (sheet.getCell(0, j).getContents().trim().matches("\\d{4}-\\d+-\\d+\\s*\\d{2}:\\d{2}:\\d{2}")) {
                        stringBuilder.append("{\"callStartDateTime\":\"" + sheet.getCell(0, j).getContents().trim() + "\",\"callLocation\":\"" +
                                sheet.getCell(1, j).getContents().trim() + "\",\"callType\":\"" + sheet.getCell(2, j).getContents().trim() +
                                "\",\"otherTelNum\":\"" + sheet.getCell(3, j).getContents().trim() + "\",\"callDuration\":\"" +
                                sheet.getCell(4, j).getContents().trim() + "\",\"callTypeDetail\":\"" + sheet.getCell(5, j).getContents().trim() +
                                "\",\"totalFee\":\"" + sheet.getCell(6, j).getContents().trim() + "\"},");
                    }
                } else if (StringUtils.equals("SMS", queryType)) {
                    if (sheet.getCell(0, j).getContents().trim().matches("\\d{4}-\\d+-\\d+\\s*\\d{2}:\\d{2}:\\d{2}")) {
                        stringBuilder.append("{\"smsDateTime\":\"" + sheet.getCell(0, j).getContents().trim() + "\",\"otherNum\":\"" +
                                sheet.getCell(2, j).getContents().trim() + "\",\"smsType\":\"" + sheet.getCell(3, j).getContents().trim() +
                                "\",\"businessType\":\"" + sheet.getCell(4, j).getContents().trim() + "\",\"fee\":\"" +
                                sheet.getCell(6, j).getContents().trim() + "\"},");
                    }
                } else if (StringUtils.equals("GPRSWLAN", queryType)) {
                    if (sheet.getCell(0, j).getContents().trim().matches("\\d{4}-\\d+-\\d+\\s*\\d{2}:\\d{2}:\\d{2}")) {
                        stringBuilder.append("{\"netStartDateTime\":\"" + sheet.getCell(0, j).getContents().trim() + "\",\"netLocation\":\"" +
                                sheet.getCell(1, j).getContents().trim() + "\",\"businessType\":\"" + sheet.getCell(2, j).getContents().trim() +
                                "\",\"netDuration\":\"" + sheet.getCell(3, j).getContents().trim() + "\",\"totalFlow\":\"" +
                                sheet.getCell(4, j).getContents().trim() + "\",\"totalFee\":\"" + sheet.getCell(6, j).getContents().trim() + "\"},");
                    }
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.substring(0, stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
        }
        return null;
    }

    @Override
    public HttpResult<Map<String, Object>> loginPost(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String pageContent = TaskUtils.getTaskShare(param.getTaskId(), "pageContentLogin");
            pageContent = processSSOLogin(param, pageContent);
            String templateUrl = PatternUtils.group(pageContent, "window\\.parent\\.location\\.href='([^']+)'", 1);
            if (StringUtils.isNotEmpty(templateUrl)) {
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            }
            String rsaModule
                    = "8a4928b7e4ce5943230539120cb6ee7a64000034b11b923a91faf8c381dd09b4a9a9a6fa02ca0bd3b90576ac1498983f7c78d8f8f5126a24a30f75eac86815c3430fe3e77f81a326d0d2f7ffbfe285bb368175d66c29777ec031c0c75f64da92aa43866fdfa2597cfb4ce614f450e95670be7cc27e4b05b7a48ca876305e5d51";
            String rsaEmpoent = "10001";
            templateUrl = "http://www.hb.10086.cn/my/index.action";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent)) {
                List<String> rsaModuleList = XPathUtil.getXpath("//input[@id='rsaModule']/@value", pageContent);
                for (String string : rsaModuleList) {
                    if (StringUtils.isNotBlank(string)) {
                        rsaModule = string;
                    }
                }
                List<String> rsaEmpoentList = XPathUtil.getXpath("//input[@id='rsaEmpoent']/@value", pageContent);
                for (String string : rsaEmpoentList) {
                    if (StringUtils.isNotBlank(string)) {
                        rsaEmpoent = string;
                    }
                }
            }
            TaskUtils.addTaskShare(param.getTaskId(), "rsaModule", rsaModule);
            TaskUtils.addTaskShare(param.getTaskId(), "rsaEmpoent", rsaEmpoent);
            TaskUtils.addTaskShare(param.getTaskId(), "sendSmsCount", "0");
            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
