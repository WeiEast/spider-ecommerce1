package com.datatrees.rawdatacentral.domain.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息
 * Created by zhouxinghai on 2017/4/25.
 */
public class TaskMessage implements Serializable {

    /**
     * 消息topic
     */
    private String topic;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * web名称
     */
    private String websiteName;

    /**
     * 数据类型
     */
    private String websiteType;

    /**
     * 业务时间
     */
    private long timestamp = System.currentTimeMillis();

    /**
     * 扩展属性
     */
    private Map<String, Object> attributes = new HashMap<>();

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(String websiteType) {
        this.websiteType = websiteType;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 设置属性
     *
     * @param key
     * @param value
     */
    public void setAttribute(String key, Object value) {
        if (null != key) {
            attributes.put(key, value);
        }
    }

    /**
     * 从attributes获取属性
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        if (attributes.containsKey(key)) {
            return String.valueOf(key);
        }
        return null;
    }

    /**
     * 从attributes获取属性
     *
     * @param key
     * @return
     */
    public Long getLong(String key) {
        if (attributes.containsKey(key)) {
            return Long.valueOf(key);
        }
        return null;
    }

    /**
     * 从attributes获取属性
     *
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        if (attributes.containsKey(key)) {
            return Boolean.valueOf(key);
        }
        return null;
    }


}
