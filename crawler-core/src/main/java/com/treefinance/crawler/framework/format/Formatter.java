package com.treefinance.crawler.framework.format;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.exception.FormatException;

/**
 * @author Jerry
 * @since 15:49 2018/5/14
 */
public interface Formatter<R> {

    boolean supportResultType(Object value);

    default  R format(String value, String pattern, @Nonnull Request request, @Nonnull Response response) throws FormatException {
        return format(value, new FormatConfig(request, response, pattern));
    }

    R format(String value, @Nonnull FormatConfig config) throws FormatException;
}
