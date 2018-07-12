package com.treefinance.crawler.framework.format.datetime;

import javax.annotation.Nonnull;
import java.util.Date;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.exception.FormatException;
import com.treefinance.crawler.framework.format.ConfigurableFormatter;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Jerry
 * @since 00:43 2018/6/2
 */
public class DateFormatter extends ConfigurableFormatter<Date> {

    @Override
    protected Date toFormat(@Nonnull String value, String pattern, Request request, Response response) throws Exception {
        String input = value.trim();
        String actualPattern = StringUtils.trim(pattern);
        if (StringUtils.isEmpty(actualPattern)) {
            if (RegExp.matches(input, "\\d+")) {
                return new Date(Long.parseLong(input));
            }

            actualPattern = StringUtils.trim(getConf().get("DEFAULT_DATE_PATTERN", Constants.DEFAULT_DATE_PATTERN));
        }

        if (StringUtils.isEmpty(actualPattern)) {
            throw new FormatException("Empty date pattern used to parse datetime.");
        }

        String separator = getConf().get("DEFAULT_DATE_PATTERN_SEPARATOR", ";");
        String[] patterns = actualPattern.split(separator);
        Date result = null;
        for (String item : patterns) {
            if (item.isEmpty()) {
                continue;
            }

            DateTimeFormatter dateFormat = RequestUtil.getDateFormat(request).computeIfAbsent(item, DateTimeFormat::forPattern);
            try {
                result = dateFormat.parseDateTime(input).toDate();
            } catch (Exception e) {
                logger.warn("Error parsing datetime with pattern: {}, input: {}", item, input);
            }
            if (result != null) {
                // handle pattern has no year
                if (!item.toLowerCase().contains("yy")) {
                    result.setYear(new Date().getYear());
                }
                break;
            }
        }

        if (result == null) {
            throw new FormatException("There was no matched date pattern to parse datetime. input: " + value + ", patterns: " + actualPattern);
        }

        return result;
    }
}
