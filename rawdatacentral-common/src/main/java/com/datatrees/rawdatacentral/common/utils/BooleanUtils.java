package com.datatrees.rawdatacentral.common.utils;

public class BooleanUtils {

    /**
     * 是否正数
     * @param number
     * @return
     */
    public static Boolean isPositiveNumber(Number number) {
        if (null == number) {
            return false;
        }
        return number.doubleValue() > 0;
    }

    /**
     * 是否非正数
     * @param number
     * @return
     */
    public static Boolean isNotPositiveNumber(Number number) {
        return !isPositiveNumber(number);
    }
}
