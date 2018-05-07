package com.datatrees.rawdatacentral.collector.worker.filter;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.datatrees.crawler.core.domain.config.search.BusinessType;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.processor.page.handler.BusinessTypeFilterHandler;
import com.datatrees.rawdatacentral.service.AppCrawlerConfigService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.treefinance.saas.grapserver.facade.model.TaskRO;
import com.treefinance.saas.grapserver.facade.service.TaskFacade;
import com.treefinance.saas.knife.result.SaasResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * User: yand
 * Date: 2018/4/9
 */
@Service("businessTypeFilter")
public class BusinessTypeFilter implements BusinessTypeFilterHandler {

    private static final Logger                 logger     = LoggerFactory.getLogger(BusinessTypeFilter.class);
    private final        Cache<String, Boolean> localCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).expireAfterAccess(1, TimeUnit.MINUTES).softValues().build();
    @Resource
    private TaskFacade              taskFacade;
    @Resource
    private AppCrawlerConfigService appCrawlerConfigService;

    @Override
    public Boolean isFilter(String businessType, long taskId) {
        if (StringUtils.isBlank(businessType)) {
            return Boolean.FALSE;
        }

        Boolean filtered = isFiltered(businessType, taskId);

        logger.info("crawling-business decider >> taskId: {}, businessType: {}, filter: {}", taskId, businessType, filtered);

        return filtered;
    }

    private Boolean isFiltered(String businessType, long taskId) {
        BusinessType type = BusinessType.getBusinessType(businessType);
        if (type == null || !type.isEnable()) {
            logger.warn("Disabled crawling-business type >>> {}, taskId: {}", businessType, taskId);
            return Boolean.FALSE;
        }

        try {
            return localCache.get(taskId + "_" + type.getCode(), () -> {
                SaasResult<TaskRO> taskRO = taskFacade.getById(taskId);
                TaskRO data = taskRO.getData();

                logger.debug("taskRO is {}", data);

                if (data != null) {
                    String appId = data.getAppId();
                    String result = appCrawlerConfigService.getFromRedis(appId, type.getCode());

                    logger.info("appId: {}, projectCode: {}, crawlBizConfig: {} ", appId, type.getCode(), result);

                    return Boolean.FALSE.toString().equalsIgnoreCase(result);
                }

                return false;
            });
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException(e);
        }
    }

    public boolean isFilter(SearchTemplateConfig templateConfig, Long taskId) {
        if (templateConfig.getBusinessType() == null) {
            logger.info("Empty businessType with search templateId[{}]", templateConfig.getId());
            return false;
        }

        return isFilter(templateConfig.getBusinessType().getCode(), taskId);
    }

}
