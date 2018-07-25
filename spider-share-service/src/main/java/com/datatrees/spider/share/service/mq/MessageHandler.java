package com.datatrees.spider.share.service.mq;

/**
 * 通用topic处理
 */
public interface MessageHandler {

    //@Value("${core.message.loginInfo.topic}")

    /**
     * 业务类型
     * @return
     */
    String getTitle();

    /**
     * 处理消息
     * @return
     */
    boolean consumeMessage(String msg);

    /**
     * 消费失败最大重试次数
     * @return
     */
    int getMaxRetry();

    /**
     * topic
     * @return
     */
    String getTopic();

    /**
     * 消息tag
     */
    String getTag();

    /**
     * 消息过期时间
     * @return
     */
    long getExpireTime();

}