package com.datatrees.spider.share.service.mq;

import com.alibaba.rocketmq.common.message.MessageExt;

public interface CommonMqService {

    boolean consumeMessage(MessageExt messageExt, String msg);
}
