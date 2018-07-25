package com.datatrees.spider.share.common.utils;

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
                    logger.warn("arg is null template={},index={}", template, i);
                }
                template = template.replaceFirst("\\{}", null != arg ? arg.toString() : "");
            }
        }
        return template;
    }

}
