package com.datatrees.rawdatacentral.core.message;

import com.alibaba.rocketmq.client.consumer.MQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.core.model.message.MessageInfo;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractRocketMessageListener<T> implements MessageListenerConcurrently {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRocketMessageListener.class);
    private MQPushConsumer      mqConsumer;
    private String              topicJson;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext arg1) {
        MessageExt message = msgs.get(0);
        long startTime = System.currentTimeMillis();
        logger.info("receive message: msgId={},topic={},tags={},body={}", message.getMsgId(), message.getTopic(),
            message.getTags(), new String(message.getBody()));
        try {
            T convertResult = null;
            try {
                convertResult = messageConvert(message);
                if (convertResult != null && convertResult instanceof MessageInfo) {
                    ((MessageInfo) convertResult).setReconsumeTimes(message.getReconsumeTimes());
                    ((MessageInfo) convertResult).setMsgId(message.getMsgId());
                    ((MessageInfo) convertResult).setBornTimestamp(message.getBornTimestamp());
                }
            } catch (Exception e) {
                logger.error("messageConvert error: msgId={},topic={},tags={},body={}", message.getMsgId(),
                    message.getTopic(), message.getTags(), new String(message.getBody()), e);
            }
            if (convertResult != null) {
                process(convertResult);
            } else {
                logger.warn("empty msg: msgId={},topic={},tags={},body={}", message.getMsgId(), message.getTopic(),
                    message.getTags(), new String(message.getBody()));
            }
            logger.info("process message success: useTime={},msgId={},topic={},tags={},body={}",
                DateUtils.getUsedTime(startTime), message.getMsgId(), message.getTopic(), message.getTags(),
                new String(message.getBody()));
        } catch (Exception e) {
            logger.error("process message error: useTime={},msgId={},topic={},tags={},body={}",
                DateUtils.getUsedTime(startTime), message.getMsgId(), message.getTopic(), message.getTags(),
                new String(message.getBody()), e);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public abstract void process(T message);

    public abstract T messageConvert(MessageExt message);

    public void init() throws Exception {
        if (StringUtils.isEmpty(topicJson) || mqConsumer == null) {
            logger.error("params init error!");
            throw new RuntimeException("params init error!");
        }
        Map<String, String> topicMap = (Map<String, String>) GsonUtils.fromJson(topicJson,
            new TypeToken<Map<String, String>>() {
            }.getType());
        logger.debug("listener topicJson is {}", topicJson);
        for (Entry<String, String> entry : topicMap.entrySet()) {
            mqConsumer.subscribe(entry.getKey(), entry.getValue());
        }
        mqConsumer.registerMessageListener(this);
        mqConsumer.start();
    }

    public MQPushConsumer getMqConsumer() {
        return mqConsumer;
    }

    public void setMqConsumer(MQPushConsumer mqConsumer) {
        this.mqConsumer = mqConsumer;
    }

    /**
     * @return the topicJson
     */
    public String getTopicJson() {
        return topicJson;
    }

    /**
     * @param topicJson the topicJson to set
     */
    public void setTopicJson(String topicJson) {
        this.topicJson = topicJson;
    }

}
