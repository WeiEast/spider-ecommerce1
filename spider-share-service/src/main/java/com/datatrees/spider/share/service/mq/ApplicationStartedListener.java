package com.datatrees.spider.share.service.mq;

import java.util.Map;

import com.treefinance.crawler.framework.context.control.BusinessTypeDecider;
import com.treefinance.crawler.framework.context.control.IBusinessTypeFilter;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Jerry
 * @since 21:57 2018/5/9
 */
@Component
@Order(1)
public class ApplicationStartedListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartedListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("Application context completed!");

        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, IBusinessTypeFilter> beans = applicationContext.getBeansOfType(IBusinessTypeFilter.class);
        if (MapUtils.isNotEmpty(beans)) {
            LOGGER.info("Registering crawling-business filters into decider.");
            BusinessTypeDecider.registerFilters(beans.values());
        }
    }

}
