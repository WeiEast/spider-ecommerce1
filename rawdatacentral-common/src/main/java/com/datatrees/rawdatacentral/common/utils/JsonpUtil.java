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
        if (jsonp.trim().startsWith("{") || jsonp.trim().startsWith("[")) {
            return jsonp;
        }
        return jsonp.substring(jsonp.indexOf("(") + 1, jsonp.lastIndexOf(")"));
    }

    public static void main(String[] args) {
        String jsonp = "{\"retCode\":\"400000\",\"retMsg\":\"parameter illegal!\"}";
        System.out.println(getJsonString(jsonp));
    }
}
