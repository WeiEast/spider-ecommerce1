package com.treefinance.crawler.framework.format.base;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.processor.common.exception.FormatException;
import com.treefinance.crawler.framework.format.AbstractFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;

/**
 * @author Jerry
 * @since 00:42 2018/6/2
 */
public class StringFormatter extends AbstractFormatter<String> {

    @Override
    public String format(String value, @Nonnull FormatConfig config) throws FormatException {
        return value;
    }
}
