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

package com.datatrees.spider.operator.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * input 标签 业务类型
 * Created by zhouxinghai on 2017/6/22
 */
public enum FieldBizType {

    USERNAME("USERNAME", "手机号"),
    PASSWORD("PASSWORD", "服务密码"),
    SMS_CODE("SMS_CODE", "短信验证码"),
    PIC_CODE("PIC_CODE", "图片验证码"),
    REAL_NAME("REAL_NAME", "真实姓名"),
    ID_CARD("ID_CARD", "身份证号码");

    /**
     * bizType对应的input标签属性
     */
    public static final Map<String, InputField> fields = new HashMap<>();

    static {
        for (FieldBizType type : FieldBizType.values()) {
            InputField field = new InputField();
            field.setBizType(type.getCode());
            field.setLabel(type.getRemark());
            switch (type) {
                case USERNAME:
                    field.setName("username");
                    field.setType("text");
                    field.setValidationPattern("\\d{11}");
                    field.setValidationMsg("请输入正确的手机号");
                    field.setPlaceholder("请输入手机号码");
                    break;
                case PASSWORD:
                    field.setName("password");
                    field.setType("password");
                    field.setValidationPattern("");
                    field.setValidationMsg("请输入服务密码");
                    field.setPlaceholder("请输入服务密码");
                    break;
                case SMS_CODE:
                    field.setName("randomPassword");
                    field.setType("text");
                    field.setValidationPattern("");
                    field.setValidationPattern("\\d{4,6}");
                    field.setValidationMsg("请输入短信验证码");
                    field.setPlaceholder("请输入短信验证码");
                    break;
                case PIC_CODE:
                    field.setName("code");
                    field.setType("text");
                    field.setValidationPattern("");
                    field.setValidationMsg("请输入验证码");
                    field.setPlaceholder("请输入验证码");
                    break;
                case REAL_NAME:
                    field.setName("realName");
                    field.setType("text");
                    field.setValidationPattern("");
                    field.setValidationMsg("请输入真实姓名");
                    field.setPlaceholder("请输入真实姓名");
                    break;
                case ID_CARD:
                    field.setName("idCard");
                    field.setType("text");
                    field.setValidationPattern(
                            "^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$");
                    field.setValidationMsg("请输入身份证号码");
                    field.setPlaceholder("请输入身份证号码");
                    break;
                default:
                    throw new RuntimeException("not support input biz type" + type.getRemark());
            }
            fields.put(type.getCode(), field);
        }
    }

    /**
     * 字段类型
     */
    private final String code;

    /**
     * 要显示的label
     */
    private final String remark;

    FieldBizType(String code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public String getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return "FieldBizType{" + "code='" + code + '\'' + ", remark='" + remark + '\'' + '}';
    }
}
