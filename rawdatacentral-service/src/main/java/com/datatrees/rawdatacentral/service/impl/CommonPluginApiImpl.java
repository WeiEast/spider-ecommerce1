package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.Map;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BackRedisUtils;
import com.datatrees.rawdatacentral.common.utils.ProcessResultUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.StepEnum;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommonPluginApiImpl implements CommonPluginApi {

    private static final Logger logger = LoggerFactory.getLogger(CommonPluginApiImpl.class);
    @Resource
    private ClassLoaderService   classLoaderService;
    @Resource
    private RedisService         redisService;
    @Resource
    private WebsiteConfigService websiteConfigService;
    @Resource
    private MonitorService       monitorService;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        HttpResult<Object> result = new HttpResult<>();
        try {
            Long taskId = param.getTaskId();
            String websiteName = param.getWebsiteName();
            String username = param.getUsername();
            String formType = param.getFormType();
            if (StringUtils.equals(FormType.LOGIN, formType)) {
                TaskUtils.addStep(param.getTaskId(), StepEnum.REC_INIT_MSG);
                //清理共享信息
                RedisUtils.del(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId));
                RedisUtils.del(RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId));
                RedisUtils.del(RedisKeyPrefixEnum.TASK_PROXY.getRedisKey(taskId));
                RedisUtils.del(RedisKeyPrefixEnum.TASK_PROXY_ENABLE.getRedisKey(taskId));
                try {
                    BackRedisUtils.del(RedisKeyPrefixEnum.TASK_REQUEST.getRedisKey(taskId));
                    BackRedisUtils.del(RedisKeyPrefixEnum.TASK_PAGE_CONTENT.getRedisKey(taskId));
                } catch (Throwable e) {
                    logger.error("delete task info from back redis error taskId={}", taskId, e);
                }
                RedisUtils.del(RedisKeyPrefixEnum.TASK_CONTEXT.getRedisKey(taskId));
                RedisUtils.del(RedisKeyPrefixEnum.TASK_WEBSITE.getRedisKey(taskId));

                //这里电商,邮箱,老运营商
                Website website = websiteConfigService.getWebsiteByWebsiteName(websiteName);
                redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
                //缓存task基本信息
                TaskUtils.initTaskShare(taskId, websiteName);
                if (StringUtils.isNotBlank(username)) {
                    TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, username);
                }
                TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_CODE, website.getGroupCode());
                TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_NAME, website.getGroupName());
                TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TITLE, website.getWebsiteTitle());
                TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TYPE, website.getWebsiteType());

                //设置代理
                ProxyUtils.setProxyEnable(taskId, param.isProxyEnable());
                //记录登陆开始时间
                TaskUtils.addTaskShare(taskId, RedisKeyPrefixEnum.START_TIMESTAMP.getRedisKey(param.getFormType()), System.currentTimeMillis() + "");
                //初始化监控信息
                monitorService.initTask(taskId, websiteName, username);

                if (null != param.getExtral() && !param.getExtral().isEmpty()) {
                    for (Map.Entry<String, Object> entry : param.getExtral().entrySet()) {
                        TaskUtils.addTaskShare(taskId, entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }

                //执行运营商插件初始化操作
                //运营商独立部分第一次初始化后不启动爬虫
                result = classLoaderService.getCommonPluginService(param).init(param);
                //爬虫状态
                if (!result.getStatus()) {
                    TaskUtils.addStep(taskId, StepEnum.INIT_FAIL);
                    monitorService.sendTaskLog(taskId, websiteName, "登录-->初始化-->失败");
                    logger.warn("登录-->初始化-->失败");
                    return result;
                }
                TaskUtils.addStep(taskId, StepEnum.INIT_SUCCESS);
                monitorService.sendTaskLog(taskId, websiteName, "登录-->初始化-->成功");
                logger.info("登录-->初始化-->成功");
                return result.success();
            }
            result = classLoaderService.getCommonPluginService(param).init(param);
            if (!result.getStatus()) {
                monitorService.sendTaskLog(taskId, websiteName, TemplateUtils.format("{}-->初始化-->失败", param.getActionName()));
                logger.warn("{}-->初始化-->失败", param.getActionName());
                return result;
            }
            monitorService.sendTaskLog(taskId, websiteName, TemplateUtils.format("{}-->初始化-->成功", param.getActionName()));
            logger.info("{}-->初始化-->成功", param.getActionName());
            return result;
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).refeshPicCode(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).refeshSmsCode(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).submit(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).validatePicCode(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).defineProcess(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public ProcessResult<Object> queryProcessResult(long processId) {
        return ProcessResultUtils.queryProcessResult(processId);
    }
}
