package com.datatrees.spider.operator.domain.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/20.
 */
public class FormType {

    public static final  String              LOGIN                = "LOGIN";//登陆表单

    public static final  String              LOGIN_POST           = "LOGIN_POST";//登陆后处理

    public static final  String              VALIDATE_BILL_DETAIL = "VALIDATE_BILL_DETAIL";//验证详单

    public static final  String              VALIDATE_USER_INFO   = "VALIDATE_USER_INFO";//验证个人信息

    private static final Map<String, String> allTypes             = new HashMap<>();

    static {
        allTypes.put(LOGIN, "登录");
        allTypes.put(VALIDATE_BILL_DETAIL, "详单");
        allTypes.put(VALIDATE_USER_INFO, "个人信息");
    }

    public static String getName(String type) {
        if (allTypes.containsKey(type)) {
            return allTypes.get(type);
        }
        return "自定义插件" + type;
    }

    public boolean validate(String type) {
        return allTypes.containsKey(type);
    }

}
