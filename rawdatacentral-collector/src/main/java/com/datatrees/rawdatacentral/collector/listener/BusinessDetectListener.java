package com.datatrees.rawdatacentral.collector.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.collector.listener.message.BusinessDetectMessage;
import com.datatrees.rawdatacentral.collector.worker.BusinessDetectWorker;
import com.datatrees.rawdatacentral.core.message.AbstractRocketMessageListener;

public class BusinessDetectListener extends AbstractRocketMessageListener<BusinessDetectMessage> {
    private static final Logger logger = LoggerFactory.getLogger(BusinessDetectListener.class);
    private BusinessDetectWorker businessDetectWorker;


    @Override
    public void process(BusinessDetectMessage message) {
        businessDetectWorker.process(message);
    }

    @Override
    public BusinessDetectMessage messageConvert(MessageExt message) {
        String body = new String(message.getBody());
        logger.info("receive businessDetect message body: " + body);
        return (BusinessDetectMessage) GsonUtils.fromJson(body, BusinessDetectMessage.class);

    }

    public BusinessDetectWorker getBusinessDetectWorker() {
        return businessDetectWorker;
    }

    public void setBusinessDetectWorker(BusinessDetectWorker businessDetectWorker) {
        this.businessDetectWorker = businessDetectWorker;
    }

}
