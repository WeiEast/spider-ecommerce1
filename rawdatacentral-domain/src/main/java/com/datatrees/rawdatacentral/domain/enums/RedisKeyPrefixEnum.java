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
    TASK_RUN_COUNT("task.run.count", 30, TimeUnit.MINUTES, "根据taskId运行次数"),
    TASK_SHARE("task.share", 30, TimeUnit.MINUTES, "根据taskId共享中间属性"),
    TASK_PROXY("task.proxy", 30, TimeUnit.MINUTES, "根据taskId共享代理"),
    TASK_PROXY_ENABLE("task.proxy.enable", 30, TimeUnit.MINUTES, "根据taskId共享代理"),
    TASK_SMS_CODE("task.sms.code", 30, TimeUnit.MINUTES, "根据taskId共享短信验证码"),
    TASK_PIC_CODE("task.pic.code", 30, TimeUnit.MINUTES, "根据taskId共享短信验证码"),
    TASK_RESULT("task.result", 10, TimeUnit.MINUTES, "task爬取结果"),
    TASK_CONTEXT("task.context", 10, TimeUnit.MINUTES, "task context"),
    TASK_REQUEST("task.request", 10, TimeUnit.MINUTES, "根据taskId共享代理"),
    TASK_PAGE_CONTENT("task.page.content", 10, TimeUnit.MINUTES, "请求页面"),
    TASK_LOG("task.log", 15, TimeUnit.MINUTES, "任务日志"),
    TASK_METHOD_USE_TIME("task.method.use.time", 15, TimeUnit.MINUTES, "任务接口耗时"),
    MAX_WEIGHT_OPERATOR("max.weight.operator", 30, TimeUnit.DAYS, "最大权重运营商"),
    ALL_OPERATOR_CONFIG("all.operator.config", 60, TimeUnit.MINUTES, "运营商配置"),
    WEBSITE_LAST_SEND_WARN_TIME("website.last.send.warn.time", 30, TimeUnit.DAYS, "站点上一次发送预警时间"),
    WEBSITE_LAST_FAIL_TIMESTAMP("website.last.fail.timestamp", 30, TimeUnit.DAYS, "站点上一次任务失败时间"),
    WEBSITE_PLUGIN_FILE_NAME("website.plugin.file.name", 60, TimeUnit.MINUTES, "为website制定jar"),
    WEBSITE_OPERATOR("website.operator", 1, TimeUnit.HOURS, "运营商配置"),
    WEBSITE_PROXY("website.proxy", 1, TimeUnit.HOURS, "运营商指定代理"),
    START_TIMESTAMP("start.timestamp", 1, TimeUnit.HOURS, " 开始时间"),
    FINISH_TIMESTAMP("finish.timestamp", 1, TimeUnit.HOURS, " 完成时间"),
    STATUS("status", 15, TimeUnit.MINUTES, "状态"),
    STEP("step", 15, TimeUnit.MINUTES, "阶段"),
    PLUGIN_VERSION("plugin.version", 365, TimeUnit.DAYS, "插件版本"),
    PLUGIN_DATA("plugin.data", 365, TimeUnit.DAYS, "插件版本"),;
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

    public static void main(String[] args) {
        System.out.println(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(1, 2, 3, 4, 5));
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

    public String getRedisKey(Object... postfix) {
        StringBuffer sb = new StringBuffer(prefix);
        for (Object o : postfix) {
            sb.append(separator).append(o.toString());
        }
        return sb.toString();
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
