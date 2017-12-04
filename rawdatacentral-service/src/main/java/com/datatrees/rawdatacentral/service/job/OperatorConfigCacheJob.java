package com.datatrees.rawdatacentral.service.job;

import javax.annotation.Resource;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.datatrees.rawdatacentral.service.WebsiteGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperatorConfigCacheJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(OperatorConfigCacheJob.class);
    @Resource
    private WebsiteGroupService websiteGroupService;

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            websiteGroupService.updateCache();
            logger.info("update operator group config success");
        } catch (Throwable e) {
            logger.error("update operator group config error", e);
        }
    }
}
