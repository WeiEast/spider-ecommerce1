package com.datatrees.rawdatacentral.plugin.operator.he_nan_10086_web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.crawler.plugin.util.PluginHttpUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.OperatorLoginPluginService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
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
public class HeNanLogin10000Service implements OperatorLoginPluginService {

    private static final Logger logger          = LoggerFactory.getLogger(HeNanLogin10000Service.class);

    /**
     * 登陆页
     */
    private static final String preLoginUrl     = "https://login.10086.cn/html/login/login.html";

    /**
     * 刷新图片验证码
     * 类型:GET
     */
    private static final String picCodeUrl      = "https://login.10086.cn/captchazh.htm?type=05&timestamp={}";

    /**
     * 验证图片验证码
     */
    private static final String validPicCodeUrl = "https://login.10086.cn/verifyCaptcha?inputCode={}";

    /**
     * 刷新短信验证码
     * 类型:POST
     */
    private static final String smsCodeUrl      = "https://login.10086.cn/sendRandomCodeAction.action?type=01&channelID=12002&userName={}";

    /**
     * 登陆验证接口
     * 类型:GET
     * 抓包:https://login.10086.cn/login.htm?accountType=01&account=18838224796&password=716253&pwdType=01&smsPwd=073442&inputCode=&backUrl=http://shop.10086.cn/i/&rememberMe=0&channelID=12003&protocol=https:&timestamp=1500457115303
     */
    private static final String loginUrl        = "https://login.10086.cn/login.htm?accountType=01&account={}&password={}&pwdType=01&smsPwd={}&inputCode={}&backUrl=http://shop.10086.cn/i/&rememberMe=0&channelID=12002&protocol=https:&timestamp={}";

    @Override
    public HttpResult<Map<String, Object>> init(Long taskId, String websiteName, OperatorParam param) {
        //不必预登陆,cookie从刷新验证码中获取
        //预登陆可以先返回图片验证码
        return refeshPicCode(taskId, websiteName, param);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshPicCode(Long taskId, String websiteName, OperatorParam param) {
        /**
         * 这里不一定有图片验证码,随机出现
         */
        String url = TemplateUtils.format(picCodeUrl, System.currentTimeMillis());
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            byte[] data = PluginHttpUtils.doGet(url, taskId);
            String picCode = Base64.encodeBase64String(data);
            Map<String, Object> map = new HashMap<>();
            map.put(RETURN_FIELD_PIC_CODE, picCode);
            logger.info("刷新图片验证码成功,taskId={},websiteName={},url={}", taskId, websiteName, url);
            return result.success(map);
        } catch (Exception e) {
            logger.error("刷新图片验证码失败 error taskId={},websiteName={},url={}", taskId, websiteName, url);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(Long taskId, String websiteName, OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        String url = TemplateUtils.format(smsCodeUrl, param.getMobile());
        String pageContent = null;
        try {
            Map<String, String> header = new HashMap<>();
            header.put(HttpHeadKey.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
            header.put(HttpHeadKey.REFERER,
                "https://login.10086.cn/html/login/login.html?channelID=12003&backUrl=http://shop.10086.cn/mall_371_371.html?forcelogin=1");
            pageContent = PluginHttpUtils.postString(url, header, taskId);
            switch (pageContent) {
                case "0":
                    logger.info("短信发送成功,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.success();
                case "1":
                    logger.warn("对不起，短信随机码暂时不能发送，请一分钟以后再试,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "对不起,短信随机码暂时不能发送，请一分钟以后再试");
                case "2":
                    logger.warn("短信下发数已达上限，您可以使用服务密码方式登录,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "短信下发数已达上限");
                case "3":
                    logger.warn("对不起，短信发送次数过于频繁,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "对不起，短信发送次数过于频繁");
                case "4":
                    logger.warn("对不起，渠道编码不能为空,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR);
                case "5":
                    logger.warn("对不起，渠道编码异常,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR);
                case "4005":
                    logger.warn("手机号码有误，请重新输入,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR, "手机号码有误，请重新输入");
                default:
                    logger.error("短信验证码发送失败,taskId={},websiteName={},url={},result={}", taskId, websiteName,
                        pageContent);
                    return result.failure(ErrorCode.REFESH_SMS_ERROR);
            }
        } catch (Exception e) {
            logger.error("短信验证码发送失败,请重试,taskId={},websiteName={},url={},pageContent={}", taskId, websiteName,
                pageContent, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> login(Long taskId, String websiteName, OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = validatePicCode(taskId, websiteName, param);
        if (!result.getStatus()) {
            return result;
        }
        String url = TemplateUtils.format(loginUrl, param.getMobile(), param.getPassword(), param.getSmsCode(),
            param.getPicCode(), System.currentTimeMillis());
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
             */
            String pageContent = PluginHttpUtils.getString(url, preLoginUrl, taskId);
            JSONObject json = JSON.parseObject(pageContent);
            String code = json.getString("code");
            String errorMsg = json.getString("desc");
            switch (code) {
                case "0000":
                    logger.info("登陆成功,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.success();
                case "2036":
                    logger.warn("账户名与密码不匹配,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                case "6001":
                    logger.warn("短信随机码不正确或已过期,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                case "6002":
                    logger.warn("短信随机码不正确或已过期,taskId={},websiteName={},url={}", taskId, websiteName, url);
                    return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                default:
                    logger.error("登陆失败,taskId={},websiteName={},url={},pageContent={}", taskId, websiteName,
                        pageContent);
                    if (StringUtils.contains(errorMsg, "密码")) {
                        return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                    }
                    if (StringUtils.contains(errorMsg, "短信")) {
                        return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
                    }
                    return result.failure(ErrorCode.LOGIN_FAIL);
            }
        } catch (Exception e) {
            logger.error("登陆失败,taskId={},websiteName={},url={}", taskId, websiteName, e);
            return result.failure(ErrorCode.LOGIN_FAIL);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(Long taskId, String websiteName, OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        String url = TemplateUtils.format(validPicCodeUrl, param.getPicCode());
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //结果枚举:正确{"resultCode":"0"},错误{"resultCode":"1"}
            String pateContent = PluginHttpUtils.getString(url, taskId);
            JSONObject json = JSON.parseObject(pateContent);
            if (!StringUtils.equals("0", json.getString("resultCode"))) {
                logger.error("图片验证码验证失败,taskId={},websiteName={},url={},pateContent={}", taskId, websiteName,
                    pateContent);
                return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
            }
            logger.info("图片验证码验证成功,taskId={},websiteName={},url={}", taskId, websiteName);
            return result.success();
        } catch (Exception e) {
            logger.error("图片验证码验证失败,taskId={},websiteName={},url={}", taskId, websiteName, e);
            return result.failure(ErrorCode.VALIDATE_PIC_CODE_FAIL);
        }
    }

    public static void main(String[] args) {
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        int i = 1;
//        while (i++ <= 10) {
//            Cookie cookie = new BasicClientCookie("name" + i, i + "");
//            cookieStore.addCookie(cookie);
//        }
//        String json = JSON.toJSONString(cookieStore.getCookies());
//        System.out.println(json);
//        List<BasicClientCookie> list = JSON.parseArray(json, BasicClientCookie.class);
//        for(Cookie c : list){
//            System.out.println("c.getName() = " + c.getName());
//        }
//        System.out.println(11);

        System.out.println(new Date().toGMTString());

    }
}
