/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.collector.listener;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.rawdatacentral.collector.worker.Rong360InfoWorker;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;
import com.datatrees.rawdatacentral.core.model.message.impl.Rong360InfoMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年9月20日 下午5:10:06
 */
public class Rong360InfoListener extends AbstractRocketMessageListener<Rong360InfoMessage> {

    private static final Logger logger = LoggerFactory.getLogger(Rong360InfoListener.class);
    private Rong360InfoWorker worker;

    @Override
    public void process(Rong360InfoMessage message) {
        getWorker().process(message);
    }

    @Override
    public Rong360InfoMessage messageConvert(MessageExt message) {
        String body = new String(message.getBody());
        logger.info("receive Rong360Info message body: " + body);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(body, Rong360InfoMessage.class);
        } catch (Exception e) {
            logger.warn("messageConvert error:" + e.getMessage(), e);
        }
        return new Rong360InfoMessage();
    }

    /**
     * @return the worker
     */
    public Rong360InfoWorker getWorker() {
        return worker;
    }

    /**
     * @param worker the worker to set
     */
    public void setWorker(Rong360InfoWorker worker) {
        this.worker = worker;
    }

}
