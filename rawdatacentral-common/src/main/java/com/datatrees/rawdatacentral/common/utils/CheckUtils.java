package com.datatrees.rawdatacentral.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.datatrees.rawdatacentral.domain.enums.ErrorCode;

/**
 * 检查
 * Created by zhouxinghai on 2017/6/29.
 */
public class CheckUtils {

    public static void checkNotNull(Object param, String errorMsg) {
        if (null == param) {
            throw new RuntimeException(errorMsg);
        }
    }

    public static void checkNotBlank(String param, String errorMsg) {
        if (StringUtils.isBlank(param)) {
            throw new RuntimeException(errorMsg);
        }
    }

    public static void checkNotBlank(String param, ErrorCode errorCode) {
        if (StringUtils.isBlank(param)) {
            throw new RuntimeException(errorCode.getErrorMsg());
        }
    }

    public static void checkNotPositiveNumber(Number number, ErrorCode errorCode) {
        if (null == number || number.longValue() <= 0) {
            throw new RuntimeException(errorCode.getErrorMsg());
        }
    }

}
