package com.datatrees.rawdatacentral.domain.constant;

import java.io.Serializable;

/**
 * 扩展属性attributes
 * Created by zhouxinghai on 2017/4/25.
 */
public class AttributeKey implements Serializable {

    public static final String CAPTCHA         = "captcha";        // 验证码
    public static final String STATUS          = "status";         // 状态
    public static final String REMARK          = "remark";         // 备注
    public static final String TASK_ID         = "taskId";         // 任务ID
    public static final String QR              = "qr";             // 二维码图片
    public static final String ACCOUNT_NO      = "account_No";
    public static final String ACCOUNT_KEY     = "account_key";
    public static final String END_URL         = "endurl";         // 前段登陆成功跳转的url,里面有sig等可用信息
    public static final String CODE            = "code";           // 短信验证码或者图片验证码
    public static final String TIPS            = "tips";           // 提示信息
    public static final String ERROR_CODE      = "errorCode";      // plugin返回消息,如果有,if (resultMap.containsKey("errorCode")) {throw new PluginExeception("error duing plugin invoke!");}
    public static final String ERROR_MESSAGE   = "errorMessage";   //plugin返回错误消息
    public static final String HTML            = "html";           //网页内容
    public static final String COOKIES         = "cookies";        //cookies
    public static final String DIRECTIVE_ID    = "directiveId";    //指令ID
    public static final String DIRECTIVE       = "directive";      //指令
    public static final String USERNAME        = "username";       //用户名
    public static final String PASSWORD        = "password";       //密码
    public static final String RANDOM_PASSWORD = "randomPassword"; //短信验证码

}
