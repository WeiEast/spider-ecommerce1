package com.datatrees.rawdatacentral.domain.constant;

import java.io.Serializable;

/**
 * 扩展属性attributes
 * Created by zhouxinghai on 2017/4/25.
 */
public class AttributeKey implements Serializable {

    public static final String CAPTCHA                  = "captcha"; // 验证码
    public static final String STATUS                   = "status"; // 状态
    public static final String REMARK                   = "remark"; // 备注
    public static final String TASK_ID                  = "taskId"; // 任务ID
    public static final String QR                       = "qr";  // 二维码图片
    public static final String ACCOUNT_NO               = "account_No";
    public static final String ACCOUNT_KEY              = "account_key";
    public static final String END_URL                  = "endurl"; // 前段登陆成功跳转的url,里面有sig等可用信息
    public static final String CODE                     = "code"; // 短信验证码或者图片验证码
    public static final String TIPS                     = "tips"; // 提示信息
    public static final String TOKEN                    = "token";  // 请求的token信息
    public static final String ERROR_CODE               = "errorCode"; //错误代码
    public static final String MSG                      = "msg"; //信息
    public static final String ERROR_MSG                = "errorMsg"; // 错误信息
    public static final String ERROR_DETAIL             = "errorDetail"; // 详细错误信息
    public static final String TIMESTAMP                = "timestamp"; // 时间戳
    public static final String ERROR_MESSAGE            = "errorMessage"; // 错误信息
    public static final String HTML                     = "html"; // 网页内容
    public static final String COOKIES                  = "cookies"; // cookies
    public static final String COOKIE                   = "cookie"; // cookies
    public static final String DIRECTIVE_ID             = "directiveId"; // 指令ID
    public static final String DIRECTIVE                = "directive"; // 指令
    public static final String USERNAME                 = "username"; // 用户名
    public static final String PASSWORD                 = "password"; // 密码
    public static final String RANDOM_PASSWORD          = "randomPassword";  // 短信验证码
    public static final String MOBILE                   = "mobile"; // 手机号
    public static final String PROVINCE_CODE            = "provinceCode"; // 省份代码
    public static final String PROVINCE_NAME            = "provinceName"; // 省份代码
    public static final String WEBSITE_NAME             = "websiteName"; // 站点名称
    public static final String ID_CARD                  = "idCard"; // 身份证号
    public static final String REAL_NAME                = "realName"; // 姓名
    public static final String LOGIN_PIC_CODE           = "loginPicCode"; // 登录-->图片验证码
    public static final String BILL_DETAIL_PIC_CODE     = "billDetailPicCode";  // 详单-->图片验证码
    public static final String PIC_CODE                 = "picCode"; // 图片验证码
    public static final String ACCOUNT_BALANCE          = "balance"; // 手机话费余额
    public static final String SMS_CODE                 = "smsCode"; // 短信验证码
    public static final String LATEST_SEND_SMS_TIME     = "latestSendSmsTime";  // 最后一次发送短信时间,有的短信验证码发送有间隔时间限制
    public static final String LATEST_FULL_URL          = "latestFullUrl";  // 最后一次请求url包含参数
    public static final String LATEST_REQUEST_TIMESTAMP = "latestRequestimestamp"; // 最后一次请求时间
    public static final String LATEST_REQUEST_COOKIE    = "latestRequestCookie"; // 最后一次请求时间
    public static final String FORM_TYPE                = "formType"; // 表单类型
    public static final String GROUP_CODE               = "groupCode"; //分组代码
    public static final String GROUP_NAME               = "groupName"; //分组名称
    public static final String WEBSITE_TYPE             = "websiteType"; //配置类型
    public static final String WEBSITE_TITLE            = "websiteTitle"; //配置类型
    public static final String METHOD_NAME              = "methodName"; //方法名称
    public static final String CLASS_NAME               = "className"; //类名称
    public static final String KEY                      = "key"; //关键字
    public static final String PARAM                    = "param"; //参数
    public static final String PARAM_CLASS              = "paramClass"; //参数类型
    public static final String RESULT                   = "result"; //结果
    public static final String RESULT_CLASS             = "resultClass"; //结果类型
    public static final String START_TIME               = "startTime"; //开始时间
    public static final String END_TIME                 = "endTime"; //结束时间

}
