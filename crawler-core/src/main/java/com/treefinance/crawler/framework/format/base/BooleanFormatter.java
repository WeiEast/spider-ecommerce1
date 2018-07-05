package com.treefinance.crawler.framework.format.base;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.common.exception.FormatException;
import com.treefinance.crawler.framework.format.AbstractFormatter;
import org.apache.commons.lang.BooleanUtils;

/**
 * @author Jerry
 * @since 00:41 2018/6/2
 */
public class BooleanFormatter extends AbstractFormatter<Boolean> {

    @Override
    public Boolean format(String value, String pattern, Request request, Response response) throws FormatException {
        logger.debug("Formatting boolean value: {}, pattern: {}", value);
        return BooleanUtils.toBoolean(value.trim());
    }
}
