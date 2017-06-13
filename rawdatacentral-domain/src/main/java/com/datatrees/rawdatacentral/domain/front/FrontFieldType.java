/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.domain.front;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since   2017年6月9日 下午2:42:51 
 */
public enum FrontFieldType {
    USERNAME("USERNAME"),PASSWORD("PASSWORD"),RANDOM("RANDOM"),CODE("CODE");//用户名、密码、短信验证码、图片验证码
    private String type;
    private FrontFieldType(String type) {
        this.type = type;
    }
    private static Map<String,FrontField> frontFieldTypeMap = new HashMap<String, FrontField>();
    static {
        for(FrontFieldType type:FrontFieldType.values()){
            FrontField field = new FrontField();
            switch (type) {
                case USERNAME:
                    field.setName("username");
                    field.setHtmlType("text");
                    field.setType("phone");
                    field.setLabel("手机号");
                    field.setPattern("\\d{11}");
                    field.setValidationMsg("请输入正确的手机号");
                    field.setPlaceholder("请输入手机号码");
                    frontFieldTypeMap.put(type.getType(), field);
                    break;
                case PASSWORD:
                    field.setName("password");
                    field.setHtmlType("password");
                    field.setLabel("服务密码");
                    field.setValidationMsg("请输入服务密码");
                    field.setPlaceholder("请输入服务密码");
                    frontFieldTypeMap.put(type.getType(), field);
                    break;
                case RANDOM:
                    field.setName("random");
                    field.setHtmlType("text");
                    field.setType("smsCaptcha");
                    field.setLabel("短信码");
                    field.setPattern("\\d{4,6}");
                    field.setValidationMsg("请输入短信验证码");
                    field.setPlaceholder("请输入短信验证码");
                    frontFieldTypeMap.put(type.getType(), field);
                    break;
                case CODE:
                    field.setName("code");
                    field.setHtmlType("text");
                    field.setType("imageCaptcha");
                    field.setLabel("图片码");
                    field.setValidationMsg("请输入验证码");
                    field.setPlaceholder("请输入验证码");
                    frontFieldTypeMap.put(type.getType(), field);
                    break;

                default:
                    break;
            }
        }
    }
    public static FrontField getField(String type){
        return frontFieldTypeMap.get(type);
    }
    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return this.getType();
    }
    public static void main(String[] args) {
        System.out.println(FrontFieldType.getField(FrontFieldType.PASSWORD.toString()).getHtmlType());
    }
}
