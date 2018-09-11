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

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.domain.TopicTag;
import com.datatrees.spider.share.service.mq.CommonMqService;
import com.datatrees.spider.share.service.mq.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BankSpiderStartMessageHandler implements MessageHandler {

    private static final Logger          logger = LoggerFactory.getLogger(BankSpiderStartMessageHandler.class);

    @Resource
    private              CommonMqService commonMqService;

    @Override
    public String getTag() {
        return TopicTag.LOGIN_INFO.getTag();
    }

    @Override
    public long getExpireTime() {
        return TimeUnit.MINUTES.toMillis(10);
    }

    @Override
    public String getTitle() {
        return "准备";
    }

    @Override
    public boolean consumeMessage(MessageExt messageExt, String msg) {
        return commonMqService.consumeMessage(messageExt, msg);
    }

    @Override
    public int getMaxRetry() {
        return 0;
    }

    @Override
    public String getTopic() {
        return TopicEnum.SPIDER_BANK.getCode();
    }

}
