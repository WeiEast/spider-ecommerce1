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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 表单input标签
 * Created by zhouxinghai on 2017/6/22
 */
public class InputField implements Serializable {

    /**
     * name属性
     */
    private String      name;

    /**
     * 业务 类型 例如:USERNAME,PASSWORD,SMS_CODE,PIC_CODE
     */
    private String      bizType;

    /**
     * type属性 例如:text,password
     */
    private String      type;

    /**
     * label 属性
     */
    private String      label;

    /**
     * 验证input正确性的正则
     */
    private String      validationPattern;

    /**
     * 验证失败提示信息
     */
    private String      validationMsg;

    /**
     * 默认提示信息
     */
    private String      placeholder;

    /**
     * 依赖字段input的name属性
     */
    private Set<String> dependencies = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValidationPattern() {
        return validationPattern;
    }

    public void setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
    }

    public String getValidationMsg() {
        return validationMsg;
    }

    public void setValidationMsg(String validationMsg) {
        this.validationMsg = validationMsg;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

}
