package com.datatrees.rawdatacentral.common.utils;

import com.datatrees.spider.share.domain.ErrorCode;
import org.apache.commons.lang3.StringUtils;

/**
 * 检查
 * Created by zhouxinghai on 2017/6/29.
 */
public class CheckUtils {

    public static void checkNotNull(Object param, String errorMsg) {
        if (null == param) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static void checkNotBlank(String param, String errorMsg) {
        if (StringUtils.isBlank(param)) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    public static void checkNotBlank(String param, ErrorCode errorCode) {
        if (StringUtils.isBlank(param)) {
            throw new IllegalArgumentException(errorCode.getErrorMsg());
        }
    }

    public static void checkNotPositiveNumber(Number number, ErrorCode errorCode) {
        if (null == number || number.longValue() <= 0) {
            throw new IllegalArgumentException(errorCode.getErrorMsg());
        }
    }

    public static void checkNotPositiveNumber(Number number, String errorMsg) {
        if (null == number || number.longValue() <= 0) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

}
