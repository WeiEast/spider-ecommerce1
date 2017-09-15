package com.datatrees.rawdatacentral.domain.enums;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
public enum RedisKeyPrefixEnum {

    WEBSITENAME_TRANSFORM_MAP("websitename.transform.map", 60, TimeUnit.MINUTES, "websitename中文转英文名称"),
    WEBSITE_CONF_WEBSITENAME("website.conf.websitename", 60, TimeUnit.MINUTES, "根据websitename查找website.conf"),
    LOCK("lock", 5, TimeUnit.SECONDS, "共享锁"),
    TASK_COOKIE("task.cookie", 30, TimeUnit.MINUTES, "根据taskId共享cookie"),
    TASK_WEBSITE("task.website", 30, TimeUnit.MINUTES, "根据taskId保存website"),
    TASK_FIRST_VISIT_WEBSITENAME("task.first.visit.websitename", 30, TimeUnit.MINUTES, "task第一次访问使用的websiteName"),
    TASK_RUN_STAGE("task.run.stage", 30, TimeUnit.MINUTES, "task运营阶段"),
    TASK_SHARE("task.share", 30, TimeUnit.MINUTES, "根据taskId共享中间属性"),
    TASK_PROXY("task.proxy", 30, TimeUnit.MINUTES, "根据taskId共享代理"),
    TASK_PROXY_ENABLE("task.proxy.enable", 30, TimeUnit.MINUTES, "根据taskId共享代理"),
    TASK_SMS_CODE("task.sms.code", 30, TimeUnit.MINUTES, "根据taskId共享短信验证码"),
    TASK_PIC_CODE("task.pic.code", 30, TimeUnit.MINUTES, "根据taskId共享短信验证码"),
    TASK_RESULT("task.result", 60, TimeUnit.MINUTES, "task爬取结果"),
    TASK_REQUEST("task.request", 30, TimeUnit.MINUTES, "根据taskId共享代理"),
    PLUGIN_CLASS_DATA("plugin.class.data", 24, TimeUnit.HOURS, "根据taskId共享class"),
    PLUGIN_FILE("plugin.file", 365, TimeUnit.DAYS, "插件jar存储"),
    PLUGIN_FILE_MD5("plugin.file.md5", 365, TimeUnit.DAYS, "插件md5"),
    SEND_LOGIN_MSG_STAGE("send.login.msg.stage", 24, TimeUnit.HOURS, "发送登录成功消息阶段"),
    MAX_WEIGHT_OPERATOR("max.weight.operator", 365, TimeUnit.DAYS, "最大权重运营商"),
    ALL_OPERATOR_CONFIG("all.operator.config.new", 60, TimeUnit.MINUTES, "运营商配置"),
    WEBSITE_PLUGIN_FILE_NAME("website.plugin.file.name", 60, TimeUnit.MINUTES, "为website制定jar"),
    WEBSITE_OPERATOR("website.operator", 1, TimeUnit.HOURS, "运营商配置"),
    WEBSITE_OPERATOR_RENAME("website.operator.rename", 365, TimeUnit.DAYS, "运营商websiteName别名,兼容方案初期用");
    /**
     * 备注
     */
    private final String remark;
    /**
     * 分隔符
     */
    private final String separator = ".";
    /**
     * 前缀
     */
    private String   prefix;
    /**
     * 超时时间
     */
    private int      timeout;
    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

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

    public String parsePostfix(String key) {
        return key.substring(prefix.length() + separator.length());
    }

    public int toSeconds() {
        long l = timeUnit.toSeconds(timeout);
        return l == 0 ? 1 : (int) l;
    }
}
