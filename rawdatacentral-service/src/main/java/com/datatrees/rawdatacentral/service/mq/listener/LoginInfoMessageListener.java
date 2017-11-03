package com.datatrees.rawdatacentral.service.mq.listener;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.service.mq.handler.AbstractMessageHandler;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * mq消息处理
 * topic:crawler_monitor
 * Created by zhouxinghai on 2017/10/17
 */
@Service
public class LoginInfoMessageListener implements MessageListenerConcurrently, InitializingBean {

    private static final Logger                              logger          = LoggerFactory.getLogger(LoginInfoMessageListener.class);
    private static final Charset                             DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final Map<String, AbstractMessageHandler> handlers        = new HashMap<>();
    @Value("${core.message.loginInfo.topic}")
    private String                topic;
    @Resource
    private ApplicationContext    applicationContext;
    @Resource
    private DefaultMQPushConsumer loginInfoConsumer;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        MessageExt messageExt = msgs.get(0);
        String topic = messageExt.getTopic();
        String tag = messageExt.getTags();
        String msgId = messageExt.getMsgId();
        String keys = messageExt.getKeys();
        int reconsumeTimes = messageExt.getReconsumeTimes();
        String body = new String(messageExt.getBody(), DEFAULT_CHARSET);
        AbstractMessageHandler handler = handlers.get(tag);
        if (null == handler) {
            logger.warn("no message handler found,丢弃消息,body={},topic={},tag={},msgId={},keys={}", body, topic, tag, msgId, keys);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        int maxRetry = handler.getMaxRetry();
        String bizType = handler.getBizType();
        try {
            Boolean result = handler.consumeMessage(body);
            if (!result && reconsumeTimes <= maxRetry) {
                logger.warn("{}-->失败,稍后重试,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys());
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } else if (reconsumeTimes > maxRetry) {
                logger.warn("{}-->失败,丢弃消息,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys());
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            logger.info("{}-->成功,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Throwable e) {
            if (reconsumeTimes > maxRetry) {
                logger.warn("{}-->失败,丢弃消息,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys());
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } else {
                logger.error("{}-->失败,稍后重试,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys(), e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, AbstractMessageHandler> map = applicationContext.getBeansOfType(AbstractMessageHandler.class);
        if (CollectionUtils.isNotEmpty(map)) {
            for (Map.Entry<String, AbstractMessageHandler> entry : map.entrySet()) {
                AbstractMessageHandler handler = entry.getValue();
                handlers.put(handler.getTag(), handler);
                logger.info("register message handler topic={},tag={},handler={}", handler.getTopic(), handler.getTag(),
                        handler.getClass().getName());
            }
        } else {
            logger.warn("no message handler found");
        }

        loginInfoConsumer.subscribe(topic, "*");
        loginInfoConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        loginInfoConsumer.setMessageModel(MessageModel.CLUSTERING);
        loginInfoConsumer.setConsumeMessageBatchMaxSize(1);
        loginInfoConsumer.registerMessageListener(this);
        loginInfoConsumer.start();

    }
}
