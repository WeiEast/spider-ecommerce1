/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.cookie.fetcher;

import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.domain.config.properties.cookie.CustomCookie;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 27, 2014 7:44:23 PM
 */
public class CookieFetchFactory {

    private static final Logger log = LoggerFactory.getLogger(CookieFetchFactory.class);

    public static CookieFetchHandler getCookieHandler(SearchProcessorContext wrapper) {
        CookieFetchHandler handler = null;
        try {
            Preconditions.checkNotNull(wrapper);

            CustomCookie cookie = (CustomCookie) wrapper.getCookieConf();

            String config = cookie.getHandleConfig();
            if (StringUtils.isEmpty(config)) {
                return handler;
            }

            if (UrlUtils.isUrl(config)) {
                return new URLCookieFetchHandler(config);
            } else {
                return new PluginCookieFetchHandler(wrapper);
            }

        } catch (Exception e) {
            log.error("invoke getCookieHandler error", e);
        }
        return handler;
    }
}
