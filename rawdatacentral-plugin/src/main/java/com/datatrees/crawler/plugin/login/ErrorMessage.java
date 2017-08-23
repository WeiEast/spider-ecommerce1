package com.datatrees.crawler.plugin.login;

public interface ErrorMessage {

    String DEFAULT_ERROR          = "运营商正在升级维护，请稍后重试。";
    String INFO_NOT_ENOUGTH_ERROR = "信息不完整，请按要求完成输入。";
    String NOT_CONFORM_ERROR      = "您的账号存在异常，请登录运营商官网进行验证确认。";
    String SERVER_INTERNAL_ERROR  = "系统内部错误，请稍后重试。";
    String VAILD_CODE_ERROR       = "验证码错误，请输入正确的验证码。";
    String REGION_CHECK_ERROR     = "手机号码与运营商归属地不符，请重新输入。";
    String UNKNOWN_ERROR          = "未知异常，请稍后重试。";
    String LOGIN_DEFAULT_ERROR    = "登录失败，请重试";
    String USER_PASSWORD_ERROR    = "用户名或密码错误，请重新输入。";
    String MAIL_DEFAULT_ERROR     = "邮箱正在升级维护，请稍后重试。";
}
