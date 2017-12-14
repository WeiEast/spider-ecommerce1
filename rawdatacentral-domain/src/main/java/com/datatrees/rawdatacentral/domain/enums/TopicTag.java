package com.datatrees.rawdatacentral.domain.enums;

public enum TopicTag {

    LOGIN_INFO("login_info", "登陆准备"),
    OPERATOR_CRAWLER_START("operator_crawler_start", "运营商爬虫"),
    OPERATOR_LOGIN_POST("operator_login_post", "运营商登陆后"),
    TASK_INIT("task_init", "task初始化"),
    TASK_COMPLETE("task_complete", "task完成"),
    CALLBACK_INFO("callback_info", "回调信息"),;
    private String tag;
    private String remark;

    TopicTag(String tag, String remark) {
        this.tag = tag;
        this.remark = remark;
    }

    public String getTag() {
        return tag;
    }

    public String getRemark() {
        return remark;
    }
}
