package com.datatrees.rawdatacentral.service.mq.listener;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.common.utils.DateUtils;
import com.datatrees.rawdatacentral.domain.enums.TopicEnum;
import com.datatrees.rawdatacentral.service.mq.handler.AbstractMessageHandler;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * mq消息处理
 * topic:crawler_monitor
 * Created by zhouxinghai on 2017/10/17
 */
@Service
public class MessageListener implements MessageListenerConcurrently, InitializingBean {

    private static final Logger                              logger          = LoggerFactory.getLogger(MessageListener.class);
    private static final Charset                             DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final Map<String, AbstractMessageHandler> handlers        = new HashMap<>();
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
        long bornTimestamp = messageExt.getBornTimestamp();
        int reconsumeTimes = messageExt.getReconsumeTimes();

        String body = new String(messageExt.getBody(), DEFAULT_CHARSET);
        AbstractMessageHandler handler = handlers.get(tag);
        if (null == handler) {
            logger.warn("no message handler found,丢弃消息,body={},topic={},tag={},msgId={},keys={}", body, topic, tag, msgId, keys);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        int maxRetry = handler.getMaxRetry();
        String bizType = handler.getBizType();
        if (reconsumeTimes > maxRetry) {
            logger.warn("{}-->失败,丢弃消息,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        if (System.currentTimeMillis() - bornTimestamp > TimeUnit.MINUTES.toMillis(60)) {
            logger.warn("{}-->失败,已过期,丢弃消息,msg={},msgId={},retry={},key={},bornTimestamp={}", bizType, body, msgId, reconsumeTimes,
                    messageExt.getKeys(), DateUtils.formatYmdhms(bornTimestamp));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        try {
            Boolean result = handler.consumeMessage(body);
            if (!result) {
                if (reconsumeTimes < maxRetry) {
                    logger.warn("{}-->失败,稍后重试,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys());
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                } else {
                    logger.warn("{}-->失败,丢弃消息,msg={},msgId={},retry={},key={}", bizType, body, msgId, reconsumeTimes, messageExt.getKeys());
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
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

        loginInfoConsumer.subscribe(TopicEnum.RAWDATA_INPUT.getCode(), "*");
        loginInfoConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        loginInfoConsumer.setMessageModel(MessageModel.CLUSTERING);
        loginInfoConsumer.setConsumeMessageBatchMaxSize(1);
        loginInfoConsumer.registerMessageListener(this);
        loginInfoConsumer.start();

    }
}
