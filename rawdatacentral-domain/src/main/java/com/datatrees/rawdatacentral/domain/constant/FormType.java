package com.datatrees.rawdatacentral.domain.constant;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by zhouxinghai on 2017/7/20.
 */
public class FormType {

    public static final String       LOGIN                = "LOGIN";                //登陆表单
    public static final String       VALIDATE_BILL_DETAIL = "VALIDATE_BILL_DETAIL"; //验证详单
    public static final String       VALIDATE_USER_INFO   = "VALIDATE_USER_INFO";   //验证个人信息

    private static final Set<String> allTypes             = new HashSet<>();

    static {
        allTypes.add(LOGIN);
        allTypes.add(VALIDATE_BILL_DETAIL);
        allTypes.add(VALIDATE_USER_INFO);
    }

    public boolean validate(String type) {
        return allTypes.contains(type);
    }

}
