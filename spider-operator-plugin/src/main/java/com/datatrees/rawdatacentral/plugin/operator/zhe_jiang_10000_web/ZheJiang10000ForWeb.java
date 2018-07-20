package com.datatrees.rawdatacentral.plugin.operator.zhe_jiang_10000_web;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.spider.operator.domain.model.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.rawdatacentral.service.OperatorPluginPostService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/19.
 */
public class ZheJiang10000ForWeb implements OperatorPluginPostService {

    private static final Logger                     logger     = LoggerFactory.getLogger(ZheJiang10000ForWeb.class);
    private              LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCode(param);
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
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
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
            String templateUrl = "http://zj.189.cn/zjpr/service/query/query_order.html?menuFlag=1";
            response = TaskHttpClient.create(param, RequestType.GET, "zhe_jiang_10000_web_005").setFullUrl(templateUrl).invoke();

            String referer = templateUrl;
            templateUrl = "http://zj.189.cn/bfapp/buffalo/cdrService";
            String data = "<buffalo-call><method>querycdrasset</method></buffalo-call>";
            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10000_web_005").setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).setReferer(referer).invoke();

            templateUrl = "http://zj.189.cn/bfapp/buffalo/VCodeOperation";
            String templateData = "<buffalo-call><method>SendVCodeByNbr</method><string>{}</string></buffalo-call>";
            data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10000_web_005").setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "成功")) {
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
        try {
            String productid = TaskUtils.getTaskContext(param.getTaskId(), "productid");
            String areaid = TaskUtils.getTaskContext(param.getTaskId(), "areaid");
            //String cdrlevel = TaskUtils.getTaskContext(param.getTaskId(), "cdrlevel");
            String servtype = TaskUtils.getTaskContext(param.getTaskId(), "servtype");
            String username = TaskUtils.getTaskContext(param.getTaskId(), "Name");
            String idCard = TaskUtils.getTaskContext(param.getTaskId(), "IdentityCard");
            if (StringUtils.isBlank(username) || StringUtils.isBlank(idCard) || StringUtils.contains(idCard, "*")) {
                logger.error("详单-->校验失败,姓名或身份证号不完整,param={},username={},idCard={}", param, username, idCard);
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
            username = URLEncoder.encode(username, "gb2312");
            TaskUtils.addTaskShare(param.getTaskId(), "realname", username);
            TaskUtils.addTaskShare(param.getTaskId(), "idCard", idCard);

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            String billMonth = sf.format(c.getTime());

            String referer = "http://zj.189.cn/zjpr/service/query/query_order.html?menuFlag=1";
            String templateUrl = "http://zj.189.cn/zjpr/cdr/getCdrDetail.htm";
            String templateData = "flag=1&cdrCondition.pagenum=1&cdrCondition.pagesize=100&cdrCondition.productnbr={}&cdrCondition.areaid={}&cdrCondition" +
                    ".cdrlevel=&cdrCondition.productid={}&cdrCondition.product_servtype={}&cdrCondition" +
                    ".recievenbr=%D2%C6%B6%AF%B5%E7%BB%B0&cdrCondition.cdrmonth={}&cdrCondition.cdrtype=11&cdrCondition.usernameyanzheng={}&cdrCondition.idyanzheng={}&cdrCondition" +
                    ".randpsw={}";
            String data = TemplateUtils
                    .format(templateData, param.getMobile(), areaid, productid, servtype, billMonth, username, idCard, param.getSmsCode());
            response = TaskHttpClient.create(param, RequestType.POST, "zhe_jiang_10000_web_006").setFullUrl(templateUrl).setRequestBody(data)
                    .setReferer(referer).setSocketTimeout(30000).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "清单详情") || StringUtils.contains(pageContent, "ErrorNo=61010")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
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
            String templateUrl = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10012&toStUrl=http://zj.189.cn/zjpr/balancep/getBalancep.htm";
            response = TaskHttpClient.create(param, RequestType.GET, "").setFullUrl(templateUrl).invoke();
            logger.info("登陆成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
