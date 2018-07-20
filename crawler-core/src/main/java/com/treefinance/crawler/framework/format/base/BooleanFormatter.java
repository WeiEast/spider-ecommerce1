package com.treefinance.crawler.framework.format.base;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.processor.common.exception.FormatException;
import com.treefinance.crawler.framework.format.AbstractFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import org.apache.commons.lang.BooleanUtils;

/**
 * @author Jerry
 * @since 00:41 2018/6/2
 */
public class BooleanFormatter extends AbstractFormatter<Boolean> {

    @Override
    public Boolean format(String value, @Nonnull FormatConfig config) throws FormatException {
        logger.debug("Formatting boolean value: {}, pattern: {}", value);
        return BooleanUtils.toBoolean(value.trim());
    }
}
