package com.datatrees.rawdatacentral.domain.enums;

public enum TaskStageEnum {

    RECEIVE("RECEIVE", "收到消息"),
    INIT_SUCCESS("INIT_SUCCESS", "初始化成功"),
    LOGIN_SUCCESS("LOGIN_SUCCESS", "登录成功"),
    LOGIN_POST_SUCCESS("LOGIN_POST_SUCCESS", "登录后处理成功"),
    CRAWLER_START("CRAWLER_START", "爬虫启动"),;

    private String status;

    private String remark;

    TaskStageEnum(String status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }
}
