package com.datatrees.rawdatacentral.domain.enums;

/**
 * 消息topic
 * Created by zhouxinghai on 2017/4/25.
 */
public enum TopicEnum {

    RAWDATA_INTER_DIRECTIVE("rawdata_inter_directive", "交互指令"),
    RAWDATA_TASK_LOG("rawdata_task_log", "任务状态日志"),;

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
        return "TopicEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
