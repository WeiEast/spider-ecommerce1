package com.datatrees.spider.operator.service.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.service.mq.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Jerry
 * @since 21:57 2018/5/9
 */
@Component
@Order(1)
public class OperatorMqStartedListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorMqStartedListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            startSpiderMqConsumer(event.getApplicationContext());
        } catch (MQClientException e) {
            LOGGER.error("start mq error", e);
        }
    }

    /**
     * // 等应用容器启动完成后再开始启动mq consumer，开始消费
     * @param applicationContext spring容器
     * @exception MQClientException
     */
    private void startSpiderMqConsumer(ApplicationContext applicationContext) throws MQClientException {
        LOGGER.info("Starting mq consumer");
        MessageListener messageListener = applicationContext.getBean(MessageListener.class);
        DefaultMQPushConsumer consumer = applicationContext.getBean("operatorSpiderConsumer", DefaultMQPushConsumer.class);
        consumer.subscribe(TopicEnum.SPIDER_OPERATOR.getCode(), "*");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.registerMessageListener(messageListener);
        consumer.start();
        LOGGER.info("Started mq consumer");
    }
}