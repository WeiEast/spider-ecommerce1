package com.datatrees.rawdatacentral.domain.enums;

/**
 * 运营商类型
 * Created by zhouxinghai on 2017/8/31
 */
public enum OperatorType {

    CMCC("10086", "移动"),
    TELECOM("10000", "电信"),
    UNICOM("10010", "联通"),;
    /**
     * 代码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;

    OperatorType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
