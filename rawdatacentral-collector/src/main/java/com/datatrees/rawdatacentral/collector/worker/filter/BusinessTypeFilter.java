package com.datatrees.rawdatacentral.collector.worker.filter;

import javax.annotation.Resource;

import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.processor.page.handler.BusinessTypeFilterHandler;
import com.datatrees.rawdatacentral.service.AppCrawlerConfigService;
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

    private static final Logger logger = LoggerFactory.getLogger(BusinessTypeFilter.class);
    @Resource
    private TaskFacade              taskFacade;
    @Resource
    private AppCrawlerConfigService appCrawlerConfigService;

    @Override
    public Boolean isFilter(String businessType, long taskId) {
        if (StringUtils.isBlank(businessType)) {
            return Boolean.FALSE;
        }
        SaasResult<TaskRO> taskRO = taskFacade.getById(taskId);
        logger.info("taskRO is {}", taskRO.getData());
        if (taskRO.getData() != null) {
            String appId = taskRO.getData().getAppId();
            String result = appCrawlerConfigService.getFromRedis(appId, businessType);
            logger.info("result from redis is {},appId is {},project is {}", result, appId, businessType);
            //result为null，该业务未配置，默认为抓取
            if (StringUtils.isBlank(result)) {
                return Boolean.FALSE;
            } else if (result.equals("true")) {
                return Boolean.FALSE;
            } else if (result.equals("false")) {
                return Boolean.TRUE;
            }
        }

        return false;
    }

    public boolean isFilter(SearchTemplateConfig templateConfig, Long taskId) {
        logger.info(" BusinessTypeFilter templateConfig is {},taskId is {}", templateConfig, taskId);
        if (templateConfig.getBusinessType() == null) {
            logger.info("search templateId is {}", templateConfig.getId());
            return false;
        }
        logger.info("bushinessType from searchTemplate is {}", templateConfig.getBusinessType());
        return isFilter(templateConfig.getBusinessType().getCode(), taskId);
    }

}
