package com.datatrees.rawdatacentral.service;

import com.datatrees.crawler.core.domain.config.service.ServiceType;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.rawdatacentral.service.plugin.TaskHttpServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class CrawlerInitService implements InitializingBean {

    static {
        ProcessorFactory.register(ServiceType.Task_Http_Service, TaskHttpServiceImpl.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
