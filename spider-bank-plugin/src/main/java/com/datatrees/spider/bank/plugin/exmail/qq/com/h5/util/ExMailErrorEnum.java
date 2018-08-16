/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.bank.plugin.exmail.qq.com.h5.util;

/**
 * Created by zhangyanjia on 2018/2/27.
 */
public enum ExMailErrorEnum {
    ERRORADMINNAME("errorAdminName", "此管理员帐号不存在，请重新输入"),
    ERRORUSERNAME("errorUserName", "您输入的邮箱帐号不正确，请重新输入。"),
    EMPTYUSERNAME("emptyUserName", "请填写您的邮箱帐号。"),
    EMPTYPASSWORD("emptyPassword", "请填写邮箱密码。"),
    EMPTYVERIFYCODE("emptyVerifyCode", "请填写验证码。"),
    ERRORPASSOWRDTOOLONG("errorPassowrdTooLong", "邮箱密码不能超过100个字符。"),
    ERRORNAMEPASSOWRD("errorNamePassowrd", "您填写的帐号或密码不正确，请再次尝试。"),
    ERRORREQUIRESECONDPASSWORD("errorRequireSecondPassword", "请使用邮箱的“独立密码”登录。"),
    ERRORVERIFYCODE("errorVerifyCode", "您填写的验证码不正确。"),
    FREQUENT("frequent", "为了保障邮箱安全，请输入验证码。"),
    ERRORSECONDPASSWORD("errorSecondPassword", "独立密码输入有误。"),
    ERRORSECONDPWDNEEDQQERR("errorSecondPwdNeedQQErr", "您设置了网页登录须先输入QQ密码。"),
    ERRORNEEDQQPROTECT("errorNeedQQProtect", "您的QQ帐号处于未保护状态，暂时无法登录"),
    ERRORBLOCKIPERR("errorBlockIPErr", "为了保障邮箱安全，暂时不能使用页面登录，请登录QQ后跳转邮箱。"),
    ERRORDISTINCTVALID("errorDistinctValid", "为了保障邮箱安全，请再次输入验证码登录。"),
    ERRORNEEDJUMPFOXMAIL("errorNeedJumpFoxmail", "请到www.foxmail.com登录该帐户"),
    ERRORPERMISSIONDENIED("errorPermissionDenied", "您还未被邀请使用企业邮箱。<br/>您可以登录您的QQ邮箱，在体验室中自助开通。"),
    ERRORLOGINWITHQQACCOUNT("errorLoginWithQQAccount", "请使用企业邮箱帐号登录。"),
    ERRORBIZMAILMX("errorBizmailMX", "登录失败。您域名的MX记录未通过验证，请联系管理员。"),
    ERRORBIZMAILLOCKED("errorBizmailLocked", "登录失败。您的域名已被锁定，请联系管理员。"),
    ERRORBINDNULLUIN("errorBindNullUin", "帐号为空，请重输"),
    ERRORBINDERR("errorBindErr", "帐号绑定关系错误，请联系管理员"),
    ERRORBINDFAIL("errorBindFail", "帐号绑定关系查询出错，请稍后再试"),
    ERRORSYSERR("errorSysErr", "登录失败，请咨询企业邮箱客服。"),
    ERRORNEEDOPEN("errorNeedOpen", "您的企业邮箱账号未开通，请联系企业邮箱管理员。"),
    ERRORBIZMAILLOGINLIMIT("errorBizmailLoginLimit", "管理员限制该IP登录企业邮箱"),
    ERRORWEIXINBIND("errorWeixinBind", "您的微信没有绑定企业邮箱，请先登录后绑定"),
    ERRORDYNPWDSENDFAILED("errorDynPwdSendFailed", "动态密码推送失败，请稍后重试"),
    ERRORTOKENEXPIREDSCAN("errorTokenExpiredScan", "请扫码登录"),
    ERRORADSCAN("errorADScan", "你的帐号需要使用微信扫码登录，请使用微信扫描右侧二维码登录");

    private String code;

    private String message;

    ExMailErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessageByCode(String code) {
        for (ExMailErrorEnum e : ExMailErrorEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.message;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return message;
    }

    @Override
    public String toString() {
        return "OperationEnum{" + "code='" + code + '\'' + ", message='" + message + '\'' + '}';
    }

}
