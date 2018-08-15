/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import static com.treefinance.crawler.framework.context.UrlProtocolRegistry.PROTOCOL_SEPARATOR;
import static com.treefinance.crawler.framework.context.UrlProtocolRegistry.getProtocols;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2014-12-18 下午7:31:52
 */
public final class URLSplitter {

    private URLSplitter() {
    }

    public static Collection<String> split(String multiUrls) {
        String urls = StringUtils.trim(multiUrls);
        if(StringUtils.isEmpty(urls)){
            return Collections.emptyList();
        }

        Set<String> list = new HashSet<>();

        int end = urls.length();
        int start = urls.lastIndexOf(PROTOCOL_SEPARATOR);
        String body;
        while (start >= PROTOCOL_SEPARATOR.length()) {
            int pos = urls.lastIndexOf(PROTOCOL_SEPARATOR, start - 1);
            if (pos == -1) {
                pos = 0;
            }

            body = urls.substring(start, end);
            String head = urls.substring(pos, start);

            for (String protocol : getProtocols()) {
                if (head.endsWith(protocol)) {
                    list.add(protocol + StringUtils.stripEnd(body, null));
                    end = start - protocol.length();
                    break;
                }
            }
            start = pos;
        }

        if (end > 0) {
            list.add(urls.substring(0, end));
        }

        if (list.size() > 1) {
            list.add(urls);
        }

        return list;
    }
}
