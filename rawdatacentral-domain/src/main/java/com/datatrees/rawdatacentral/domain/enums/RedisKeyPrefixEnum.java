package com.datatrees.rawdatacentral.domain.enums;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
public enum RedisKeyPrefixEnum {

    WEBSITENAME_TRANSFORM_MAP("websitename_transform_map", 60, "websitename中文转英文名称"),

    TASK_COOKIE("task_cookie",30," 根据taskId共享cookie"),
    PLUGIN_CLASS("plugin_class",60*24*365,"根据taskId共享cookie"),

    //website相关
    WEBSITE_CONF_WEBSITENAME("website_conf_websitename", 60 , "根据websitename查找website_conf"),
    ALL_OPERATOR_CONFIG("all_operator_config", 60 , "运营商配置"),

    PLUGIN_FILE("plugin_file",60*24*365,"插件jar存储"),
    PLUGIN_FILE_MD5("plugin_file_md5",60*24*365,"插件md5"),
    ;

    /**
     * 前缀
     */
    private  String prefix;

    /**
     * 备注
     */
    private final String remark;

    /**
     * 超时时间(单位:分),默认10分钟
     */
    private  int   timeout = 10;

    /**
     * 分隔符
     */
    private final String separator = "_";

    RedisKeyPrefixEnum(String prefix, int timeout, String remark) {
        this.prefix = prefix;
        this.remark = remark;
        this.timeout = timeout;
    }

    RedisKeyPrefixEnum(String remark) {
        this.remark = remark;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getRedisKey() {
        return prefix;
    }

    public String getRedisKey(String postfix) {
        return prefix + separator + postfix;
    }
}
