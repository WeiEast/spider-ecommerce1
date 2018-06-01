package com.treefinance.crawler.framework.format;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.exception.FormatException;

/**
 * @author Jerry
 * @since 15:49 2018/5/14
 */
public interface Formatter<R> {

    boolean supportResultType(Object value);

    R format(String value, String pattern, Request request, Response response) throws FormatException;
}
