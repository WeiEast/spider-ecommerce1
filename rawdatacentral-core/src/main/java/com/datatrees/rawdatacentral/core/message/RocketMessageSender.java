package com.datatrees.rawdatacentral.core.message;

import java.util.Map;

import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.message.MessageSender;
import com.datatrees.common.util.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketMessageSender implements MessageSender<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RocketMessageSender.class);
    private MQProducer     producer;
    private MessageFactory factory;

    public MessageFactory getFactory() {
        return factory;
    }

    public void setFactory(MessageFactory factory) {
        this.factory = factory;
    }

    public MQProducer getProducer() {
        return producer;
    }

    public void setProducer(MQProducer producer) {
        this.producer = producer;
    }

    public boolean sendMessage(Object messageMap) {
        try {
            if (messageMap instanceof Map) {
                String topic = (String) ((Map) messageMap).get("topic");
                String tag = (String) ((Map) messageMap).get("tag");
                Object resultMessage = ((Map) messageMap).get("body");
                String userId = (String) ((Map) messageMap).get("userId");
                Message mqMessage = factory.getMessage(topic, tag, GsonUtils.toJson(resultMessage), userId);
                SendResult sendResult = producer.send(mqMessage);
                logger.info("send result message:" + mqMessage + "result:" + sendResult);
                return true;
            }
        } catch (Exception e) {
            logger.error("send message error", e);
        }
        return false;
    }

}
