package com.treefinance.crawler.framework.format.base;

import javax.annotation.Nonnull;

import com.treefinance.crawler.framework.format.CommonFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import org.apache.commons.lang.BooleanUtils;

/**
 * @author Jerry
 * @since 00:41 2018/6/2
 */
public class BooleanFormatter extends CommonFormatter<Boolean> {

    @Override
    protected Boolean toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        logger.debug("Formatting boolean value: {}, pattern: {}", value);
        return BooleanUtils.toBoolean(value.trim());
    }
}
