/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.domain.front;

import java.util.List;

/**
 *
 * @author  <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since   2017年6月9日 下午2:40:05 
 */
public class FrontField {
    private String name;
    private String htmlType;
    private String type;
    private String label;
    private String pattern;
    private String validationMsg;
    private String placeholder;
    private List<String> dependencies;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHtmlType() {
        return htmlType;
    }
    public void setHtmlType(String htmlType) {
        this.htmlType = htmlType;
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
    public String getPattern() {
        return pattern;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
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
    public List<String> getDependencies() {
        return dependencies;
    }
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }
    
}
