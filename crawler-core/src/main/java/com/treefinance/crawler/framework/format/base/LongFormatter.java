package com.treefinance.crawler.framework.format.base;

import javax.annotation.Nonnull;

import com.treefinance.crawler.framework.format.CommonFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;

/**
 * @author Jerry
 * @since 00:40 2018/6/2
 */
public class LongFormatter extends CommonFormatter<Long> {

    @Override
    protected Long toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        String val = value.replaceAll("\\s+", "");
        return Long.valueOf(val);
    }
}
