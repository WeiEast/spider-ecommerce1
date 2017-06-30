package com.datatrees.rawdatacentral.common.utils;

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
}
