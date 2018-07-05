package com.treefinance.crawler.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.datatrees.common.protocol.util.CharsetUtil;
import com.treefinance.crawler.exception.UnexpectedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 17:25 2018/5/25
 */
public final class UrlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlUtils.class);

    private UrlUtils() {
    }

    public static String urlEncode(String value, String charset, boolean strict) {
        if (StringUtils.isEmpty(value)) return value;

        String encoding = StringUtils.trim(charset);

        if (StringUtils.isEmpty(encoding)) {
            LOGGER.warn("The given charset is empty. use default charset: {}", charset);
            encoding = CharsetUtil.UTF_8_NAME;
        }

        LOGGER.info("Url encoding with charset: {}, value: {}", encoding, value);

        try {
            return URLEncoder.encode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            if (strict) {
                throw new UnexpectedException("Error url encoding with charset: " + encoding + ", value: " + value, e);
            }

            LOGGER.warn("Error url encoding with charset: {}, value: {}", encoding, value);

            if (!CharsetUtil.UTF_8_NAME.equals(encoding)) {
                LOGGER.info("Url encoding with default charset: {}, value: {}", CharsetUtil.UTF_8_NAME, value);
                try {
                    return URLEncoder.encode(value, CharsetUtil.UTF_8_NAME);
                } catch (UnsupportedEncodingException e1) {
                    LOGGER.warn("Error url encoding with default charset: {}, value: {}", CharsetUtil.UTF_8_NAME, value);
                }
            }
        }

        return value;
    }
}
