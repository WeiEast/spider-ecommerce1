package com.treefinance.crawler.framework.expression.special;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.treefinance.crawler.framework.expression.StandardExpression;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 16:18 2018/5/31
 */
public final class SearchUrlExpParser {

    private static final Logger  LOGGER  = LoggerFactory.getLogger(PageExpParser.class);
    private static final String  REGEX   = "$\\{\\s*page\\s*,\\s*([\\d]+)\\s*,\\s*([\\d]+)\\s*,\\s*([\\d]+)\\s*\\+\\s*}";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private SearchUrlExpParser() {
    }

    public static String eval(String url, int page, boolean notOverloadMax, String keywords, Map<String, Object> placeholderMapping) {
        LOGGER.debug("Eval page expression. input: {}", url);
        if (StringUtils.isEmpty(url)) return url;

        String newUrl;
        Matcher matcher = PATTERN.matcher(url);
        if (matcher.find()) {
            int pageNum = page;
            if (pageNum <= 0) {
                pageNum = 1;
            }

            StringBuffer buffer = new StringBuffer();

            do {
                int val = calculate(matcher, pageNum, notOverloadMax);
                matcher.appendReplacement(buffer, Integer.toString(val));
            } while (matcher.find());

            matcher.appendTail(buffer);

            newUrl = buffer.toString();
            LOGGER.debug("Page expression's eval-result: {}", newUrl);
        } else {
            newUrl = url;
        }

        newUrl = newUrl.replace("${keyword}", StringUtils.defaultString(keywords));

        return StandardExpression.eval(newUrl, placeholderMapping);
    }

    private static int calculate(Matcher matcher, int page, boolean notOverloadMax) {
        int start = Integer.parseInt(matcher.group(1));
        int end = Integer.parseInt(matcher.group(2));
        int offset = Integer.parseInt(matcher.group(3));

        int pos = start + offset * (page - 1);
        if (pos > end && notOverloadMax) {
            pos = end;
        }

        return pos;
    }
}
