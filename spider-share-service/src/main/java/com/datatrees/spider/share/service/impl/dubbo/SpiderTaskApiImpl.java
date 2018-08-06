package com.datatrees.spider.share.service.impl.dubbo;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.api.SpiderTaskApi;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.ProcessResultUtils;
import com.datatrees.spider.share.common.utils.RedisUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.domain.directive.DirectiveRedisCode;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import com.datatrees.spider.share.domain.directive.DirectiveType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.model.Task;
import com.datatrees.spider.share.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SpiderTaskApiImpl implements SpiderTaskApi {

    private static final Logger       logger = LoggerFactory.getLogger(SpiderTaskApiImpl.class);

    @Resource
    private              TaskService  taskService;

    @Resource
    private              RedisService redisService;

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

    @Override
    public HttpResult<Boolean> importCrawlCode(String directiveId, long taskId, int type, String code, Map<String, String> extra) {
        HttpResult<Boolean> result = new HttpResult<>();
        try {
            if (null == extra) {
                extra = new HashMap<>();
            }
            if (taskId <= 0 || type < 0 || StringUtils.isAnyBlank(directiveId, code)) {
                logger.warn("invalid param taskId={},type={},directiveId={},code={},extra={}", taskId, type, directiveId, code,
                        JSON.toJSONString(extra));
                return result.failure("参数为空或者参数不完整");
            }
            String status = DirectiveRedisCode.WAIT_SERVER_PROCESS;
            String directiveType = null;
            switch (type) {
                case 0:
                    directiveType = DirectiveType.CRAWL_SMS;
                    break;
                case 1:
                    directiveType = DirectiveType.CRAWL_CODE;
                    break;
                case 3:
                    directiveType = DirectiveType.LOGIN_SECOND_PASSWORD;
                    Long processId = Long.parseLong(extra.get("processId"));
                    ProcessResult<Object> processResult = ProcessResultUtils.queryProcessResult(processId);
                    if (StringUtils.equals(processResult.getProcessStatus(), ProcessStatus.REQUIRE_SECOND_PASSWORD)) {
                        processResult.setProcessStatus(ProcessStatus.PROCESSING);
                        ProcessResultUtils.saveProcessResult(processResult);
                        TaskUtils.addTaskShare(taskId, AttributeKey.QR_STATUS, QRStatus.WAITING);
                    }
                    break;
                default:
                    logger.warn("invalid param taskId={},type={}", taskId, type);
                    return result.failure("未知参数type");
            }

            extra.put(AttributeKey.CODE, code);
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(directiveType, taskId);
            //保存交互指令到redis
            sendDirective.fill(status, extra);
            redisService.saveDirectiveResult(directiveId, sendDirective);
            logger.info("import success taskId={},directiveId={},code={},extra={}", taskId, directiveId, code, JSON.toJSONString(extra));
            return result.success(true);
        } catch (Exception e) {
            logger.error("import error taskId={},directiveId={},code={},extra={}", taskId, directiveId, code, JSON.toJSONString(extra), e);
            return result.failure();
        }
    }
}
