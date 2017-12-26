package com.datatrees.rawdatacentral.service.impl;

import com.alibaba.fastjson.JSON;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.EducationService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyanjia on 2017/11/30.
 */
@Service
public class EducationServiceImpl implements EducationService {

    private static final Logger logger = LoggerFactory.getLogger(EducationServiceImpl.class);

    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    WebsiteConfigService websiteConfigService;

    @Override
    public HttpResult<Map<String, Object>> loginInit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            //设置代理
            ProxyUtils.setProxyEnable(param.getTaskId(), true);
            //删cookies是防止用户进注册页又回登录页登录时报错
            String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId());
            RedisUtils.del(redisKey);
            String url = "https://account.chsi.com.cn/passport/login?service=https://my.chsi.com.cn/archive/j_spring_cas_security_check";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chsi_com_cn_01").setFullUrl(url).invoke();
            String pageContent = response.getPageContent();
            //获取lt参数，登录需要使用
            String select = "//input[@name='lt']/@value";
            List<String> list = XPathUtil.getXpath(select, pageContent);
            String lt = list.get(0);
            StringBuilder ltKey = new StringBuilder("lt_" + param.getTaskId());
            RedisUtils.set(ltKey.toString(), lt, 1800);
            String str = "//form[@id='fm1']/@action";
            List<String> listStr = XPathUtil.getXpath(str, pageContent);
            String jsessionId = listStr.get(0);
            StringBuilder jsKey = new StringBuilder("jsessionId_" + param.getTaskId());
            RedisUtils.set(jsKey.toString(), jsessionId, 1800);
//            redisTemplate.opsForValue().set(ltKey, lt, 300, TimeUnit.SECONDS);
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化-->失败,param={},response={}", JSON.toJSONString(param), response, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> loginSubmit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getLoginName() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
//            String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId());
//            RedisUtils.del(redisKey);
            //           redisTemplate.delete(redisKey);
            TaskUtils.addTaskShare(param.getTaskId(), "username", param.getLoginName());
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            Map<String, Object> map = new HashMap<>();
            StringBuilder ltKey = new StringBuilder("lt_" + param.getTaskId());
            String lt = RedisUtils.get(ltKey.toString());
            StringBuilder jsKey = new StringBuilder("jsessionId_" + param.getTaskId());
            String js = RedisUtils.get(jsKey.toString());
            String url = "https://account.chsi.com.cn" + js;
            String templateData;
            String data;
            if (param.getPicCode() != null) {
                templateData = "username={}&password={}&captcha={}&lt={}&_eventId=submit&submit=%E7%99%BB%C2%A0%C2%A0%E5%BD%95";
                data = TemplateUtils.format(templateData, param.getLoginName(), param.getPassword(), param.getPicCode(), lt);
                logger.info("学信网请求登录参数url={},loginName={},password={},lt={},picCode={}", url, param.getLoginName(), param.getPassword(), lt, param.getPicCode());
            } else {
                templateData = "username={}&password={}&lt={}&_eventId=submit&submit=%E7%99%BB%C2%A0%C2%A0%E5%BD%95";
                data = TemplateUtils.format(templateData, param.getLoginName(), param.getPassword(), lt);
                logger.info("学信网请求登录参数url={},loginName={},password={},lt={}", url, param.getLoginName(), param.getPassword(), lt);
            }

            String referer = "https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_02").setFullUrl(url).setRequestBody(data, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (pageContent != null && pageContent.contains("您输入的用户名或密码有误")) {
                map.put("directive", "login_fail");
                map.put("information", "您输入的用户名或密码有误");
                logger.error("登录-->失败，param={},response={}", JSON.toJSONString(param), response);
                return result.success(map);
            } else if (pageContent != null && pageContent.contains("为保障您的账号安全，请输入验证码后重新登录") || (pageContent != null && pageContent.contains("图片验证码输入有误"))) {
                url = "https://account.chsi.com.cn/passport/captcha.image";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chsi_com_cn_登录获取验证码").setFullUrl(url).invoke();
                map.put("directive", "require_picture");
                map.put("information", response.getPageContent());
                logger.error("登录-->失败，param={},response={}", JSON.toJSONString(param), response);
                return result.success(map);
            } else if (pageContent != null && pageContent.contains("手机校验码获取过于频繁,操作被禁止")) {
                map.put("directive", "login_fail");
                map.put("information", "手机校验码获取过于频繁,操作被禁止");
                logger.error("登录-->失败，param={},response={}", JSON.toJSONString(param), response);
                return result.success(map);
            } else if (pageContent != null && pageContent.contains("退出") || (pageContent != null && pageContent.contains("进入学信档案"))) {
                map.put("directive", "login_success");
                map.put("information", "登陆成功");
                logger.info("登录-->成功，param={},response={}", JSON.toJSONString(param), response);
                return result.success(map);
            }
            map.put("directive", "login_fail");
            map.put("information", "登录失败");
            return result.success(map);
        } catch (Exception e) {
            logger.error("登录-->失败，param={},response={}", JSON.toJSONString(param), response, e);
            return result.failure(ErrorCode.LOGIN_FAIL);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> registerInit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            //进入注册页需将原来的cookies删了 获取新cookies
            String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId());
            RedisUtils.del(redisKey);
            String url = "https://account.chsi.com.cn/account/preregister.action?from=archive";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chsi_com_cn_注册初始化").setFullUrl(url).invoke();
            return result.success();
        } catch (Exception e) {
            logger.error("注册-->初始化失败,param={}", param, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> registerRefeshPicCode(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            //注册获取图片验证码前校验手机号是否已被注册。。。
            String url = "https://account.chsi.com.cn/account/checkmobilephoneother.action";
            String templateDate = "mphone={}&dataInfo={}&optType=REGISTER";
            String date = TemplateUtils.format(templateDate, param.getMobile(), param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_05").setFullUrl(url).setRequestBody(date).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("false")) {
                logger.error("此手机号已被注册，mobile={}", param.getMobile());
                return result.failure("手机号已被注册");
            }
            long time = System.currentTimeMillis();
            url = "https://account.chsi.com.cn/account/captchimagecreateaction.action?time=" + time;
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chsi_com_cn_03").setFullUrl(url).invoke();
            Map<String, Object> map = new HashMap<>();
            map.put("picCode", response.getPageContent());
            return result.success(map);
        } catch (Exception e) {
            logger.error("注册-->获取图片验证码失败，param={},response={},", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> registerValidatePicCodeAndSendSmsCode(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getPicCode() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            String url = "https://account.chsi.com.cn/account/getmphonpincode.action";
//            Map<String, Object> params = new HashMap<>();
//            params.put("captch", param.getPicCode());
//            params.put("mobilePhone", param.getMobile());
//            params.put("optType", "REGISTER");
//            params.put("ignoremphone", "false");
            String templateDate = "captch={}&mobilePhone={}&optType=REGISTER&ignoremphone=false";
            String date = TemplateUtils.format(templateDate, param.getPicCode(), param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_04").setFullUrl(url).setRequestBody(date,ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)).invoke();
            String pageContent = response.getPageContent();
            String str = "学信网已向 " + param.getMobile() + " 发送校验码，请查收";
            if (pageContent.contains(str)) {
                logger.info("注册-->发送短信验证码成功,param={},response={}", JSON.toJSONString(param), response);
                Map<String, Object> map = new HashMap<>();
                map.put("msg", response.getPageContent());
                return result.success(map);
            }
            if (pageContent.contains("手机号码受限，短信发送次数已达到上限，请24小时后再试")) {
                logger.error("注册-->短信次数已达上限,param={},response={}", JSON.toJSONString(param), response);
                return result.failure("手机号码受限，短信发送次数已达到上限，请24小时后再试");
            }
            logger.error("注册-->验证码不正确，param={},response={}", JSON.toJSONString(param), response);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
        } catch (Exception e) {
            logger.error("注册-->校验验证码或者发送短信异常，param={},response={}", JSON.toJSONString(param), response, e);
            return result.failure("校验验证码异常");
        }
    }


    @Override
    public HttpResult<Map<String, Object>> registerSubmit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null || param.getSmsCode() == null || param.getPwd() == null
                || param.getSurePwd() == null || param.getRealName() == null || param.getIdCard() == null || param.getIdCardType() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            String url = "https://account.chsi.com.cn/account/checkmobilephoneother.action";
            String templateDate = "mphone={}&dataInfo={}&optType=REGISTER";
            String date = TemplateUtils.format(templateDate, param.getMobile(), param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_05").setFullUrl(url).setRequestBody(date).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("false")) {
                logger.error("此手机号已被注册，mobile={}", param.getMobile());
                return result.failure("手机号已被注册");
            }
            String name = URLEncoder.encode(param.getRealName(), "utf-8");
            url = "https://account.chsi.com.cn/account/registerprocess.action";
            templateDate = "from=&mphone={}&vcode={}&password={}&password1={}&xm={}&credentialtype={}&sfzh={}&from=&email=&pwdreq1=&pwdanswer1=&pwdreq2=&pwdanswer2=&pwdreq3=&pwdanswer3=&continueurl=&serviceId=&serviceNote=1&serviceNote_res=0";
            date = TemplateUtils.format(templateDate, param.getMobile(), param.getSmsCode(), param.getPwd(), param.getSurePwd(), name, param.getIdCardType(), param.getIdCard());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_06").setFullUrl(url).setRequestBody(date).invoke();
            pageContent = response.getPageContent();
            logger.info("注册返回结果 responPage={}",response.getPageContent());
            Map<String, Object> map = new HashMap<>();
            if (pageContent.contains("校验码有误")) {
                logger.error("注册失败，param={},response={}", param, response);
                return result.failure("校验码有误,注册失败");
            }
            if (pageContent.contains("账号注册成功")) {
                logger.info("注册成功，param={},response={}", param, response);
                return result.success();
            }
            return result.failure("注册失败");
        } catch (Exception e) {
            logger.error("注册异常 param={},response={}", param, response);
            return result.failure("注册失败，请稍后重试");
        }
    }
}
