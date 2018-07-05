package com.treefinance.crawler.framework.format.base;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.exception.FormatException;
import com.treefinance.crawler.framework.format.AbstractFormatter;

/**
 * @author Jerry
 * @since 00:42 2018/6/2
 */
public class StringFormatter extends AbstractFormatter<String> {

    @Override
    public String format(String value, String pattern, Request request, Response response) throws FormatException {
        return value;
    }
}
