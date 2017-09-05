package com.datatrees.rawdatacentral.domain.enums;

public enum TopicTag {

    TASK_COMPLETE("task_complete", "task完成"),
    TASK_ERROR("task_error", "task错误信息"),
    TASK_LOG("task_log", "task日志"),
    ;
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
