package com.datatrees.rawdatacentral.common.utils;

/**
 * Created by zhouxinghai on 2017/7/21.
 */
public class JsonpUtil {

    /**
     * 去掉function
     * @param jsonp
     * @return
     */
    public static String getJsonString(String jsonp) {
        return jsonp.substring(jsonp.indexOf("(") + 1, jsonp.lastIndexOf(")"));
    }
}
