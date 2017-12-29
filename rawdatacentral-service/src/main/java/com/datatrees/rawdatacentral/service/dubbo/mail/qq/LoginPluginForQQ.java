package com.datatrees.rawdatacentral.service.dubbo.mail.qq;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.domain.constant.DirectiveType;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPluginForQQ implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LoginPluginForQQ.class);
    private Long   taskId;
    private String websiteName;
    private MonitorService monitorService = BeanFactoryUtils.getBean(MonitorService.class);
    private RedisService   redisService   = BeanFactoryUtils.getBean(RedisService.class);

    public LoginPluginForQQ(Long taskId, String websiteName) {
        this.taskId = taskId;
        this.websiteName = websiteName;
    }

    @Override
    public void run() {
        try {
            logger.info("start run Login plugin!taskId={},websiteName={}", taskId, websiteName);
            monitorService.sendTaskLog(taskId, "模拟登录-->启动-->成功");

            final String groupKey = DirectiveResult.getGroupKey(DirectiveType.PLUGIN_LOGIN, taskId);
            long maxInterval = TimeUnit.MINUTES.toMillis(15) + System.currentTimeMillis();

            while (System.currentTimeMillis() < maxInterval) {
                DirectiveResult<Map<String, Object>> directive = redisService.getNextDirectiveResult(groupKey, 500, TimeUnit.MILLISECONDS);
                if (null == directive) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    continue;
                }
                String status = directive.getStatus();
                String directiveId = directive.getDirectiveId();
                Map<String, Object> extra = directive.getData();
            }

        } catch (Throwable e) {
            logger.error("login for qq error,taskId={},websiteName={}", taskId, websiteName, e);
        }
    }

}
