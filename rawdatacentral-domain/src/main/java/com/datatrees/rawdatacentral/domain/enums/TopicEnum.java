package com.datatrees.rawdatacentral.domain.enums;

/**
 * 消息topic
 * Created by zhouxinghai on 2017/4/25.
 */
public enum TopicEnum {

    TASK_NEXT_DIRECTIVE("task_next_directive", "交互指令"),
    CRAWLER_MONITOR("crawler_monitor", " 爬虫监控"),
    TASK_LOG("task_log", "任务状态日志"),
    RAWDATA_INPUT("rawData_input", "登录成功消息"),;
    /**
     * topic代码
     */
    private String code;
    /**
     * 描述
     */
    private String name;

    TopicEnum(String topic, String remark) {
        this.code = topic;
        this.name = remark;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TopicEnum{" + "code='" + code + '\'' + ", name='" + name + '\'' + '}';
    }
}
