package com.datatrees.rawdatacentral.plugin.operator.ji_lin_10000_web;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 吉林电信web端
 * 登录 wap端手机号，服务密码，图片验证码
 * 详单查询 短信验证码和两次图片验证码
 * User: yand
 * Date: 2017/10/10
 */
public class JiLin10000ForWeb implements OperatorPluginService {

    private static Logger                     logger     = LoggerFactory.getLogger(JiLin10000ForWeb.class);

    private        LoginUtilsForChina10000Web loginUtils = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCodeForLogin(param);
            case "QUERY_BASEINFO":
                return refeshPicCodeForBaseInfo(param);
            case FormType.VALIDATE_USER_INFO:
                return refeshPicCodeForUserInfo(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshPicCodeForBillDetail(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case "QUERY_BASEINFO":
                return validatePicCodeForBaseinfo(param);
            case FormType.VALIDATE_USER_INFO:
                return validatePicCodeForUserInfo(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
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

    private HttpResult<String> refeshPicCodeForBaseInfo(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            //删除cookie
            //RedisUtils.del(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId()));

            String templateUrl = "http://wapjl.189.cn/menu/treeMenu.action?cms_page_type=4&servId=-1";
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10000_web_001").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("get encrypt param:初始化失败!");
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            String pscToken = "nqnfydcxen";
            String queryType = "4";
            String areaCode = "";
            String pwdType = "2";
            String firstInput = "y";
            String url = StringUtils.EMPTY;
            List<String> pscTokenList = XPathUtil.getXpath("//input[@id='pscToken']/@value", pageContent);
            if (!CollectionUtils.isEmpty(pscTokenList)) {
                pscToken = pscTokenList.get(0);
            }
            List<String> queryTypeList = XPathUtil.getXpath("//input[@name='queryType']/@value", pageContent);
            if (!CollectionUtils.isEmpty(queryTypeList)) {
                queryType = queryTypeList.get(0);
            }
            List<String> areaCodeList = XPathUtil.getXpath("//input[@name='areaCode']/@value", pageContent);
            if (!CollectionUtils.isEmpty(areaCodeList)) {
                areaCode = areaCodeList.get(0);
            }
            List<String> pwdTypeList = XPathUtil.getXpath("//input[@name='pwdType']/@value", pageContent);
            if (!CollectionUtils.isEmpty(pwdTypeList)) {
                pwdType = pwdTypeList.get(0);
            }
            List<String> firstInputList = XPathUtil.getXpath("//input[@name='firstInput']/@value", pageContent);
            if (!CollectionUtils.isEmpty(firstInputList)) {
                firstInput = firstInputList.get(0);
            }
            List<String> urlList = XPathUtil.getXpath("//input[@name='url']/@value", pageContent);
            if (!CollectionUtils.isEmpty(urlList)) {
                url = urlList.get(0);
            }

            TaskUtils.addTaskShare(param.getTaskId(), "pscToken", pscToken);
            TaskUtils.addTaskShare(param.getTaskId(), "queryType", queryType);
            TaskUtils.addTaskShare(param.getTaskId(), "areaCode", areaCode);
            TaskUtils.addTaskShare(param.getTaskId(), "pwdType", pwdType);
            TaskUtils.addTaskShare(param.getTaskId(), "firstInput", firstInput);
            TaskUtils.addTaskShare(param.getTaskId(), "url", url);

            templateUrl = "http://wapjl.189.cn/authImg?" + Math.random();
            String referer = "http://wapjl.189.cn/";
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10000_web_002").setFullUrl(templateUrl).setReferer(referer).invoke();
            logger.info("登陆wap版-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登陆wap版-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            //web端登录
            result = loginUtils.submitForLogin(param);
            if (!result.getStatus()) {
                return result;
            }
            logger.info("登陆web版成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆web版-->校验-->失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBaseinfo(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String pscToken = TaskUtils.getTaskShare(param.getTaskId(), "pscToken");
            String queryType = TaskUtils.getTaskShare(param.getTaskId(), "queryType");
            String areaCode = TaskUtils.getTaskShare(param.getTaskId(), "areaCode");
            String pwdType = TaskUtils.getTaskShare(param.getTaskId(), "pwdType");
            String firstInput = TaskUtils.getTaskShare(param.getTaskId(), "firstInput");
            String url = TaskUtils.getTaskShare(param.getTaskId(), "url");
            //wap端登录
            Invocable invocable = ScriptEngineUtil.createInvocable(param.getWebsiteName(), "des.js", "GBK");
            String encryptPassword = invocable.invokeFunction("encrypt", new Object[]{param.getPassword(), pscToken}).toString();
            String templateUrl = "http://wapjl.189.cn/echn/login/login.action";
            String templateData = "queryValue=" + param.getMobile() + "&passWord=" + encryptPassword + "&randCode=" + param.getPicCode() +
                    "&queryType=" + queryType + "&areaCode=" + areaCode + "&pwdType=" + pwdType + "&firstInput=" + firstInput +
                    "&changeLoginFlag=&returnUrlFlag=&url=" + URLEncoder.encode(url, "UTF-8");
            String referer = "http://wapjl.189.cn/";
            response = TaskHttpClient.create(param, RequestType.POST, "ji_lin_10000_web_003").setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            List<String> list = XPathUtil.getXpath("div:has(form):not(:has(div)) span:eq(0)/text()", pageContent);
            String msg = StringUtils.EMPTY;
            if (!CollectionUtils.isEmpty(list)) {
                msg = list.get(0);
            }
            if (StringUtils.isBlank(msg)) {
                list = XPathUtil.getXpath("span:has(img) + span/text()", pageContent);
                if (!CollectionUtils.isEmpty(list)) {
                    msg = list.get(0);
                }
            }
            templateUrl = "http://wapjl.189.cn/custquery/customerInfoQuery.action?servId=154";
            referer = "http://wapjl.189.cn/";
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10000_web_004").setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, String.valueOf(param.getMobile()))) {
                String customerName = PatternUtils.group(pageContent, "客户姓名：<\\/strong>\\s*([^：]+)\\s*<br", 1);
                customerName = PatternUtils.group(customerName, "(\\S*)", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "customerName", customerName);
                String idCardNo = PatternUtils.group(pageContent, "证件号码：<\\/strong>\\s*([^：]+)\\s*<br", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "idCardNo", idCardNo);
                String joinDate = PatternUtils.group(pageContent, "开通时间：<\\/strong>\\s*([^：]+)\\s*<\\/div>", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "joinDate", joinDate);
            } else {
                logger.error("登陆wap版-->校验失败,错误信息: " + msg);
                logger.error("登陆wap版-->校验失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
            logger.info("登陆wap版-->校验成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("登陆wap版失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForUserInfo(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.189.cn/dqmh/my189/checkMy189Session.do";
            String referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00710599";
            String data = "fastcode=00710602";
            response = TaskHttpClient.create(param, RequestType.POST, "").setFullUrl(templateUrl).setRequestBody(data).setReferer(referer).invoke();

            templateUrl
                    = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10030&toStUrl=http://jl.189.cn/service/bill/toDetailBillFra.action?fastcode=00710602&cityCode=jl";
            referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=00710602";
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10000_web_0010").setFullUrl(templateUrl).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent) || !StringUtils.contains(pageContent, "证件号码")) {
                logger.error("requestUrl is wrong!");
                return result.failure(ErrorCode.RESPONSE_EMPTY_ERROR_CODE);
            }
            templateUrl = "http://jl.189.cn/authImg";
            referer = response.getRedirectUrl();
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10000_web_0011").setFullUrl(templateUrl).setReferer(referer).invoke();
            logger.info("详单-->第一次图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->第一次图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> validatePicCodeForUserInfo(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "http://jl.189.cn/service/bill/toDetailBillFra.action?cityCode=jl&fastcode=00710602";
            String templateUrl = "http://jl.189.cn/realname/checkIdCardFra.action";
            Map<String, Object> params = new HashMap<>();
            params.put("ruleDetalId", 109);
            params.put("certCode", param.getIdCard());
            params.put("custName", param.getRealName());
            params.put("randCode", param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "ji_lin_10000_web_0012").setUrl(templateUrl).setParams(params)
                    .setRequestCharset(Charset.forName("UTF-8")).setReferer(referer).addHeader("X-Requested-With", "XMLHttpRequest").invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent) && StringUtils.contains(pageContent, "result\":\"0")) {
                logger.info("详单-->第一次图片验证码-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->第一次图片验证码-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->第一次图片验证码-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_ERROR);
        }
    }

    private HttpResult<String> refeshPicCodeForBillDetail(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://jl.189.cn/service/bill/toDetailBillFra.action?cityCode=jl&fastcode=00710602";
            String referer = "http://jl.189.cn/service/bill/toDetailBillFra.action?fastcode=00710602&cityCode=jl";
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10000_web_0013").setFullUrl(templateUrl).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent) || !StringUtils.contains(pageContent, "短信验证码")) {
                logger.error("requestUrl is wrong!");
                return result.failure(ErrorCode.RESPONSE_EMPTY_ERROR_CODE);
            }

            templateUrl = "http://jl.189.cn/authImg?1";
            referer = "http://jl.189.cn/service/bill/toDetailBillFra.action?cityCode=jl&fastcode=00710602";
            response = TaskHttpClient.create(param, RequestType.GET, "ji_lin_10000_web_0014").setFullUrl(templateUrl).setReferer(referer).invoke();
            logger.info("详单-->第二次图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("详单-->第二次图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }

    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            logger.info("详单-->短信验证码-->刷新成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://jl.189.cn/service/transaction/qryInacWorkOrder.action";
            String templateData = "fromPage=XDCX";
            String referer = "http://jl.189.cn/service/bill/toDetailBillFra.action?cityCode=jl&fastcode=00710602";
            response = TaskHttpClient.create(param, RequestType.POST, "ji_lin_10000_web_0015").setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();

            templateUrl = "http://jl.189.cn/service/bill/doDetailBillFra.action";
            templateData = "sRandomCode=" + param.getSmsCode() + "&randCode=" + param.getPicCode();
            referer = "http://jl.189.cn/service/bill/toDetailBillFra.action?cityCode=jl&fastcode=00710602";
            response = TaskHttpClient.create(param, RequestType.POST, "ji_lin_10000_web_0015").setFullUrl(templateUrl).setRequestBody(templateData)
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "billDetailValidate\":\"true")) {
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

}


