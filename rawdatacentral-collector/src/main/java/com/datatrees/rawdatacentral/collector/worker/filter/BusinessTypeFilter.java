package com.datatrees.rawdatacentral.collector.worker.filter;

import javax.annotation.Resource;

import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
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
@Service
public class BusinessTypeFilter {

    private static final Logger logger = LoggerFactory.getLogger(BusinessTypeFilter.class);
    @Resource
    private TaskFacade              taskFacade;
    @Resource
    private AppCrawlerConfigService appCrawlerConfigService;

    public boolean isFilter(SearchTemplateConfig templateConfig, Long taskId) {
        if (StringUtils.isBlank(templateConfig.getBusinessType().getName())) {
            return Boolean.FALSE;
        }
        SaasResult<TaskRO> taskRO = taskFacade.getById(taskId);
        logger.debug("taskRO is {}", taskRO.getData());
        String appId = taskRO.getData().getAppId();
        String project = templateConfig.getBusinessType().getName();
        String result = appCrawlerConfigService.getFromRedis(appId, project);
        logger.debug("result from redis is {},appId is {},project is {}", result, appId, project);
        //result为null，该业务未配置，默认为抓取
        if (StringUtils.isBlank(result)) {
            return Boolean.FALSE;
        } else if (result.equals("false")) {
            return Boolean.FALSE;
        } else if (result.equals("true")) {
            return Boolean.TRUE;
        }
        return false;
    }

}
