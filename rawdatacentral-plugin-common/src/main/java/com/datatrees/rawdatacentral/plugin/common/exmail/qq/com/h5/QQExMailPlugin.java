package com.datatrees.rawdatacentral.plugin.common.exmail.qq.com.h5;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.plugin.login.ErrorMessage;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.*;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.common.exmail.qq.com.h5.util.EncryptExMailQQUtils;
import com.datatrees.rawdatacentral.plugin.common.exmail.qq.com.h5.util.ExMailErrorEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by zhangyanjia on 2018/2/26.
 */
public class QQExMailPlugin implements CommonPluginService {
    private static final Logger logger = LoggerFactory.getLogger(QQExMailPlugin.class);

    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    private static final Pattern PRELOGIN_RESULT_PATTERN = PatternUtils.compile("var PublicTs=\"(\\d+)\";");

    private static final Pattern CURRENTURL_RESULT_PATTERN = PatternUtils.compile("showMsg\\(\"(\\w+)\"\\);");

    private static final Pattern ISPICCODE_RESULT_PATTERN = PatternUtils.compile("var bAlwaysShowVerifyCode = \\((\\w+) == true\\);");

    private static final Pattern URLHEAD_RESULT_PATTERN = PatternUtils.compile("var urlHead=\"(\\S+)\";");

    private static final Pattern SUCCESS_TARGETURL_RESULT_PATTERN = PatternUtils.compile("targetUrl = urlHead \\+ \"(\\S+)\"");

    private static final Pattern SUCCESS_TARGETURL_PARAM_RESULT_PATTERN = PatternUtils.compile("targetUrl\\+=\"(\\S+)\"");

    //提示正在登陆腾讯企业邮箱，响应较少的
    private static final Pattern FAIL_TARGETURL_RESULT_PATTERN = PatternUtils.compile("targetUrl\\s*=\\s*urlHead\\s*\\+\\s*\"([^;]+)\";");
    private static final Pattern FAIL_TARGETURL_PARAM_RESULT_PATTERN = PatternUtils.compile("targetUrl\\s*\\+=\\s*\\s*\"([^;]+)\";");

    //提示腾讯企业邮箱，响应较多的FAIL__RESULT_PATTERN
    private static final Pattern FAIL__RESULT_PATTERN = PatternUtils.compile("getTop\\(\\)\\.location\\.href=\"([^;]+)\"<");


    private static final String URL = "https://exmail.qq.com";

    private static final String MAIN_URL = "https://exmail.qq.com/login";

    private static final String LOGIN_URL = "https://exmail.qq.com/cgi-bin/login";

    private static final String PIC_URL = "https://exmail.qq.com/cgi-bin/getverifyimage?aid=23000101&f=html&ck=1";

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        logger.info("腾讯企业邮箱开始初始化taskId={}", param.getTaskId());
        TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "腾讯企业邮箱h5");
        HttpResult<Object> result = new HttpResult<>();
        Map<String, Object> map = new HashMap<>();
        Response response = null;
        MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        try {
            ProxyUtils.setProxyEnable(param.getTaskId(), true);
            String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId());
            RedisUtils.del(redisKey);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "").setFullUrl(MAIN_URL).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("exmailqq web login request home url error!");
                monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "腾讯企业邮箱h5登录-->初始化-->失败");
                return result.failure(ErrorMessage.MAIL_DEFAULT_ERROR);
            }
            String publicTs = PatternUtils.group(pageContent, PRELOGIN_RESULT_PATTERN, 1);
            RedisUtils.set("exmail_publicTs_" + param.getTaskId(), publicTs, 600);
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "腾讯企业邮箱h5登录-->初始化-->成功");
            return result.success("初始化成功");
        } catch (Exception e) {
            logger.error("登录-->初始化-->失败,param={},response={},e={}", JSON.toJSONString(param), response, e.getMessage());
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "腾讯企业邮箱h5登录-->初始化-->失败");
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        return null;
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "username", param.getUsername());
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "腾讯企业邮箱h5");
            Map<String, Object> map = new HashMap<>();
            Long taskId = param.getTaskId();
            String userName = param.getUsername();
            String passWord = param.getPassword();
            if(userName.contains("@qq.com")||userName.contains("@vip.qq.com")||userName.contains("@foxmail.com")){
                map.put("directive", "login_fail");
                map.put("information", "请使用企业邮箱帐号登录。");
                logger.error("登录-->失败,errorMessage={}", "请使用企业邮箱帐号登录。");
                monitorService.sendTaskLog(taskId, param.getWebsiteName(), "腾讯企业邮箱h5登陆-->校验-->失败");
                return result.success(map);
            }
            String publicTs = RedisUtils.get("exmail_publicTs_" + taskId);
            int num = StringUtils.countMatches(userName, "@");
            if (num != 1) {
                map.put("directive", "login_fail");
                map.put("information", "您输入的用户名有误,请重新输入！");
                logger.error("登录-->失败,errorMessage={}", "您输入的用户名有误,请重新输入！");
                monitorService.sendTaskLog(taskId, param.getWebsiteName(), "腾讯企业邮箱h5登陆-->校验-->失败");
                return result.success(map);
            }
            String p = EncryptExMailQQUtils.getExMailQQSP(publicTs, passWord);
            p = URLEncoder.encode(p, "UTF-8");
            if (StringUtils.isEmpty(p)) {
                logger.error("exmailqq login su or sp encrypt error! username : " + userName + " password " + passWord);
                map.put("directive", "login_fail");
                map.put("information", "系统异常,请稍后再试！");
                return result.success(map);
            }
            String templateData;
            String data;
            String uin = userName.substring(0, userName.lastIndexOf("@"));
            String domain = userName.substring(userName.lastIndexOf("@") + 1);

            if (param.getPicCode() == null) {
                templateData = "sid=&firstlogin=false&domain={}&aliastype=other&errtemplate=dm_loginpage&first_step=&buy_amount=&year=&company_name=&is_get_dp_coupon=&source=&qy_code=&origin=&starttime={}&redirecturl=&f=biz&uin={}&p={}&delegate_url=&ts={}&from=&ppp=&chg=0&domain_bak=0&loginentry=3&s=&dmtype=bizmail&fun=&inputuin={}&verifycode=";
                data = TemplateUtils.format(templateData, domain, System.currentTimeMillis(), uin, p, publicTs, userName);
            } else {
                templateData = "sid=&firstlogin=false&domain={}&aliastype=other&errtemplate=dm_loginpage&first_step=&buy_amount=&year=&company_name=&is_get_dp_coupon=&source=&qy_code=&origin=&starttime={}&redirecturl=&f=biz&uin={}&p={}&delegate_url=&ts={}&from=&ppp=&chg=0&domain_bak=0&loginentry=3&s=&dmtype=bizmail&fun=&inputuin={}&verifycode={}";
                data = TemplateUtils.format(templateData, domain, System.currentTimeMillis(), uin, p, publicTs, userName, param.getPicCode());
            }
            response = TaskHttpClient.create(taskId, param.getWebsiteName(), RequestType.POST, "").setFullUrl(LOGIN_URL).setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("正在登录腾讯企业邮箱") && pageContent.contains("\"frame_html?sid=")) {
                String urlHead = PatternUtils.group(pageContent, URLHEAD_RESULT_PATTERN, 1);
                String targetUrl = PatternUtils.group(pageContent, SUCCESS_TARGETURL_RESULT_PATTERN, 1);
                String targetUrlParam = PatternUtils.group(pageContent, SUCCESS_TARGETURL_PARAM_RESULT_PATTERN, 1);
                StringBuilder url = new StringBuilder();
                String currentUrl = url.append(urlHead).append(targetUrl).append(targetUrlParam).toString();
                response = TaskHttpClient.create(taskId, param.getWebsiteName(), RequestType.GET, "").setFullUrl(currentUrl).invoke();
                map.put("directive", "login_success");
                map.put("information", "登陆成功");
                Map<String, Object> mqMap = new HashMap<>();
                mqMap.put(AttributeKey.TASK_ID, taskId);
                mqMap.put(AttributeKey.WEBSITE_NAME, param.getWebsiteName());
                mqMap.put(AttributeKey.END_URL, currentUrl);
                String cookies = TaskUtils.getCookieString(taskId);
                mqMap.put(AttributeKey.COOKIE, cookies);
                BeanFactoryUtils.getBean(MessageService.class).sendMessage(TopicEnum.RAWDATA_INPUT.getCode(), TopicTag.LOGIN_INFO.getTag(), mqMap, DEFAULT_CHARSET_NAME);
                return result.success(map);
            } else {
                String currentUrl;
                if (pageContent.contains("正在登录腾讯企业邮箱") && pageContent.contains("var target=\"ERROR\";") && pageContent.contains("urlHead + \"loginpage?")) {
                    String urlHead = PatternUtils.group(pageContent, URLHEAD_RESULT_PATTERN, 1);
                    String targetUrl = PatternUtils.group(pageContent, FAIL_TARGETURL_RESULT_PATTERN, 1);
                    targetUrl = targetUrl.replaceAll("\n", "");
                    targetUrl = targetUrl.replaceAll(" ", "");
                    targetUrl = targetUrl.replaceAll("\"\\+\"", "");
                    pageContent = pageContent.replaceAll("\n", "");
                    List<String> list = PatternUtils.findAll(pageContent, FAIL_TARGETURL_PARAM_RESULT_PATTERN, 1);
                    String targetUrlParam = list.get(1);
                    String targetUrlParam2 = list.get(2);
                    StringBuilder stringBuilder = new StringBuilder();
                    currentUrl = stringBuilder.append(urlHead).append(targetUrl).append(targetUrlParam).append(targetUrlParam2).toString();
                } else {
                    String fail = PatternUtils.group(pageContent, FAIL__RESULT_PATTERN, 1);
                    fail = fail.replaceAll("\n", "");
                    fail = fail.replaceAll(" ", "");
                    fail = fail.replaceAll("\"\\+\"", "");
                    StringBuilder stringBuilder = new StringBuilder();
                    currentUrl = stringBuilder.append(URL).append(fail).toString();
                }
                response = TaskHttpClient.create(taskId, param.getWebsiteName(), RequestType.GET, "").setFullUrl(currentUrl).invoke();
                pageContent = response.getPageContent();
                //更新redis中ts的值
                publicTs = PatternUtils.group(pageContent, PRELOGIN_RESULT_PATTERN, 1);
                RedisUtils.del("exmail_publicTs_" + taskId);
                RedisUtils.set("exmail_publicTs_" + taskId, publicTs, 600);
                List<String> list = PatternUtils.findAll(pageContent, CURRENTURL_RESULT_PATTERN, 1);
                String errorString = list.get(5);
                String isPicCode = PatternUtils.group(pageContent, ISPICCODE_RESULT_PATTERN, 1);
                if (pageContent.contains("请用绑定的微信进行扫码登录")){
                    map.put("directive", "login_fail");
                    map.put("information", "登录失败,请关闭安全验证后再试！");
                    logger.error("登录-->失败,errorMessage={}", "用户开启安全验证，需要扫码才能登");
                    monitorService.sendTaskLog(taskId, param.getWebsiteName(), "腾讯企业邮箱h5登陆-->校验-->失败");
                    return result.success(map);
                } else if(isPicCode.equals("true") && errorString.equals("errorVerifyCode")) {
                    response = TaskHttpClient.create(taskId, param.getWebsiteName(), RequestType.GET, "").setFullUrl(PIC_URL).invoke();
                    map.put("directive", "require_picture_again");
                    map.put("errorMessage", "输入的验证码不正确");
                    map.put("information", response.getPageContent());
                    monitorService.sendTaskLog(taskId, param.getWebsiteName(), "腾讯企业邮箱h5登陆-->校验-->失败");
                    return result.success(map);
                } else if (errorString.equals("errorNamePassowrd")) {
                    return failForResult(param, result, monitorService, map, taskId, errorString);
                } else if (isPicCode.equals("true")) {
                    response = TaskHttpClient.create(taskId, param.getWebsiteName(), RequestType.GET, "").setFullUrl(PIC_URL).invoke();
                    map.put("directive", "require_picture");
                    map.put("information", response.getPageContent());
                    logger.error("登录-->失败，重新访问的图片的response={}", response);
                    monitorService.sendTaskLog(taskId, param.getWebsiteName(), "腾讯企业邮箱h5登陆-->校验-->失败");
                    return result.success(map);
                } else {
                    return failForResult(param, result, monitorService, map, taskId, errorString);
                }
            }
        } catch (Exception e) {
            logger.error("登录-->失败，param={},response={},异常信息e={}", JSON.toJSONString(param), response, e.getMessage());
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "腾讯企业邮箱h5登陆-->校验-->失败");
            return result.failure(ErrorCode.LOGIN_FAIL);
        }
    }

    private HttpResult<Object> failForResult(CommonPluginParam param, HttpResult<Object> result, MonitorService monitorService, Map<String, Object> map, Long taskId, String errorString) {
        String errorMessage = ExMailErrorEnum.getMessageByCode(errorString);
        map.put("directive", "login_fail");
        map.put("information", errorMessage);
        logger.error("登录-->失败,errorMessage={}", errorMessage);
        monitorService.sendTaskLog(taskId, param.getWebsiteName(), "腾讯企业邮箱h5登陆-->校验-->失败");
        return result.success(map);
    }


    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return null;
    }
}
