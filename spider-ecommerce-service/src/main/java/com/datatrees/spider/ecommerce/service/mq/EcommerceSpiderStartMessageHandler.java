package com.datatrees.spider.ecommerce.service.mq;

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
public class EcommerceSpiderStartMessageHandler implements MessageHandler {

    private static final Logger          logger = LoggerFactory.getLogger(EcommerceSpiderStartMessageHandler.class);

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
        return TopicEnum.SPIDER_ECOMMERCE.getCode();
    }

}
