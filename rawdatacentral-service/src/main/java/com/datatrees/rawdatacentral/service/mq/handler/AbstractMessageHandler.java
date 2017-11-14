package com.datatrees.rawdatacentral.service.mq.handler;

import org.springframework.beans.factory.annotation.Value;

/**
 * 通用topic处理
 */
public abstract class AbstractMessageHandler {

    /**
     * 重试次数
     */
    protected final int maxRetry = 2;
    /**
     * 消息topic
     */
    @Value("${core.message.loginInfo.topic}")
    protected String topic;

    /**
     * 消息tag
     */
    public abstract String getTag();

    /**
     * 业务类型
     * @return
     */
    public abstract String getBizType();

    /**
     * 处理消息
     * @return
     */
    public abstract boolean consumeMessage(String msg);

    public int getMaxRetry() {
        return maxRetry;
    }

    public String getTopic() {
        return topic;
    }
}
