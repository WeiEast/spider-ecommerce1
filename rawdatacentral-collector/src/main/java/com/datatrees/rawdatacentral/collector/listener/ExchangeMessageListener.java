/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.listener;

import java.util.Arrays;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;
import com.datatrees.rawdatacentral.core.model.message.impl.ExchangeMessage;
import com.datatrees.rawdatacentral.core.service.RedisService;

/**
 * only save to redis ,synchronous to do
 *
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月13日 下午2:25:49
 */
public class ExchangeMessageListener extends AbstractRocketMessageListener<ExchangeMessage> {

    private RedisService redisService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * AbstractRocketMessageListener#process(java.lang.Object)
     */
    @Override
    public void process(ExchangeMessage message) {
        String key = "verify_result_" + message.getWebsiteName() + "_" + message.getUserId();
        redisService.saveListString(key, Arrays.asList(message.getMessage()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * AbstractRocketMessageListener#messageConvert(com.alibaba
     * .rocketmq.common.message.Message)
     */
    @Override
    public ExchangeMessage messageConvert(MessageExt message) {
        String body = new String(message.getBody());
        ExchangeMessage result = (ExchangeMessage) GsonUtils.fromJson(body, ExchangeMessage.class);
        result.setMessage(body);
        return result;
    }

    /**
     * @return the redisService
     */
    public RedisService getRedisService() {
        return redisService;
    }

    /**
     * @param redisService the redisService to set
     */
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

}
