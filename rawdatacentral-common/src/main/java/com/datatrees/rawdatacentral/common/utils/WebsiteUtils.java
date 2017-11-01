package com.datatrees.rawdatacentral.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * website工具类
 */
public class WebsiteUtils {

    /**
     * 是否是运营商
     * @param websiteName
     * @return
     */
    public static boolean isOperator(String websiteName) {
        return StringUtils.containsAny(websiteName, "10086", "10000", "10010");
    }

}
