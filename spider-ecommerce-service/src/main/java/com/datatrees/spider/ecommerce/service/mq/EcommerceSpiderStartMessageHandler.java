package com.datatrees.spider.ecommerce.service.mq;

import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.domain.TopicEnum;
import com.datatrees.spider.share.service.MonitorService;
import com.datatrees.spider.share.service.WebsiteHolderService;
import com.datatrees.spider.share.service.collector.actor.Collector;
import com.datatrees.spider.share.service.mq.AbstractCommonLoginMessageHandler;
import com.treefintech.spider.share.integration.manager.TaskPointManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcommerceSpiderStartMessageHandler extends AbstractCommonLoginMessageHandler {

    @Autowired
    public EcommerceSpiderStartMessageHandler(Collector collector, MonitorService monitorService, TaskPointManager taskPointManager, WebsiteHolderService websiteHolderService,
        RedisService redisService) {
        super(collector, monitorService, taskPointManager, websiteHolderService, redisService);
    }

    @Override
    public String getTopic() {
        return TopicEnum.SPIDER_ECOMMERCE.getCode();
    }

}
