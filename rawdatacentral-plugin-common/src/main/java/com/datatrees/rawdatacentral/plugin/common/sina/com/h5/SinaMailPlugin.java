package com.datatrees.rawdatacentral.plugin.common.sina.com.h5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.datatrees.crawler.plugin.login.ErrorMessage;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.spider.share.common.http.ProxyUtils;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.CollectionUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.Response;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyanjia on 2018/2/1.
 */
public class SinaMailPlugin implements CommonPluginService {

    private static final Logger  logger                  = LoggerFactory.getLogger(SinaMailPlugin.class);

    private static final String  DEFAULT_CHARSET_NAME    = "UTF-8";

    private static final String  MAIN_URL                = "http://mail.sina.cn/?vt=4";

    private static final String  CHECK_CODE_URL          = "http://login.sina.com.cn/cgi/pin.php?r=%s&s=0&p=%s";

    private static final String  PRE_LOGIN_URL
                                                         = "https://login.sina.com.cn/sso/prelogin.php?entry=cnmail&callback=sinaSSOController.preloginCallBack&su=&rsakt=mod&client=ssologin.js(v1.4.19)&_=";

    private static final String  LOGIN_URL               = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)&_=";

    private static final Pattern PRELOGIN_RESULT_PATTERN = PatternUtils.compile(
            "\"retcode\":(\\d+),\"servertime\":(\\d+),\"pcid\":\"([^\"]+)\",\"nonce\":\"([^\"]+)\",\"pubkey\":\"([^\"]+)\",\"rsakv\":\"(\\d+)\"");

    private static final String  FINAL_URL               = "http://mail.sina.cn/cgi-bin/sla.php?vt=4";

    private static final String  END_URL                 = "http://%s/wa.php?a=getprofile";

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        logger.info("新浪邮箱开始初始化taskId={}", param.getTaskId());
        TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "新浪邮箱h5");
        HttpResult<Object> result = new HttpResult<>();
        Map<String, Object> map = new HashMap<>();
        Response response = null;
        MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        MessageService messageService = BeanFactoryUtils.getBean(MessageService.class);
        try {
            //设置代理
            ProxyUtils.setProxyEnable(param.getTaskId(), true);
            String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId());
            RedisUtils.del(redisKey);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(MAIN_URL).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("sina web login request home url error!");
                messageService.sendTaskLog(param.getTaskId(), "登录初始化失败");
                monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5登录-->初始化-->失败");
                return result.failure(ErrorMessage.MAIL_DEFAULT_ERROR);
            }
            String preLoginUrl = PRE_LOGIN_URL + System.currentTimeMillis();
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(preLoginUrl).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("sina web pre login error!");
                messageService.sendTaskLog(param.getTaskId(), "登录初始化失败");
                monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5登录-->初始化-->失败");
                return result.failure(ErrorMessage.MAIL_DEFAULT_ERROR);
            }
            String serverTime = PatternUtils.group(pageContent, PRELOGIN_RESULT_PATTERN, 2);
            String pcId = PatternUtils.group(pageContent, PRELOGIN_RESULT_PATTERN, 3);
            String nonce = PatternUtils.group(pageContent, PRELOGIN_RESULT_PATTERN, 4);
            String pubKey = PatternUtils.group(pageContent, PRELOGIN_RESULT_PATTERN, 5);
            String rsakv = PatternUtils.group(pageContent, PRELOGIN_RESULT_PATTERN, 6);
            RedisUtils.set("sina_serverTime_" + param.getTaskId(), serverTime, 600);
            RedisUtils.set("sina_pcId_" + param.getTaskId(), pcId, 600);
            RedisUtils.set("sina_nonce_" + param.getTaskId(), nonce, 600);
            RedisUtils.set("sina_pubKey_" + param.getTaskId(), pubKey, 600);
            RedisUtils.set("sina_rsakv_" + param.getTaskId(), rsakv, 600);
            messageService.sendTaskLog(param.getTaskId(), "登录初始化成功");
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5登录-->初始化-->成功");
            return result.success("初始化成功");
        } catch (Exception e) {
            logger.error("登录-->初始化-->失败,param={},response={},e={}", JSON.toJSONString(param), response, e.getMessage());
            messageService.sendTaskLog(param.getTaskId(), "登录初始化失败");
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5登录-->初始化-->失败");
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        MessageService messageService = BeanFactoryUtils.getBean(MessageService.class);
        try {
            int rnd = RandomUtils.nextInt(100000000);
            String pcId = RedisUtils.get("sina_pcId_" + param.getTaskId());
            String requestUrl = String.format(CHECK_CODE_URL, rnd, pcId);
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(requestUrl).invoke();
            Map<String, Object> map = new HashMap<>();
            if (response.getStatusCode() == 200) {
                messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码成功");
                monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5-->刷新图片验证码-->成功");
                map.put("picCode", response.getPageContent());
                return result.success(map);
            }
            messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码失败");
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5-->刷新图片验证码-->失败");
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        } catch (Exception e) {
            logger.error("获取图片验证码失败，param={},response={},e={}", param, response, e.getMessage());
            messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码失败");
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5-->刷新图片验证码-->失败");
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
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
        MessageService messageService = BeanFactoryUtils.getBean(MessageService.class);
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "username", param.getUsername());
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "新浪邮箱h5");
            Map<String, Object> map = new HashMap<>();
            Long taskId = param.getTaskId();
            String serverTime = RedisUtils.get("sina_serverTime_" + taskId);
            String pcId = RedisUtils.get("sina_pcId_" + taskId);
            String nonce = RedisUtils.get("sina_nonce_" + taskId);
            String pubKey = RedisUtils.get("sina_pubKey_" + taskId);
            String rsakv = RedisUtils.get("sina_rsakv_" + taskId);
            String userName = param.getUsername();
            String passWord = param.getPassword();
            String su = EncryptSinaUtils.getSinaSU(userName);
            String sp = EncryptSinaUtils.getSinaSP(serverTime, nonce, pubKey, passWord);
            if (StringUtils.isEmpty(su) && StringUtils.isEmpty(sp)) {
                logger.error("sina login su or sp encrypt error! username : " + userName + " password " + passWord);
                map.put("directive", "login_fail");
                map.put("information", "系统异常,请稍后再试！");
                return result.success(map);
            }
            String templateData;
            String data;
            String requestUrl = LOGIN_URL + System.currentTimeMillis();
            if (param.getPicCode() == null) {
                templateData
                        = "entry=cnmail&gateway=1&from=&savestate=30&qrcode_flag=false&useticket=0&pagerefer=&cw=1&su={}&service=sso&servertime={}&nonce={}&pwencode=rsa2&rsakv={}&sp={}&sr=1920*1200&encoding=UTF-8&cdult=3&domain=sina.com.cn&prelt=46&returntype=TEXT";
                data = TemplateUtils.format(templateData, su, serverTime, nonce, rsakv, sp);
            } else {
                templateData
                        = "entry=cnmail&gateway=1&from=&savestate=30&qrcode_flag=false&useticket=0&pagerefer=&cw=1&pcid={}&door={}&su={}&service=sso&servertime={}&nonce={}&pwencode=rsa2&rsakv={}&sp={}&sr=1920*1200&encoding=UTF-8&cdult=3&domain=sina.com.cn&prelt=46&returntype=TEXT";
                data = TemplateUtils.format(templateData, pcId, param.getPicCode(), su, serverTime, nonce, rsakv, sp);
            }
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(requestUrl).setRequestBody(data)
                    .invoke();
            String pageContent = response.getPageContent();
            logger.info("新浪登录请求返回的response={}", response);
            if (pageContent.contains("101")) {
                map.put("directive", "login_fail");
                map.put("information", "登录名或密码错误");
                logger.error("登录-->失败,用户名或密码错误");
                messageService.sendTaskLog(param.getTaskId(), "登陆失败,登录名或密码错误");
                monitorService.sendTaskLog(taskId, param.getWebsiteName(), "新浪邮箱h5登陆-->校验-->失败");
                return result.success(map);
            } else if (pageContent.contains("2070")) {
                //需要重新获取图片验证码
                response = getPicCode(param, response, pcId);
                //返回验证码错误结果
                map.put("directive", "require_picture_again");
                map.put("errorMessage", "输入的验证码不正确");
                map.put("information", response.getPageContent());
                messageService.sendTaskLog(param.getTaskId(), "登陆失败,输入的验证码不正确");
                monitorService.sendTaskLog(taskId, param.getWebsiteName(), "新浪邮箱h5登陆-->校验-->失败");
                return result.success(map);
            } else if (pageContent.contains("4040")) {
                map.put("directive", "login_fail");
                map.put("information", "登录尝试次数过于频繁，请稍后再登录");
                messageService.sendTaskLog(param.getTaskId(), "登陆失败,登录尝试次数过于频繁");
                monitorService.sendTaskLog(taskId, param.getWebsiteName(), "新浪邮箱h5登陆-->校验-->失败");
                return result.success(map);
            } else if (pageContent.contains("4049")) {
                //获取验证码
                response = getPicCode(param, response, pcId);
                map.put("directive", "require_picture");
                map.put("information", response.getPageContent());
                logger.error("登录-->失败，重新访问的图片的response={}", response);
                messageService.sendTaskLog(param.getTaskId(), "登陆失败");
                monitorService.sendTaskLog(taskId, param.getWebsiteName(), "新浪邮箱h5登陆-->校验-->失败");
                return result.success(map);
            } else if (pageContent.contains("crossDomainUrlList")) {
                List<String> urlList = JsonPathUtil.readAsList(pageContent, "$.crossDomainUrlList");
                if (CollectionUtils.isEmpty(urlList)) {
                    logger.error("sina web login request login url error!pageContent: " + pageContent);
                    map.put("directive", "login_fail");
                    map.put("information", ErrorMessage.MAIL_DEFAULT_ERROR);
                    return result.success(map);
                }
                int i = 0;
                for (String crossUrl : urlList) {
                    crossUrl = crossUrl + "&callback=sinaSSOController.doCrossDomainCallBack&scriptId=ssoscript" + i +
                            "&client=ssologin.js(v1.4.19)&_=" + System.currentTimeMillis();
                    response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(crossUrl).invoke();
                    pageContent = response.getPageContent();
                    if (StringUtils.isBlank(pageContent)) {
                        logger.error("sina web login request cross url error! crossUrl: " + crossUrl);
                        map.put("directive", "login_fail");
                        map.put("information", ErrorMessage.MAIL_DEFAULT_ERROR);
                        return result.success(map);
                    }
                    i++;
                }
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).
                        setFullUrl(FINAL_URL).setReferer("http://mail.sina.cn/?vt=4").addHeader("Upgrade-Insecure-Requests", "1").invoke();
                pageContent = response.getPageContent();
                if (StringUtils.isBlank(pageContent)) {
                    logger.error("sina web login request final url error! finalUrl: " + FINAL_URL);
                    map.put("directive", "login_fail");
                    map.put("information", ErrorMessage.MAIL_DEFAULT_ERROR);
                    return result.success(map);
                }
                String redriectUrl = response.getRedirectUrl();
                if (StringUtils.isEmpty(redriectUrl) || !redriectUrl.contains("mobile/index.php")) {
                    logger.error("sina web login request final url error! pageContent: " + pageContent);
                    map.put("directive", "login_fail");
                    map.put("information", ErrorMessage.MAIL_DEFAULT_ERROR);
                    return result.success(map);
                }
                String urlDomain = PatternUtils.group(redriectUrl, "https?://([^/]+)/", 1);
                requestUrl = String.format(END_URL, urlDomain);
                if (StringUtils.isEmpty(urlDomain)) {
                    logger.error("sina web login get endurl domain error! redriectUrl: " + redriectUrl);
                    map.put("directive", "login_fail");
                    map.put("information", ErrorMessage.MAIL_DEFAULT_ERROR);
                    return result.success(map);
                }
                templateData = "uid={}&reload=true";
                data = TemplateUtils.format(templateData, param.getUsername());
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(requestUrl)
                        .setRequestBody(data).invoke();
                pageContent = response.getPageContent();
                if (!PatternUtils.match("\"username\":\"([^\"]+)\"", pageContent)) {
                    logger.error("sina web login request end url error! pageContent: " + pageContent);
                    map.put("directive", "login_fail");
                    map.put("information", ErrorMessage.MAIL_DEFAULT_ERROR);
                    return result.success(map);
                }
                map.put("directive", "login_success");
                map.put("information", "登陆成功");
                logger.info("登录-->成功");
                Map<String, Object> mqMap = new HashMap<>();
                mqMap.put(AttributeKey.TASK_ID, taskId);
                mqMap.put(AttributeKey.WEBSITE_NAME, param.getWebsiteName());
                mqMap.put(AttributeKey.END_URL, redriectUrl);
                String cookies = TaskUtils.getCookieString(taskId);
                mqMap.put(AttributeKey.COOKIE, cookies);
                BeanFactoryUtils.getBean(MessageService.class)
                        .sendMessage(TopicEnum.RAWDATA_INPUT.getCode(), TopicTag.LOGIN_INFO.getTag(), mqMap, DEFAULT_CHARSET_NAME);
                return result.success(map);
            } else if (pageContent.contains("2092")) {
                map.put("directive", "login_fail");
                map.put("information", "抱歉！登录失败，请稍候再试");
                logger.error("登录-->失败,抱歉！登录失败，请稍候再试");
                messageService.sendTaskLog(param.getTaskId(), "登陆失败");
                monitorService.sendTaskLog(taskId, param.getWebsiteName(), "新浪邮箱h5登陆-->校验-->失败");
                return result.success(map);
            }
            map.put("directive", "login_fail");
            map.put("information", "登录失败");
            messageService.sendTaskLog(param.getTaskId(), "登陆失败");
            monitorService.sendTaskLog(taskId, param.getWebsiteName(), "新浪邮箱h5登陆-->校验-->失败");
            return result.success(map);

        } catch (Exception e) {
            logger.error("登录-->失败，param={},response={},异常信息e={}", JSON.toJSONString(param), response, e.getMessage());
            messageService.sendTaskLog(param.getTaskId(), "登陆失败");
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5登陆-->校验-->失败");
            return result.failure(ErrorCode.LOGIN_FAIL);
        }
    }

    private Response getPicCode(CommonPluginParam param, Response response, String pcId) {
        MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        MessageService messageService = BeanFactoryUtils.getBean(MessageService.class);
        int rnd = RandomUtils.nextInt(100000000);
        String picUrl = String.format(CHECK_CODE_URL, rnd, pcId);
        response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(picUrl).invoke();
        if (response.getStatusCode() == 200) {
            BeanFactoryUtils.getBean(MessageService.class).sendTaskLog(param.getTaskId(), "刷新图片验证码");
            messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码成功");
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5-->刷新图片验证码-->成功");
        } else {
            messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码失败");
            monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "新浪邮箱h5-->刷新图片验证码-->失败");
        }
        return response;
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        return null;
    }
}
