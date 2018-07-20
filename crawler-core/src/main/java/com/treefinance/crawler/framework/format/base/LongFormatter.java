package com.treefinance.crawler.framework.format.base;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.treefinance.crawler.framework.format.CommonFormatter;

/**
 * @author Jerry
 * @since 00:40 2018/6/2
 */
public class LongFormatter extends CommonFormatter<Long> {

    @Override
    protected Long toFormat(@Nonnull String value, String pattern, Request request, Response response) throws Exception {
        String val = value.replaceAll("\\s+", "");
        return Long.valueOf(val);
    }
}