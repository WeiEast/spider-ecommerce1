package com.datatrees.rawdatacentral.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
public class TemplateUtils {

    public static String format(String template, Object... args) {
        if (StringUtils.isNoneBlank(template) && args.length > 0) {
            for (Object arg : args) {
                template = template.replaceFirst("\\{}", String.valueOf(arg));
            }
            template = template.replaceAll("\\{}", "");

        }
        return template;
    }

}
