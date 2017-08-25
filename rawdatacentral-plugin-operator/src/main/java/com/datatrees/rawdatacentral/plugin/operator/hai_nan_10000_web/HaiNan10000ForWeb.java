package com.datatrees.rawdatacentral.plugin.operator.hai_nan_10000_web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 海南电信--web版
 * 登陆地址:http://www.189.cn/login/index/box/uam.do
 * 登陆方式:服务密码登陆
 * 图片验证码:支持
 * Created by guimeichao on 17/8/24.
 */
public class HaiNan10000ForWeb implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(HaiNan10000ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            /**
             * 获取登录需要用到的参数
             */
            String templateUrl = "http://www.189.cn/login/index/box/uam.do";
            Response response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10000_web_001").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();

            String lt = XPathUtil.getXpath("//input[@name='lt']/@value", pageContent).get(0);
            if (StringUtils.isBlank(lt)) {
                logger.error("登录-->初始化失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }

            TaskUtils.addTaskShare(param.getTaskId(), "lt", lt);
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

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://uam.ct10000.com/ct10000uam/validateImg.jsp";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "hai_nan_10000_web_002").setFullUrl(templateUrl).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        String lt = TaskUtils.getTaskShare(param.getTaskId(), "lt");
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://uam.ct10000.com/ct10000uam/login?service=http://www.189.cn:80/login/uam.do&returnURL=1&register=register2.0&UserIp=";
            String templateData = "forbidpass=null&forbidaccounts=null&authtype=c2000004&customFileld02=22&areaname=" + URLEncoder.encode("海南", "UTF-8") + "&username={}&customFileld01" + "=1&password={}&randomId={}&lt={}&_eventId=submit&open_no=1";
            String data = TemplateUtils.format(templateData, param.getMobile(), param.getPassword(), param.getPicCode(), lt);
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10000_web_003").setFullUrl(templateUrl).setRequestBody(data, ContentType.APPLICATION_FORM_URLENCODED).invoke();
            /**
             * 结果枚举:
             * 登陆成功:<script type='text/javascript'>location.replace('http://www.189.cn:80/login/uam.do?UATicket=35nullST--174603-eUnkIrlk4upEVeWrS2St-ct10000uam'
             );</script>
             */
            String pageContent = response.getPageContent();
            if (pageContent.contains("location.replace")) {

                /**
                 * 访问登录成功的重定向页面
                 */
                templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'", 1);
                response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10000_web_004").setFullUrl(templateUrl).invoke();
                /**
                 * 获取查询权限
                 */
                templateUrl = "http://www.189.cn/login/sso/uam.do?method=linkTo&shopId=10022&toStUrl=http://hi.189.cn/service/bill/feequery.jsp?TABNAME=yecx&fastcode=02091574&cityCode=hi";
                response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10000_web_005").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();

                templateUrl = PatternUtils.group(pageContent, "replace\\('([^']+)'", 1);
                response = TaskHttpClient.create(param, RequestType.GET, "hai_nan_10000_web_006").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();

                logger.info("pageContent2222: "+response);

                String citycode = PatternUtils.group(pageContent, "var citycode=\"(\\d+)\"", 1);
                String prodid = PatternUtils.group(pageContent, "value='SHOUJI\\|" + param.getMobile() + "\\|(\\d+)\\|\\d+\\|[^\\|]+\\|[^']+", 1);
                String prodcode = PatternUtils.group(pageContent, "value='SHOUJI\\|" + param.getMobile() + "\\|\\d+\\|(\\d+)\\|[^\\|]+\\|[^']+", 1);
                String prodnum = PatternUtils.group(pageContent, "value='SHOUJI\\|" + param.getMobile() + "\\|\\d+\\|\\d+\\|[^\\|]+\\|([^']+)", 1);
                String userid = PatternUtils.group(pageContent, "var userid=\"(\\d+)\"", 1);

                TaskUtils.addTaskShare(param.getTaskId(), "citycode", citycode);
                TaskUtils.addTaskShare(param.getTaskId(), "prodid", prodid);
                TaskUtils.addTaskShare(param.getTaskId(), "prodcode", prodcode);
                TaskUtils.addTaskShare(param.getTaskId(), "prodnum", prodnum);
                TaskUtils.addTaskShare(param.getTaskId(), "userid", userid);

                templateUrl = "http://www.hi.189.cn/BUFFALO/buffalo/FeeQueryAjaxV4Service";
                templateData = "<buffalo-call><method>queryBalance</method><map><type>java.util" + ".HashMap</type><string>PRODNUM</string><string>{}</string><string>CITYCODE</string><string>{}</string><string>TYPE</string" + "><string>1</string><string>PRODID</string><string>{}</string><string>USERTYPE</string><string>SHOUJI</string></map" + "></buffalo-call>";
                data = TemplateUtils.format(templateData, prodnum, citycode, prodid);
                response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10000_web_007").setFullUrl(templateUrl).setRequestBody(data, ContentType.TEXT_XML).invoke();
                pageContent = response.getPageContent();
                if (pageContent.contains("余额")) {
                    return result.success();
                } else {
                    logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }
            String status = XPathUtil.getXpath("//td[@id='status2']/text()", pageContent).get(0);

            if (status.contains("账号或密码输入错误")) {
                logger.warn("登录失败-->账户名与密码不匹配,param={}", param);
                return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
            } else if (status.contains("验证码输入错误")) {
                logger.warn("登录失败-->图片随机码不正确,param={}", param);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            } else {
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
            String templateUrl = "http://www.hi.189.cn/BUFFALO/buffalo/CommonAjaxService";
            String templateData = "<buffalo-call><method>getSmsCode</method><map><type>java.util" + ".HashMap</type><string>PHONENUM</string><string>{}</string><string>PRODUCTID</string><string>50</string><string>RTYPE</string><string>QD</string></map></buffalo-call>";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10000_web_008").setFullUrl(templateUrl).setRequestBody(data, ContentType.TEXT_XML).invoke();
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
            String templateData = "<buffalo-call><method>queryDetailBill</method><map><type>java.util" + ".HashMap</type><string>PRODNUM</string><string>{}</string><string>CITYCODE</string><string>{}</string><string>QRYDATE</string" + "><string>{}</string><string>TYPE</string><string>8</string><string>PRODUCTID</string><string>{}</string><string>CODE</string" + "><string>{}</string><string>USERID</string><string>{}</string></map></buffalo-call>";
            String data = TemplateUtils.format(templateData, prodnum, citycode, format.format(new Date()), prodid, param.getSmsCode(), userid);
            response = TaskHttpClient.create(param, RequestType.POST, "hai_nan_10000_web_008").setFullUrl(templateUrl).setRequestBody(data, ContentType.TEXT_XML).invoke();
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
}
