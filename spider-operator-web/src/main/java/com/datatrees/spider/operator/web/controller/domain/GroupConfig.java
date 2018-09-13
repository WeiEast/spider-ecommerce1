package com.datatrees.spider.operator.web.controller.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GroupConfig implements Serializable {

    private String               groupCode;

    private Map<String, Integer> config;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Map<String, Integer> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Integer> config) {
        this.config = config;
    }
}
