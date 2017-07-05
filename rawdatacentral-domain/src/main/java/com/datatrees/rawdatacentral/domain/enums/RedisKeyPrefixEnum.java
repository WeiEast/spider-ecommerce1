package com.datatrees.rawdatacentral.domain.enums;

/**
 * Created by zhouxinghai on 2017/7/5.
 */
public enum RedisKeyPrefixEnum {

    WEBSITENAME_TRANSFORM_MAP("websitename_transform_map", 60, "websitename中文转英文名称"),
    WEBSITE_CONF_WEBSITENAME("website_conf_websitename", 60 , "根据websitename查找website_conf"),

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
