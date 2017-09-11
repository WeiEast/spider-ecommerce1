package com.datatrees.rawdatacentral.domain.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuminlang on 15/7/29.
 */
public enum ExtractCode {

    EXTRACT_SUCCESS(0, "Extract success"),
    EXTRACT_CONF_FAIL(2, "No conf or conf parse fail"),
    ERROR_INPUT(4, "Extract error input"),
    EXTRACT_FAIL(6, "Extract fail code"),
    EXTRACT_STORE_FAIL(8, "Extract store failed");
    private static Map<Integer, ExtractCode> extractCodeMap = new HashMap<Integer, ExtractCode>();

    static {
        for (ExtractCode obj : values()) {
            extractCodeMap.put(obj.getCode(), obj);
        }
    }

    int    code;
    String desc;

    ExtractCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ExtractCode getExtractCode(Integer value) {
        return extractCodeMap.get(value);
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }

}
