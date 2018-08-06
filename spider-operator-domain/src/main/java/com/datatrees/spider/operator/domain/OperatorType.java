package com.datatrees.spider.operator.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 运营商类型
 * Created by zhouxinghai on 2017/8/31
 */
public enum OperatorType {

    CMCC("10086", "移动"),
    TELECOM("10000", "电信"),
    UNICOM("10010", "联通"),;
    private static final Map<String, String> map = new HashMap<>();

    static {
        for (OperatorType e : OperatorType.values()) {
            map.put(e.getCode(), e.getName());
        }
    }

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

    public static String getName(String code) {
        return map.get(code);
    }

    public static String getName(Integer code) {
        return map.get(code + "");
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
