package com.datatrees.rawdatacentral.domain.operator;

import java.util.List;

/**
 * 字段配置,用于解析website 里的 initSetting属性
 * Created by zhouxinghai on 2017/6/23.
 */
public class FieldInitSetting {
    /**
     * 字段业务属性FieldBizType
     */
    private String       type;

    /**
     * 依赖业务属性FieldBizType
     */
    private List<String> dependencies;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }
}
