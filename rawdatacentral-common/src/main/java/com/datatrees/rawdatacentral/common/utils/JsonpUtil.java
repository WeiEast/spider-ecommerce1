package com.datatrees.rawdatacentral.common.utils;

import com.alibaba.fastjson.JSON;

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
        String json = jsonp.trim();
        if ((json.startsWith("{") && json.endsWith("}")) || (json.startsWith("[") && json.endsWith("]"))) {
            return json;
        }
        //有的结尾带";"
        if (null != json && json.contains("(") && json.trim().contains(")")) {
            json = json.substring(json.indexOf("(") + 1, json.lastIndexOf(")"));
            return json;
        }
        return json;
    }

}
