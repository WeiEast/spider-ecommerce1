package com.datatrees.rawdatacentral.domain.enums;

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
        for (OperationEnum e : OperationEnum.values()) {
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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getName(String code) {
        return map.get(code);
    }

    public String getName(Long code) {
        return map.get(code + "");
    }
}
