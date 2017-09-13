package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.rawdatacentral.api.CrawlerTaskService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.service.TaskService;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.apache.commons.lang3.StringUtils;
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
        //是否是独立运营商
        Boolean isNewOperator = TaskUtils.isNewOperator(taskId);
        //获取第一次消息用的websiteName
        String firstVisitWebsiteName = redisService.getString(RedisKeyPrefixEnum.TASK_FIRST_VISIT_WEBSITENAME, taskId);
        //兼容老的
        if (StringUtils.isBlank(firstVisitWebsiteName)) {
            firstVisitWebsiteName = redisService.getString(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME, taskId);
        }
        Map<String, String> map = new HashMap<>();
        map.put(AttributeKey.TASK_ID, taskId + "");
        //使用伪装的webisteName
        map.put(AttributeKey.WEBSITE_NAME, firstVisitWebsiteName);
        GroupEnum group = null;
        if (isNewOperator) {
            WebsiteOperator websiteOperator = websiteOperatorService.getByWebsiteName(TaskUtils.getRealWebsiteName(firstVisitWebsiteName));
            map.put(AttributeKey.WEBSITE_TITLE, websiteOperator.getWebsiteTitle());
            group = GroupEnum.getByGroupCode(websiteOperator.getGroupCode());
        } else {
            group = GroupEnum.getByWebsiteName(firstVisitWebsiteName);
        }
        map.put(AttributeKey.GROUP_CODE, group.getGroupCode());
        map.put(AttributeKey.GROUP_NAME, group.getGroupName());
        map.put(AttributeKey.WEBSITE_TYPE, group.getWebsiteType().getValue());
        map.put(AttributeKey.TIMESTAMP, System.currentTimeMillis() + "");
        return map;
    }
}
