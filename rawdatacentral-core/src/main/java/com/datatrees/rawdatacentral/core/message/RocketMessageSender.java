package com.datatrees.rawdatacentral.core.message;

import javax.annotation.Resource;
import java.util.Map;

import com.datatrees.common.message.MessageSender;
import com.datatrees.common.util.GsonUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class RocketMessageSender implements MessageSender<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RocketMessageSender.class);
    @Resource
    private DefaultMQProducer defaultMQProducer;
    private MessageFactory    factory;

    public MessageFactory getFactory() {
        return factory;
    }

    public void setFactory(MessageFactory factory) {
        this.factory = factory;
    }

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public void setDefaultMQProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    public boolean sendMessage(Object messageMap) {
        try {
            if (messageMap instanceof Map) {
                String topic = (String) ((Map) messageMap).get("topic");
                String tag = (String) ((Map) messageMap).get("tag");
                Object resultMessage = ((Map) messageMap).get("body");
                String userId = (String) ((Map) messageMap).get("userId");
                Message mqMessage = factory.getMessage(topic, tag, GsonUtils.toJson(resultMessage), userId);
                SendResult sendResult = defaultMQProducer.send(mqMessage);
                logger.info("send result message:" + mqMessage + "result:" + sendResult);
                return true;
            }
        } catch (Exception e) {
            logger.error("send message error", e);
        }
        return false;
    }

}
