package com.datatrees.spider.ecommerce.service.mq;

import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.collector.actor.Collector;
import com.datatrees.spider.share.service.mq.AbstractCommonLoginMessageHandler;
import com.treefinance.saas.taskcenter.facade.service.TaskPointFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcommerceSpiderStartMessageHandler extends AbstractCommonLoginMessageHandler {


    @Autowired
    public EcommerceSpiderStartMessageHandler(Collector collector, MonitorService monitorService, TaskPointFacade taskPointFacade) {
        super(collector, monitorService, taskPointFacade);
    }

    @Override
    public String getTopic() {
        return TopicEnum.SPIDER_ECOMMERCE.getCode();
    }

}
