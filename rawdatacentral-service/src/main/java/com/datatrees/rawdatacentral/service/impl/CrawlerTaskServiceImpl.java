package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.CrawlerTaskService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.service.TaskService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CrawlerTaskServiceImpl implements CrawlerTaskService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerTaskServiceImpl.class);
    @Resource
    private TaskService            taskService;
    @Resource
    private RedisService           redisService;
    @Resource
    private WebsiteOperatorService websiteOperatorService;

    @Override
    public Task getByTaskId(Long taskId) {
        return taskService.getByTaskId(taskId);
    }

    @Override
    public Map<String, String> getTaskBaseInfo(Long taskId) {
        Map<String, String> map = new HashMap<>();
        try {
            Website website = redisService.getCache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, new TypeReference<Website>() {});
            if (null == website) {
                logger.error("not found website from cache,taskId={}", taskId);
                return map;
            }
            map.put(AttributeKey.TASK_ID, taskId + "");
            map.put(AttributeKey.WEBSITE_TITLE, website.getWebsiteTitle());
            map.put(AttributeKey.WEBSITE_NAME, website.getWebsiteName());
            map.put(AttributeKey.GROUP_CODE, website.getGroupCode());
            map.put(AttributeKey.GROUP_NAME, website.getGroupName());
            map.put(AttributeKey.WEBSITE_TYPE, website.getWebsiteType());
            map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis() + "");
            return map;
        } catch (Exception e) {
            logger.error("getTaskBaseInfo error taskId={}", taskId);
            return map;
        }
    }
}
