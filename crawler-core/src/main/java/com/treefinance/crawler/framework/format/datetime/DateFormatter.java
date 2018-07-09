package com.treefinance.crawler.framework.format.datetime;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.DateUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.format.ConfigurableFormatter;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jerry
 * @since 00:43 2018/6/2
 */
public class DateFormatter extends ConfigurableFormatter<Date> {

    @Override
    protected Date toFormat(@Nonnull String value, String pattern, Request request, Response response) throws Exception {
        Date result = null;
        String input = value.trim();
        String actualPattern = StringUtils.trim(pattern);
        if (StringUtils.isEmpty(actualPattern)) {
            if (RegExp.matches(input, "\\d+")) {
                return new Date(Long.parseLong(input));
            }

            actualPattern = StringUtils.trim(getConf().get("DEFAULT_DATE_PATTERN", Constants.DEFAULT_DATE_PATTERN));
        }

        String separator = getConf().get("DEFAULT_DATE_PATTERN_SEPARATOR", ";");
        String[] patterns = actualPattern.split(separator);

        for (String item : patterns) {
            if (item.isEmpty()) {
                continue;
            }

            DateFormat dateFormat = getDateFormat(request, item);
            result = DateUtils.parseDate(input, dateFormat);
            if (result != null) {
                // handle pattern has no year
                if (!item.toLowerCase().contains("yy")) {
                    result.setYear(new Date().getYear());
                }
                break;
            }
        }

        if (result == null) {
            logger.warn("Parse Date failed! - input: {}, pattern: {}", value, pattern);
        }

        return result;
    }

    private DateFormat getDateFormat(Request request, String pattern) {
        Map<String, DateFormat> formatMap = RequestUtil.getDateFormat(request);

        return formatMap.computeIfAbsent(pattern, p -> {
            SimpleDateFormat format = new SimpleDateFormat(p);
            format.setLenient(true);
            return format;
        });
    }
}
