/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.bank.service.mq;

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
public class BankMqStartedListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankMqStartedListener.class);

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
        DefaultMQPushConsumer consumer = applicationContext.getBean("bankSpiderConsumer", DefaultMQPushConsumer.class);
        consumer.subscribe(TopicEnum.SPIDER_BANK.getCode(), "*");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setConsumeMessageBatchMaxSize(1);
        consumer.registerMessageListener(messageListener);
        consumer.start();
        LOGGER.info("Started mq consumer");
    }
}
