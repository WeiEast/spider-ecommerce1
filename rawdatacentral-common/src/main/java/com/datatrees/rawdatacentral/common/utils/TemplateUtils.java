package com.datatrees.rawdatacentral.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
public class TemplateUtils {

    private static final Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

    public static String format(String template, Object... args) {
        if (StringUtils.isNoneBlank(template) && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (null == arg) {
                    logger.error("arg is null template={},index={}", template, i);
                    throw new RuntimeException("format error arg is null");
                }
                template = template.replaceFirst("\\{}", String.valueOf(arg));
            }
//            template = template.replaceAll("\\{}", "");
        }
        return template;
    }

}
