package com.datatrees.rawdatacentral.plugin.operator.hai_nan_10000_web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginPostService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 海南电信--web版
 * 登陆地址:http://login.189.cn
 * 登陆方式:服务密码登陆
 * 图片验证码:支持
 * Created by guimeichao on 17/12/04.
 */
public class HaiNan10000ForWeb implements OperatorPluginPostService {

    private static final Logger logger = LoggerFactory.getLogger(HaiNan10000ForWeb.class);

    LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCodeForLogin(param);
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
        switch (param.getFormType()) {
            case "BILL_DETAIL":
                return processForBill(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }
            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.hi.189.cn/BUFFALO/buffalo/CommonAjaxService";
            String templateData = "<buffalo-call><method>getSmsCode</method><map><type>java.util" +
                    ".HashMap</type><string>PHONENUM</string><string>{}</string><string>PRODUCTID</string><string>50</string><string>RTYPE</string><string>QD</string></map></buffalo-call>";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("短信随机密码已经发到您的联系电话")) {
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
        String userid = TaskUtils.getTaskShare(param.getTaskId(), "userid");
        String prodnum = TaskUtils.getTaskShare(param.getTaskId(), "prodnum");
        String citycode = TaskUtils.getTaskShare(param.getTaskId(), "citycode");
        String prodid = TaskUtils.getTaskShare(param.getTaskId(), "prodid");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");

        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.hi.189.cn/BUFFALO/buffalo/FeeQueryAjaxV4Service";
            String templateData = "<buffalo-call><method>queryDetailBill</method><map><type>java.util" +
                    ".HashMap</type><string>PRODNUM</string><string>{}</string><string>CITYCODE</string><string>{}</string><string>QRYDATE</string" +
                    "><string>{}</string><string>TYPE</string><string>8</string><string>PRODUCTID</string><string>{}</string><string>CODE</string" +
                    "><string>{}</string><string>USERID</string><string>{}</string></map></buffalo-call>";
            String data = TemplateUtils.format(templateData, prodnum, citycode, format.format(new Date()), prodid, param.getSmsCode(), userid);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.isBlank(pageContent) || pageContent.contains("短信验证码不正确")) {
                logger.warn("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            } else {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> loginPost(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            /**
             * 获取查询权限
             */
            String templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10022&toStUrl=http://hi.189.cn/service/thesame/billing.jsp?TABNAME=zdcx&fastcode=02091576&cityCode=hi";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            templateUrl
                    = "http://www.189.cn/dqmh/ssoLink.do?method=linkTo&platNo=10022&toStUrl=http://hi.189.cn/service/thesame/balanceChanges.jsp?TABNAME=yecx&fastcode=02091574&cityCode=hi";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();

            templateUrl = "http://hi.189.cn/service/bill/feequery.jsp?TABNAME=xdcx&fastcode=02091577&cityCode=hi";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();

            String citycode = PatternUtils.group(pageContent, "var citycode=\"(\\d+)\"", 1);
            String prodid = PatternUtils.group(pageContent, "value='SHOUJI\\|" + param.getMobile() + "\\|(\\d+)\\|\\d+\\|[^\\|]+\\|[^']+", 1);
            String prodcode = PatternUtils.group(pageContent, "value='SHOUJI\\|" + param.getMobile() + "\\|\\d+\\|(\\d+)\\|[^\\|]+\\|[^']+", 1);
            String prodnum = PatternUtils.group(pageContent, "value='SHOUJI\\|" + param.getMobile() + "\\|\\d+\\|\\d+\\|[^\\|]+\\|([^']+)", 1);
            String userid = PatternUtils.group(pageContent, "var userid=\"(\\d+)\"", 1);

            TaskUtils.addTaskShare(param.getTaskId(), "citycode", citycode + "");
            TaskUtils.addTaskShare(param.getTaskId(), "prodid", prodid);
            TaskUtils.addTaskShare(param.getTaskId(), "prodcode", prodcode);
            TaskUtils.addTaskShare(param.getTaskId(), "prodnum", prodnum);
            TaskUtils.addTaskShare(param.getTaskId(), "userid", userid);

            templateUrl = "http://hi.189.cn/webgo/thesame/myBill";
            String data = "objectNum=" + param.getMobile().toString() + "&objectType=%E6%89%8B%E6%9C%BA&queryType=2";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            TaskUtils.addTaskShare(param.getTaskId(), "balancePageContent", response.getPageContent());

            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
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
            String templateUrl = "http://hi.189.cn/webgo/thesame/billing";
            String templateData = "objectNum={}&queryMonth={}";
            String data = TemplateUtils.format(templateData, param.getMobile(), billMonth);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            return result.success(response.getPageContent());
        } catch (Exception e) {
            logger.error("账单页访问失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
}
