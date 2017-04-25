/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.collector.worker.ReissueDetectWorker;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;
import com.datatrees.rawdatacentral.core.model.message.impl.ReissueDetectMessage;

/**
 * only save to redis ,synchronous to do
 *
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月13日 下午2:25:49
 */
public class MailReissueDetectListener extends AbstractRocketMessageListener<ReissueDetectMessage> {
    private static final Logger logger = LoggerFactory.getLogger(MailReissueDetectListener.class);
    private ReissueDetectWorker reissueDetectWorker;

    /*
     * (non-Javadoc)
     * 
     * @see
     * AbstractRocketMessageListener#process(java.lang.Object)
     */
    @Override
    public void process(ReissueDetectMessage message) {
        reissueDetectWorker.process(message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * AbstractRocketMessageListener#messageConvert(com.alibaba
     * .rocketmq.common.message.Message)
     */
    @Override
    public ReissueDetectMessage messageConvert(MessageExt message) {
        String body = new String(message.getBody());
        logger.info("reveive reissueDetect message body: " + body);
        return (ReissueDetectMessage) GsonUtils.fromJson(body, ReissueDetectMessage.class);
    }

    /**
     * @return the reissueDetectWorker
     */
    public ReissueDetectWorker getReissueDetectWorker() {
        return reissueDetectWorker;
    }

    /**
     * @param reissueDetectWorker the reissueDetectWorker to set
     */
    public void setReissueDetectWorker(ReissueDetectWorker reissueDetectWorker) {
        this.reissueDetectWorker = reissueDetectWorker;
    }

}
