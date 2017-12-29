package com.datatrees.rawdatacentral.service.dubbo.mail.qq;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.api.mail.qq.MailServiceApiForQQ;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.IpUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.DirectiveRedisCode;
import com.datatrees.rawdatacentral.domain.constant.DirectiveType;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.StepEnum;
import com.datatrees.rawdatacentral.domain.enums.TaskStatusEnum;
import com.datatrees.rawdatacentral.domain.mail.MailParam;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.WebsiteConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class MailServiceApiForQQImpl implements MailServiceApiForQQ, InitializingBean {

    private static final Logger logger               = LoggerFactory.getLogger(MailServiceApiForQQImpl.class);
    private static final String DEFAULT_WEBSITE_NAME = GroupEnum.MAIL_QQ_H5.getWebsiteName();
    private ThreadPoolExecutor   initExecutor;
    @Resource
    private RedisService         redisService;
    @Resource
    private WebsiteConfigService websiteConfigService;
    @Resource
    private MonitorService       monitorService;

    @Override
    public HttpResult<Map<String, String>> login(MailParam param) {
        Long taskId = param.getTaskId();
        String username = param.getUsername();
        String password = param.getPassword();
        initTask(taskId, username);
        TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, username);

        DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN, taskId);
        String directiveId = redisService.createDirectiveId();
        sendDirective.setDirectiveId(directiveId);
        Map<String, String> directiveData = new HashMap<>();
        directiveData.put(AttributeKey.USERNAME, username);
        directiveData.put(AttributeKey.PASSWORD, password);
        sendDirective.fill(DirectiveRedisCode.START_LOGIN, directiveData);

        Map<String, String> retuanData = new HashMap<>();
        retuanData.put(AttributeKey.DIRECTIVE_ID, directiveId);
        retuanData.put(AttributeKey.STATUS, TaskStatusEnum.LOGIN_PROCESSING.getCode());
        HttpResult<Map<String, String>> result = new HttpResult<>();
        result.success(retuanData);
        RedisUtils.set(RedisKeyPrefixEnum.LOGIN_RESULT.getRedisKey(directiveId), JSON.toJSONString(result),
                RedisKeyPrefixEnum.LOGIN_RESULT.toSeconds());
        redisService.saveDirectiveResult(sendDirective);
        return result;
    }

    @Override
    public HttpResult<Map<String, String>> queryLoginStatus(MailParam param) {
        String directiveId = param.getDirectiveId();
        String value = RedisUtils.get(RedisKeyPrefixEnum.LOGIN_RESULT.getRedisKey(directiveId));
        return JSON.parseObject(value, new TypeReference<HttpResult<Map<String, String>>>() {});
    }

    private void initTask(Long taskId, String username) {
        String initKey = "login.thread." + taskId;
        Boolean initStatus = RedisUtils.exists(initKey);
        if (initStatus) {
            return;
        }
        String websiteName = DEFAULT_WEBSITE_NAME;
        TaskUtils.addStep(taskId, StepEnum.INIT);
        //这里电商,邮箱,老运营商
        Website website = websiteConfigService.getWebsiteByWebsiteName(websiteName);
        redisService.cache(RedisKeyPrefixEnum.TASK_WEBSITE, taskId, website);
        //缓存task基本信息
        TaskUtils.initTaskShare(taskId, websiteName);
        TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, username);
        TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_CODE, website.getGroupCode());
        TaskUtils.addTaskShare(taskId, AttributeKey.GROUP_NAME, website.getGroupName());
        TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TITLE, website.getWebsiteTitle());
        TaskUtils.addTaskShare(taskId, AttributeKey.WEBSITE_TYPE, website.getWebsiteType());

        //初始化监控信息
        monitorService.initTask(taskId, websiteName, username);
        TaskUtils.addStep(taskId, StepEnum.INIT_SUCCESS);

        monitorService.sendTaskLog(taskId, websiteName, "登录-->初始化-->成功");

        LoginPluginForQQ plugin = new LoginPluginForQQ(taskId, DEFAULT_WEBSITE_NAME);
        initExecutor.submit(plugin);

        RedisUtils.setnx(initKey, IpUtils.getLocalHostName(), (int) TimeUnit.HOURS.toSeconds(1));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        int corePoolSize = PropertiesConfiguration.getInstance().getInt("mail.login.thread.min", 10);
        int maximumPoolSize = PropertiesConfiguration.getInstance().getInt("mail.login.thread.max", 100);
        initExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(300),
                new ThreadFactory() {
                    private AtomicInteger count = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        String threadName = "mail_login_thread_" + count.addAndGet(1);
                        t.setName(threadName);
                        logger.info("create mail login thread :{}", threadName);
                        return t;
                    }
                });
    }
}

