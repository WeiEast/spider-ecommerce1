package com.treefinance.crawler.framework.format;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.processor.common.exception.FormatException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @since 16:36 2018/5/14
 */
public abstract class CommonFormatter<R> extends AbstractFormatter<R> {

    @Override
    public final R format(String value, @Nonnull FormatConfig config) throws FormatException {
        logger.debug("Formatting value: {}, config: {}", value, config);
        if (StringUtils.isEmpty(value)) {
            logger.warn("The input value is empty. Skip formatting...");
            return null;
        }

        try {
            return toFormat(value, config);
        } catch (FormatException e) {
            throw e;
        } catch (Exception e) {
            throw new FormatException("Error formatting field value.", e);
        }
    }

    protected abstract R toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception;
}
