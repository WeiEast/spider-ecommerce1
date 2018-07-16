package com.treefinance.crawler.framework.format.datetime;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Jerry
 * @since 21:19 2018/7/16
 */
public class DateTimeFormats {

    public static final int                            BASE_YEAR    = 1970;
    private              Map<String, DateTimeFormatter> formatterMap = new HashMap<>();

    public DateTimeFormatter getFormatter(String pattern) {
        return formatterMap.computeIfAbsent(pattern, p -> DateTimeFormat.forPattern(p).withDefaultYear(BASE_YEAR));
    }

}
