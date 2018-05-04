package com.datatrees.rawdatacentral.plugin.common.xuexinwang.com.h5;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RpcOssService;
import com.datatrees.rawdatacentral.api.internal.CommonPluginService;
import com.datatrees.rawdatacentral.api.internal.XueXinPluginService;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.common.xuexinwang.com.h5.utils.HttpUtils;
import com.datatrees.rawdatacentral.plugin.common.xuexinwang.com.h5.utils.Sign;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangpan on 4/27/18 5:19 PM
 */
public class XueXinWebPlugin implements CommonPluginService, XueXinPluginService {

    private static final Logger logger = LoggerFactory.getLogger(XueXinWebPlugin.class);
    @Autowired
    WebsiteConfigService websiteConfigService;
    @Resource
    private MonitorService monitorService;
    @Resource
    private MessageService messageService;

    private final static String TX_GENERAL_URL = "http://recognition.image.myqcloud.com/ocr/general";
    private final static String appid = "1255658810";
    private final static String bucket = "dashutest";
    private final static String secretid = "AKIDHQRPGv4iroY7UgqxNejeNuFOLBpHscje";
    private final static String secretkey = "swyoTwCIH4f4IKBsPkwwTxGRTL1Vnupd";
    private final static String HOST = "recognition.image.myqcloud.com";

    private static void setRedisBySelect(String key, String select, String pageContent) {
        List<String> list = XPathUtil.getXpath(select, pageContent);
        String redisValue = list.get(0);
        RedisUtils.set(key, redisValue, 1800);
    }

    @Override
    public HttpResult<Object> init(CommonPluginParam commonPluginParam) {
        EducationParam param = (EducationParam) commonPluginParam;
        if (param.getTaskId() == null || param.getWebsiteName() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Object> result = new HttpResult<>();
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
            StringBuilder ltKey = new StringBuilder("lt_" + param.getTaskId());
            setRedisBySelect(ltKey.toString(), select, pageContent);
            String select2 = "//input[@name='execution']/@value";
            StringBuilder exeKey = new StringBuilder("execution_" + param.getTaskId());
            setRedisBySelect(exeKey.toString(), select2, pageContent);
            String str = "//form[@id='fm1']/@action";
            StringBuilder jsKey = new StringBuilder("jsessionId_" + param.getTaskId());
            setRedisBySelect(jsKey.toString(), str, pageContent);
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化-->失败,param={},response={},e={}", JSON.toJSONString(param), response, e.getMessage());
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam commonPluginParam) {
        EducationParam param = (EducationParam) commonPluginParam;
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getLoginName() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "username", param.getLoginName());
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            Map<String, Object> map = new HashMap<>();
            String ltKey = new StringBuilder("lt_" + param.getTaskId()).toString();
            String exeKey = new StringBuilder("execution_" + param.getTaskId()).toString();
            String jsKey = new StringBuilder("jsessionId_" + param.getTaskId()).toString();
            String lt = RedisUtils.get(ltKey);
            String execution = RedisUtils.get(exeKey);
            String js = RedisUtils.get(jsKey);
            String url = "https://account.chsi.com.cn";
            String templateData;
            String data;
            if (param.getPicCode() != null) {
                url = url + "/passport/login?service=https://my.chsi.com.cn/archive/j_spring_cas_security_check";
                templateData = "username={}&password={}&captcha={}&lt={}&_eventId=submit&submit=%E7%99%BB%C2%A0%C2%A0%E5%BD%95";
                data = TemplateUtils.format(templateData, param.getLoginName(), param.getPassword(), param.getPicCode(), lt);
                logger.info("学信网请求登录参数url={},loginName={},password={},lt={},picCode={}", url, param.getLoginName(), param.getPassword(), lt,
                        param.getPicCode());
            } else {
                url = url + js;
                templateData = "username={}&password={}&lt={}&execution={}&_eventId=submit&submit=%E7%99%BB%C2%A0%C2%A0%E5%BD%95";
                data = TemplateUtils.format(templateData, param.getLoginName(), param.getPassword(), lt, execution);
                logger.info("学信网请求登录参数url={},loginName={},password={},lt={},execution={}", url, param.getLoginName(), param.getPassword(), lt,
                        execution);
            }

            String referer
                    = "https://account.chsi.com.cn/passport/login?service=https%3A%2F%2Fmy.chsi.com.cn%2Farchive%2Fj_spring_cas_security_check";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_02").setFullUrl(url)
                    .setRequestBody(data, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            logger.info("登录的请求返回的response={}", response);
            if (pageContent != null && pageContent.contains("您输入的用户名或密码有误")) {
                map.put("directive", "login_fail");
                map.put("information", "您输入的用户名或密码有误");
                logger.error("登录-->失败");
                return result.success(map);
            } else if (pageContent != null && pageContent.contains("图片验证码输入有误")) {
                url = "https://account.chsi.com.cn/passport/captcha.image";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chsi_com_cn_登录获取验证码").setFullUrl(url)
                        .invoke();
                if (response.getStatusCode() == 200) {
                    messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码");
                    monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网登录-->刷新图片验证码-->成功");
                } else {
                    monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网登录-->刷新图片验证码-->失败");
                }
                map.put("directive", "require_picture_again");
                map.put("errorMessage", "验证码错误,请重新输入");
                map.put("information", response.getPageContent());
                logger.error("登录-->失败,重新访问的图片的response={}", response);
                return result.success(map);
            } else if (pageContent != null && pageContent.contains("为保障您的账号安全，请输入验证码后重新登录")) {
                url = "https://account.chsi.com.cn/passport/captcha.image";
                response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chsi_com_cn_登录获取验证码").setFullUrl(url)
                        .invoke();
                if (response.getStatusCode() == 200) {
                    messageService.sendTaskLog(param.getTaskId(), "刷新图片验证码");
                    monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网登录-->刷新图片验证码-->成功");
                } else {
                    monitorService.sendTaskLog(param.getTaskId(), param.getWebsiteName(), "学信网登录-->刷新图片验证码-->失败");
                }
                map.put("directive", "require_picture");
                map.put("information", response.getPageContent());
                logger.error("登录-->失败，重新访问的图片的response={}", response);
                return result.success(map);
            } else if (pageContent != null && pageContent.contains("手机校验码获取过于频繁,操作被禁止")) {
                map.put("directive", "login_fail");
                map.put("information", "手机校验码获取过于频繁,操作被禁止");
                logger.error("登录-->失败");
                return result.success(map);
            } else if (pageContent != null && pageContent.contains("退出") || (pageContent != null && pageContent.contains("进入学信档案"))) {
                map.put("directive", "login_success");
                map.put("information", "登陆成功");
                logger.info("登录-->成功");
                return result.success(map);
            }
            map.put("directive", "login_fail");
            map.put("information", "登录失败");
            return result.success(map);
        } catch (Exception e) {
            logger.error("登录-->失败，param={},response={},异常信息e={}", JSON.toJSONString(param), response, e.getMessage());
            return result.failure(ErrorCode.LOGIN_FAIL);
        }
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        switch (param.getFormType()) {
            case "OCR":
                return processForOCR(param);
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Object> registerInit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            //进入注册页需将原来的cookies删了 获取新cookies
            String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(param.getTaskId());
            RedisUtils.del(redisKey);
            String url = "https://account.chsi.com.cn/account/preregister.action?from=archive";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "chsi_com_cn_注册初始化").setFullUrl(url)
                    .invoke();
            return result.success();
        } catch (Exception e) {
            logger.error("注册-->初始化失败,param={},e={}", param, e.getMessage());
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Object> registerRefreshPicCode(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            //注册获取图片验证码前校验手机号是否已被注册。。。
            String url = "https://account.chsi.com.cn/account/checkmobilephoneother.action";
            String templateDate = "mphone={}&dataInfo={}&optType=REGISTER";
            String date = TemplateUtils.format(templateDate, param.getMobile(), param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_05").setFullUrl(url)
                    .setRequestBody(date).invoke();
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
            logger.error("注册-->获取图片验证码失败，param={},response={},e={}", param, response, e.getMessage());
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    @Override
    public HttpResult<Object> registerValidatePicCodeAndSendSmsCode(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getPicCode() == null || param.getMobile() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Object> result = new HttpResult<>();
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
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_04").setFullUrl(url)
                    .setRequestBody(date, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8)).invoke();
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
            if (pageContent.contains("手机校验码获取过于频繁,操作被禁")) {
                logger.error("注册-->手机校验码获取过于频繁,操作被禁 param={},response={}", JSON.toJSONString(param), response);
                return result.failure("手机校验码获取过于频繁,操作被禁,请60秒后重试");
            }
            logger.error("注册-->验证码不正确，param={},response={}", JSON.toJSONString(param), response);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
        } catch (Exception e) {
            logger.error("注册-->校验验证码或者发送短信异常，param={},response={},e={}", JSON.toJSONString(param), response, e.getMessage());
            return result.failure("校验验证码异常");
        }
    }

    @Override
    public HttpResult<Object> registerSubmit(EducationParam param) {
        if (param.getTaskId() == null || param.getWebsiteName() == null || param.getMobile() == null || param.getSmsCode() == null ||
                param.getPwd() == null || param.getSurePwd() == null || param.getRealName() == null || param.getIdCard() == null ||
                param.getIdCardType() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        HttpResult<Object> result = new HttpResult<>();
        Response response = null;
        try {
            TaskUtils.addTaskShare(param.getTaskId(), "websiteTitle", "学信网");
            String url = "https://account.chsi.com.cn/account/checkmobilephoneother.action";
            String templateDate = "mphone={}&dataInfo={}&optType=REGISTER";
            String date = TemplateUtils.format(templateDate, param.getMobile(), param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_05").setFullUrl(url)
                    .setRequestBody(date).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("false")) {
                logger.error("此手机号已被注册，mobile={}", param.getMobile());
                return result.failure("手机号已被注册");
            }
            String name = URLEncoder.encode(param.getRealName(), "utf-8");
            url = "https://account.chsi.com.cn/account/registerprocess.action";
            templateDate
                    = "from=&mphone={}&vcode={}&password={}&password1={}&xm={}&credentialtype={}&sfzh={}&from=&email=&pwdreq1=&pwdanswer1=&pwdreq2=&pwdanswer2=&pwdreq3=&pwdanswer3=&continueurl=&serviceId=&serviceNote=1&serviceNote_res=0";
            date = TemplateUtils
                    .format(templateDate, param.getMobile(), param.getSmsCode(), param.getPwd(), param.getSurePwd(), name,
                            param.getExtral().get("idCardType"), param.getIdCard());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST, "chsi_com_cn_06").setFullUrl(url)
                    .setRequestBody(date).invoke();
            pageContent = response.getPageContent();
            logger.info("注册返回结果 responsePage={}", response.getPageContent());
            Map<String, Object> map = new HashMap<>();
            if (pageContent.contains("校验码有误")) {
                logger.error("注册失败--验证码有误，param={},response={}", param, response);
                return result.failure("校验码有误,注册失败");
            }
            if (pageContent.contains("证件号码已注册")) {
                logger.error("注册失败--证件号码已注册，param={},response={}", param, response);
                return result.failure("证件号码已注册");
            }
            if (pageContent.contains("账号注册成功")) {
                logger.info("注册成功，param={},response={}", param, response);
                return result.success();
            }
            return result.failure("注册失败");
        } catch (Exception e) {
            logger.error("注册异常 param={},response={},e={}", param, response, e.getMessage());
            return result.failure("注册失败，请稍后重试");
        }
    }

    private HttpResult<Object> processForOCR(CommonPluginParam commonPluginParam) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            EducationParam param = (EducationParam) commonPluginParam;
            String websiteName = param.getWebsiteName();
            Long taskId = param.getTaskId();
            Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                    .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
            String url = paramMap.get("page_content");

            String string = handlePic(url, taskId, websiteName);
            Map<String, Object> pluginResult = new HashMap<>();
            return result.success(pluginResult);
        }catch (Exception e){
            logger.error("OCR处理失败,param={}", commonPluginParam, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }
    private static String handlePic(String url, Long taskId, String websiteName) {

        try {
            Map<String, String> map = new HashMap<>();
            byte[] pageContent = TaskHttpClient.create(taskId, websiteName, RequestType.GET, "chsi_com_cn_pic").setFullUrl(url).invoke().getResponse();
            int i = (int) (Math.random() * 100000);
            String picName=i+".jpeg";
            String path = "education/" + websiteName + "/" + taskId + "/" + picName;
            RpcOssService rpcOssService = BeanFactoryUtils.getBean(RpcOssService.class);
            rpcOssService.upload(path, pageContent);
            logger.info("学信网图片上传oss成功！path={}", path);
            String authorization = RedisUtils.get("authorization");
            if (authorization == null) {
                Long appId = Long.parseLong(appid);
                //authorization的有效期为81天
                authorization = Sign.appSign(appId, secretid, secretkey, bucket, 6998400L);
                //存redis存80天
                RedisUtils.set("authorization", authorization, 6912000);
            }
            map.put("Authorization", authorization);
            map.put("Host", HOST);
            String fileName = taskId + ".jpg";
            logger.info("请求腾讯解析图片接口参数：tx_url={},appid={},bucket={},map={}", TX_GENERAL_URL, appid, bucket, JSON.toJSONString(map));
            int num = 1;
            //腾讯云可能返回为空,所以试3次,3次都为空那你可以去买彩票了。。。
            while (num < 4) {
                String imageResult = HttpUtils.doPostForImage(TX_GENERAL_URL, map, appid, bucket, pageContent, fileName);
                if (imageResult != null) {
                    logger.info("腾讯云返回结果result={}", imageResult);
                    return imageResult;
                } else {
                    logger.info("第{}次腾讯云返回结果为空", num);
                    num++;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
