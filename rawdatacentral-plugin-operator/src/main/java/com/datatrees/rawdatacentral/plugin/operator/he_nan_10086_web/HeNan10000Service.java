package com.datatrees.rawdatacentral.plugin.operator.he_nan_10086_web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.crawler.plugin.util.PluginHttpUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.JsonpUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;

/**
 * 河南移动
 * 登陆地址:https://login.10086.cn/html/login/login.html
 * 登陆方式:服务密码登陆
 * 图片验证码:支持
 * 验证图片验证码:支持
 * 短信验证码:支持
 *
 * Created by zhouxinghai on 2017/7/17.
 */
public class HeNan10000Service implements OperatorPluginService {

    private static final Logger logger = LoggerFactory.getLogger(HeNan10000Service.class);

    @Override
    public HttpResult<Map<String, Object>> init(Long taskId, String websiteName, OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //登陆页没有获取任何cookie,https://login.10086.cn/login.html?channelID=12003&backUrl=http://shop.10086.cn/i/,不用登陆
            //预登陆可以先返回图片验证码
            return refeshPicCode(taskId, websiteName, FormType.LOGIN, param);
        } catch (Exception e) {
            logger.error("登录-->初始化失败,taskId={},websiteName={}", taskId, websiteName, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshPicCode(Long taskId, String websiteName, String type,
                                                         OperatorParam param) {
        switch (type) {
            case FormType.LOGIN:
                return refeshPicCodeForLogin(taskId, websiteName, param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshPicCodeForBillDetail(taskId, websiteName, param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(Long taskId, String websiteName, String type,
                                                         OperatorParam param) {
        switch (type) {
            case FormType.LOGIN:
                return refeshSmsCodeForLogin(taskId, websiteName, param);
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(taskId, websiteName, param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> submit(Long taskId, String websiteName, String type, OperatorParam param) {
        switch (type) {
            case FormType.LOGIN:
                return submitForLogin(taskId, websiteName, param);
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(taskId, websiteName, param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(Long taskId, String websiteName, String type,
                                                           OperatorParam param) {
        switch (type) {
            case FormType.LOGIN:
                return validatePicCodeForLogin(taskId, websiteName, param);
            case FormType.VALIDATE_BILL_DETAIL:
                return validatePicCodeForBillDetail(taskId, websiteName, param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<Map<String, Object>> refeshPicCodeForLogin(Long taskId, String websiteName,
                                                                  OperatorParam param) {
        /**
         * 这里不一定有图片验证码,随机出现
         */
        String templateUrl = "https://login.10086.cn/captchazh.htm?type=05&timestamp={}";
        String url = TemplateUtils.format(templateUrl, System.currentTimeMillis());
        return PluginHttpUtils.refeshPicCodePicCode(taskId, websiteName, url, RETURN_FIELD_PIC_CODE, FormType.LOGIN);
    }

    private HttpResult<Map<String, Object>> validatePicCodeForLogin(Long taskId, String websiteName,
                                                                    OperatorParam param) {
        String templateUrl = "https://login.10086.cn/verifyCaptcha?inputCode={}";
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        String url = TemplateUtils.format(templateUrl, param.getPicCode());
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String pateContent = null;
        try {
            //结果枚举:正确{"resultCode":"0"},错误{"resultCode":"1"}
            pateContent = PluginHttpUtils.getString(url, taskId);
            JSONObject json = JSON.parseObject(pateContent);
            if (!StringUtils.equals("0", json.getString("resultCode"))) {
                logger.error("登录-->图片验证码-->校验失败,taskId={},websiteName={},formType={},pateContent={}", taskId,
                    websiteName, FormType.LOGIN, pateContent);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            }
            logger.info("登录-->图片验证码-->校验成功,taskId={},websiteName={},formType={}", taskId, websiteName, FormType.LOGIN);
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->校验失败,taskId={},websiteName={},formType={},pateContent={}", taskId, websiteName,
                FormType.LOGIN, pateContent, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
        }
    }

    private HttpResult<Map<String, Object>> refeshPicCodeForBillDetail(Long taskId, String websiteName,
                                                                     OperatorParam param) {
        String templateUrl = "http://shop.10086.cn/i/authImg?t={}";
        String url = TemplateUtils.format(templateUrl, System.currentTimeMillis());
        return PluginHttpUtils.refeshPicCodePicCode(taskId, websiteName, url, RETURN_FIELD_PIC_CODE,
            FormType.VALIDATE_BILL_DETAIL);
    }

    private HttpResult<Map<String, Object>> validatePicCodeForBillDetail(Long taskId, String websiteName,
                                                                       OperatorParam param) {
        //http://shop.10086.cn/i/v1/res/precheck/13735874566?captchaVal=123145&_=1500623358942
        String templateUrl = "http://shop.10086.cn/i/v1/res/precheck/{}?captchaVal={}&_={}";
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
        //登陆成功是暂存
        String mobile = redisService.getTaskShare(taskId, AttributeKey.MOBILE);
        String url = TemplateUtils.format(templateUrl, mobile, param.getPicCode(), System.currentTimeMillis());
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String pateContent = null;
        try {
            //结果枚举:正确{"data":null,"retCode":"000000","retMsg":"输入正确，校验成功","sOperTime":null},
            //错误{"data":null,"retCode":"999999","retMsg":"输入错误，校验失败","sOperTime":null}
            pateContent = PluginHttpUtils.getString(url, taskId);
            JSONObject json = JSON.parseObject(pateContent);
            if (!StringUtils.equals("000000", json.getString("retCode"))) {
                logger.error("详单-->图片验证码-->校验失败,taskId={},websiteName={},formType={},pateContent={}", taskId,
                    websiteName, FormType.VALIDATE_BILL_DETAIL, pateContent);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            }
            logger.info("详单-->图片验证码-->校验成功,taskId={},websiteName={},formType={}", taskId, websiteName,
                FormType.VALIDATE_BILL_DETAIL);
            return result.success();
        } catch (Exception e) {
            logger.error("详单-->图片验证码-->校验失败,taskId={},websiteName={},formType={},pateContent={}", taskId, websiteName,
                FormType.VALIDATE_BILL_DETAIL, pateContent, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(Long taskId, String websiteName,
                                                                  OperatorParam param) {
        String templateUrl = "https://login.10086.cn/sendRandomCodeAction.action?type=01&channelID=12003&userName={}";
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String url = TemplateUtils.format(templateUrl, param.getMobile());
        String pageContent = null;
        try {
            pageContent = PluginHttpUtils.postString(url, taskId);
            switch (pageContent) {
                case "0":
                    logger.info("登录-->短信验证码-->刷新成功,taskId={},websiteName={},formType={}", taskId, websiteName,
                        FormType.LOGIN);
                    return result.success();
                case "1":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，短信随机码暂时不能发送，请一分钟以后再试,taskId={},websiteName={},formType={}",
                        taskId, websiteName, FormType.LOGIN);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "对不起,短信随机码暂时不能发送，请一分钟以后再试");
                case "2":
                    logger.warn("登录-->短信验证码-->刷新失败,短信下发数已达上限，您可以使用服务密码方式登录,taskId={},websiteName={},formType={}",
                        taskId, websiteName, FormType.LOGIN);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "短信下发数已达上限");
                case "3":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，短信发送次数过于频繁,taskId={},websiteName={},formType={}", taskId,
                        websiteName, FormType.LOGIN);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "对不起，短信发送次数过于频繁");
                case "4":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，渠道编码不能为空,taskId={},websiteName={},formType={}", taskId,
                        websiteName, FormType.LOGIN);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR);
                case "5":
                    logger.warn("登录-->短信验证码-->刷新失败,对不起，渠道编码异常,taskId={},websiteName={},formType={}", taskId,
                        websiteName, FormType.LOGIN);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR);
                case "4005":
                    logger.warn("登录-->短信验证码-->刷新失败,手机号码有误，请重新输入,taskId={},websiteName={},formType={}", taskId,
                        websiteName, FormType.LOGIN);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "手机号码有误，请重新输入");
                default:
                    logger.error("登录-->短信验证码-->刷新失败,taskId={},websiteName={},formType={},result={}", taskId,
                        websiteName, FormType.LOGIN, pageContent);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,taskId={},websiteName={},formType={},pageContent={}", taskId, websiteName,
                FormType.LOGIN, pageContent, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(Long taskId, String websiteName,
                                                                     OperatorParam param) {
        //https://shop.10086.cn/i/v1/fee/detbillrandomcodejsonp/18838224796?callback=jQuery183002065868962851658_1500889079942&_=1500889136495
        String templateUrl = "https://shop.10086.cn/i/v1/fee/detbillrandomcodejsonp/{}?callback=jQuery183002065868962851658_1500889079942_={}";
        RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
        String mobile = redisService.getTaskShare(taskId, AttributeKey.MOBILE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String url = TemplateUtils.format(templateUrl, param.getMobile(), System.currentTimeMillis());
        String pageContent = null;
        try {
            String referer = TemplateUtils.format("http://shop.10086.cn/i/?welcome={}", mobile);
            pageContent = PluginHttpUtils.getString(url, referer, taskId);
            String jsonString = JsonpUtil.getJsonString(pageContent);
            JSONObject json = JSON.parseObject(jsonString);
            if (!StringUtils.equals("000000", json.getString("retCode"))) {
                logger.error("详单-->短信验证码-->刷新失败,taskId={},websiteName={},formType={},pateContent={}", taskId,
                    websiteName, FormType.VALIDATE_BILL_DETAIL, pageContent);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            }
            logger.info("详单-->短信验证码-->刷新成功,taskId={},websiteName={},formType={}", taskId, websiteName,
                FormType.VALIDATE_BILL_DETAIL);
            return result.success();
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,taskId={},websiteName={},formType={},pageContent={}", taskId, websiteName,
                FormType.LOGIN, pageContent, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(Long taskId, String websiteName, OperatorParam param) {
        //https://shop.10086.cn/i/v1/fee/detailbilltempidentjsonp/13844034615?callback=jQuery183042723042018780055_1500975082967&pwdTempSerCode=NzE2MjUz&pwdTempRandCode=NDI4MTUz&captchaVal=a3xeva&_=1500975147178";
        String templateUrl = "https://shop.10086.cn/i/v1/fee/detailbilltempidentjsonp/{}?pwdTempSerCode={}&pwdTempRandCode={}&captchaVal={}&_={}";
        RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
        String password = redisService.getTaskShare(taskId, AttributeKey.PASSWORD);
        String pwdTempSerCode = Base64.getEncoder().encodeToString(password.getBytes());
        String pwdTempRandCode = Base64.getEncoder().encodeToString(param.getSmsCode().getBytes());
        String loginName = PluginHttpUtils.getCookieValue(taskId, "loginName");

        HttpResult<Map<String, Object>> result = validatePicCodeForBillDetail(taskId, websiteName, param);
        if (!result.getStatus()) {
            return result;
        }
        String url = TemplateUtils.format(templateUrl, loginName, pwdTempSerCode, pwdTempRandCode, param.getPicCode(),
            System.currentTimeMillis());
        String pageContent = null;
        try {
            /**
             * 结果枚举:
             //jQuery183042723042018780055_1500975082967({"data":null,"retCode":"000000","retMsg":"认证成功!","sOperTime":null})
             */
            //没有设置referer会出现connect reset
            String referer = "https://login.10086.cn/html/login/login.html";
            pageContent = PluginHttpUtils.getString(url, referer, taskId);
            String jsonString = JsonpUtil.getJsonString(pageContent);
            JSONObject json = JSON.parseObject(jsonString);
            String code = json.getString("retCode");
            if (StringUtils.equals("000000", code)) {
                logger.info("详单-->校验成功,taskId={},websiteName={},formType={}", taskId, websiteName,
                    FormType.VALIDATE_BILL_DETAIL);
                return result.success();
            }
            switch (code) {
                case "570005":
                    logger.warn("详单-->短信验证码错误,taskId={},websiteName={},url={},formType={}", taskId, websiteName, url,
                        FormType.VALIDATE_BILL_DETAIL);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("详单-->校验失败,taskId={},websiteName={},formType={},pageContent={}", taskId, websiteName,
                        FormType.LOGIN, pageContent);
                    return result.failure(ErrorCode.LOGIN_FAIL);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,taskId={},websiteName={},formType={},pageContent={}", taskId, websiteName,
                FormType.LOGIN, pageContent, e);
            return result.failure(ErrorCode.VALIDATE_FAIL);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(Long taskId, String websiteName, OperatorParam param) {
        String templateUrl = "https://login.10086.cn/login.htm?accountType=01&account={}&password={}&pwdType=01&smsPwd={}&inputCode={}&backUrl=http://shop.10086.cn/i/&rememberMe=0&channelID=12003&protocol=https:&timestamp={}";
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = validatePicCodeForLogin(taskId, websiteName, param);
        if (!result.getStatus()) {
            return result;
        }
        String url = TemplateUtils.format(templateUrl, param.getMobile(), param.getPassword(), param.getSmsCode(),
            param.getPicCode(), System.currentTimeMillis());
        String pageContent = null;
        try {
            /**
             * 结果枚举:
             * 登陆成功:{"artifact":"3490872f8d114992b44dc4e60f595fa0","assertAcceptURL":"http://shop.10086.cn/i/v1/auth/getArtifact"
             ,"code":"0000","desc":"认证成功","islocal":false,"provinceCode":"371","result":"0","uid":"b73f1d1210d94fadaf4ba9ce8c49aef1"
             }
             短信验证码过期:{"code":"6001","desc":"短信随机码不正确或已过期，请重新获取","islocal":false,"result":"8"}
             短信验证码不正确:{"code":"6002","desc":"短信随机码不正确或已过期，请重新获取","islocal":false,"result":"8"}
             {"assertAcceptURL":"http://shop.10086.cn/i/v1/auth/getArtifact","code":"2036","desc":"您的账户名与密码不匹配，请重
             新输入","islocal":false,"result":"2"}
             重复登陆:{"islocal":false,"result":"9"}
             */
            //没有设置referer会出现connect reset
            String referer = "https://login.10086.cn/html/login/login.html";
            pageContent = PluginHttpUtils.getString(url, referer, taskId);
            JSONObject json = JSON.parseObject(pageContent);
            //重复登陆:{"islocal":false,"result":"9"}
            if (StringUtils.equals("9", json.getString("result"))) {
                logger.info("重复登陆,taskId={},websiteName={},formType={}", taskId, websiteName, FormType.LOGIN);
                return result.success();
            }
            String code = json.getString("code");
            String errorMsg = json.getString("desc");
            if (StringUtils.equals("0000", code)) {
                logger.info("登陆成功,taskId={},websiteName={},formType={}", taskId, websiteName, FormType.LOGIN);
                RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);
                //保存手机号和服务密码,详单要用
                redisService.addTaskShare(taskId, AttributeKey.MOBILE, param.getMobile().toString());
                redisService.addTaskShare(taskId, AttributeKey.PASSWORD, param.getPassword());

                //获取权限信息,必须访问下主页,否则详单有些cookie没用
                String artifact = json.getString("artifact");
                url = TemplateUtils.format(
                    "http://shop.10086.cn/i/v1/auth/getArtifact?backUrl=http://shop.10086.cn/i/?f=home&artifact={}",
                    artifact);
                PluginHttpUtils.getString(url, taskId);
                return result.success();
            }
            switch (code) {
                case "2036":
                    logger.warn("登录失败-->账户名与密码不匹配,taskId={},websiteName={},formType={}", taskId, websiteName,
                        FormType.LOGIN);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                case "6001":
                    logger.warn("登录失败-->短信随机码不正确或已过期,taskId={},websiteName={},formType={}", taskId, websiteName,
                        FormType.LOGIN);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                case "6002":
                    logger.warn("登录失败-->短信随机码不正确或已过期,taskId={},websiteName={},formType={}", taskId, websiteName,
                        FormType.LOGIN);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("登陆失败,taskId={},websiteName={},formType={},pageContent={}", taskId, websiteName,
                        FormType.LOGIN, pageContent);
                    if (StringUtils.contains(errorMsg, "密码")) {
                        return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                    }
                    if (StringUtils.contains(errorMsg, "短信")) {
                        return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                    }
                    return result.failure(ErrorCode.LOGIN_FAIL);
            }
        } catch (Exception e) {
            logger.error("登陆失败,taskId={},websiteName={},formType={},pageContent={}", taskId, websiteName,
                FormType.LOGIN, pageContent, e);
            return result.failure(ErrorCode.LOGIN_FAIL);
        }
    }

}
