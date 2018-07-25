package com.datatrees.rawdatacentral.context;

import java.util.Map;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.service.mq.MessageListener;
import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.framework.context.control.BusinessTypeDecider;
import com.treefinance.crawler.framework.context.control.IBusinessTypeFilter;
import org.apache.commons.collections.MapUtils;
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
public class ApplicationStartedListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartedListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Application context completed!");

        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, IBusinessTypeFilter> beans = applicationContext.getBeansOfType(IBusinessTypeFilter.class);
        if (MapUtils.isNotEmpty(beans)) {
            LOGGER.info("Registering crawling-business filters into decider.");
            BusinessTypeDecider.registerFilters(beans.values());
        }

        try {
            startMqConsumer(applicationContext);
        } catch (MQClientException e) {
            throw new UnexpectedException("Unexpected exception when starting mq consumer.", e);
        }
    }

    /**
     * // 等应用容器启动完成后再开始启动mq consumer，开始消费
     * @param applicationContext spring容器
     * @exception MQClientException
     */
    private void startMqConsumer(ApplicationContext applicationContext) throws MQClientException {
        LOGGER.info("Starting mq consumer");
        MessageListener messageListener = applicationContext.getBean(MessageListener.class);
        DefaultMQPushConsumer loginInfoConsumer = applicationContext.getBean(DefaultMQPushConsumer.class);
        loginInfoConsumer.subscribe(TopicEnum.RAWDATA_INPUT.getCode(), "*");
        loginInfoConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        loginInfoConsumer.setMessageModel(MessageModel.CLUSTERING);
        loginInfoConsumer.setConsumeMessageBatchMaxSize(1);
        loginInfoConsumer.registerMessageListener(messageListener);
        loginInfoConsumer.start();
        LOGGER.info("Started mq consumer");
    }
}
