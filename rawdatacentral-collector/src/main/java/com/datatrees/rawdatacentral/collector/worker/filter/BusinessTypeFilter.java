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

        BusinessType type = BusinessType.getBusinessType(businessType);
        if (type == null || !type.isEnable()) {
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

                    //result为null，该业务未配置，默认为抓取
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
            logger.info("search businessType is null and search templateId is {}", templateConfig.getId());
            return false;
        }
        logger.debug("bushinessType from searchTemplate is {}", templateConfig.getBusinessType());
        return isFilter(templateConfig.getBusinessType().getCode(), taskId);
    }

}
