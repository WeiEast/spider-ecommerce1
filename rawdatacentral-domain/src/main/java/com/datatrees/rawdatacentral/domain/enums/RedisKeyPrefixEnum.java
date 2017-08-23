package com.datatrees.rawdatacentral.domain.enums;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
public enum RedisKeyPrefixEnum {

    WEBSITENAME_TRANSFORM_MAP("websitename.transform.map", 60, TimeUnit.MINUTES, "websitename中文转英文名称"),
    LOCK("lock", 1, TimeUnit.MINUTES, "共享锁"),
    TASK_COOKIE("task.cookie", 30, TimeUnit.MINUTES, " 根据taskId共享cookie"),
    TASK_SHARE("task.share", 30, TimeUnit.MINUTES, " 根据taskId共享中间属性"),
    TASK_PROXY("task.proxy", 30, TimeUnit.MINUTES, " 根据taskId共享代理"),
    TASK_REQUEST("task.request", 30, TimeUnit.MINUTES, "根据taskId共享代理"),
    WEBSITE_CONF_WEBSITENAME("website.conf.websitename", 60, TimeUnit.MINUTES, "根据websitename查找website.conf"),
    ALL_OPERATOR_CONFIG("all.operator.config", 60, TimeUnit.MINUTES, "运营商配置"),
    PLUGIN_CLASS("plugin.class", 24, TimeUnit.HOURS, "根据taskId共享cookie"),
    PLUGIN_FILE("plugin.file", 365, TimeUnit.DAYS, "插件jar存储"),
    PLUGIN_FILE_MD5("plugin.file.md5", 365, TimeUnit.DAYS, "插件md5"),
    PLUGIN_FILE_WEBSITE("plugin.file.website", 60, TimeUnit.MINUTES, "开发环境,将website的plugin jar临时映射到固定的jar"),
    SEND_LOGIN_MSG_STAGE("send.login.msg.stage", 24, TimeUnit.HOURS, "发送登录成功消息阶段"),
    SEND_SMS_INTERVAL("send.sms.interval", 60, TimeUnit.SECONDS, "发送短信间隔时间");
    /**
     * 备注
     */
    private final String   remark;
    /**
     * 分隔符
     */
    private final String separator = ".";
    /**
     * 前缀
     */
    private       String   prefix;
    /**
     * 超时时间
     */
    private       int      timeout;
    /**
     * 时间单位
     */
    private       TimeUnit timeUnit;

    RedisKeyPrefixEnum(String prefix, int timeout, TimeUnit timeUnit, String remark) {
        this.prefix = prefix;
        this.remark = remark;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getRedisKey() {
        return prefix;
    }

    public String getRedisKey(Object postfix) {
        return prefix + separator + postfix.toString();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRemark() {
        return remark;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
