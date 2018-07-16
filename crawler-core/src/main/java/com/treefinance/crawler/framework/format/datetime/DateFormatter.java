package com.treefinance.crawler.framework.format.datetime;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.common.exception.FormatException;
import com.treefinance.crawler.framework.format.ConfigurableFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Jerry
 * @since 00:43 2018/6/2
 */
public class DateFormatter extends ConfigurableFormatter<Date> {

    private static final Pattern HOUR_PATTERN = Pattern.compile("(\\b|[^0-9a-zA-Z])hh(\\b|[^0-9a-zA-Z])");
    private static final int     BASE_YEAR    = 1970;

    @Override
    protected Date toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        String input = value.trim();
        String actualPattern = config.trimmedPattern();
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
        DateTime dateTime;
        for (String item : patterns) {
            if (item.isEmpty()) {
                continue;
            }

            DateTimeFormatter dateFormat = config.getDateFormatMap().computeIfAbsent(item, p -> DateTimeFormat.forPattern(p).withDefaultYear(BASE_YEAR));
            try {
                dateTime = dateFormat.parseDateTime(input);
            } catch (Exception e) {
                logger.warn("Error parsing datetime with pattern: {}, input: {}", item, input);
                dateTime = adaptPatternFormat(item, input, config);
            }
            if (dateTime != null) {
                return adaptYear(dateTime, item).toDate();
            }
        }

        throw new FormatException("There was no matched date pattern to parse datetime. input: " + value + ", patterns: " + actualPattern);
    }

    private static DateTime adaptYear(DateTime dateTime, String pattern) {
        if (pattern.toLowerCase().contains("yy") || dateTime.getYear() != BASE_YEAR) {
            return dateTime;
        }

        DateTime now = DateTime.now();
        if (!dateTime.isAfter(now.withYear(BASE_YEAR))) {
            if (!pattern.contains("MM") && dateTime.getMonthOfYear() == 1 && dateTime.getDayOfMonth() == 1) {
                return dateTime.withDate(now.toLocalDate());
            } else {
                return dateTime.withYear(now.getYear());
            }
        } else {
            return dateTime.withYear(now.minusYears(1).getYear());
        }
    }

    private DateTime adaptPatternFormat(String item, String input, @Nonnull FormatConfig config) {
        Matcher matcher = HOUR_PATTERN.matcher(item);
        if (matcher.find()) {
            StringBuffer buffer = new StringBuffer();
            do {
                matcher.appendReplacement(buffer, matcher.group().toUpperCase());
            } while (matcher.find());
            matcher.appendTail(buffer);
            String adaptPattern = buffer.toString();

            logger.info("Find adapted possible pattern: {}", adaptPattern);
            DateTimeFormatter dateFormat = config.getDateFormatMap().computeIfAbsent(adaptPattern, DateTimeFormat::forPattern);
            try {
                return dateFormat.parseDateTime(input);
            } catch (Exception e) {
                logger.warn("Error parsing datetime with adapted possible pattern: {}, input: {}", adaptPattern, input);
            }
        }

        return null;
    }
}
