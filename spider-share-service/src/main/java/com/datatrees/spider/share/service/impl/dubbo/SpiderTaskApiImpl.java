package com.datatrees.spider.share.service.impl.dubbo;

import javax.annotation.Resource;
import java.util.Map;

import com.datatrees.spider.share.api.SpiderTaskApi;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.model.Task;
import com.datatrees.spider.share.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SpiderTaskApiImpl implements SpiderTaskApi {

    private static final Logger      logger = LoggerFactory.getLogger(SpiderTaskApiImpl.class);

    @Resource
    private              TaskService taskService;

    @Override
    public Task getByTaskId(Long taskId) {
        return taskService.getByTaskId(taskId);
    }

    @Override
    public Map<String, String> getTaskBaseInfo(Long taskId) {
        return getTaskBaseInfo(taskId, null);
    }

    @Override
    public Map<String, String> getTaskBaseInfo(Long taskId, String websiteName) {
        Map<String, String> map = TaskUtils.getTaskShares(taskId);
        map.put(AttributeKey.TASK_ID, String.valueOf(taskId));
        map.put(AttributeKey.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        return map;
    }

    @Override
    public String getTaskAccountNo(Long taskId) {
        String redisKey = RedisKeyPrefixEnum.TASK_INFO_ACCOUNT_NO.getRedisKey(taskId);
        String accountNo = RedisUtils.get(redisKey);
        return accountNo;
    }
}
