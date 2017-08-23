/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common.html.urlspliter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2014-12-18 下午7:31:52
 */
public class URLSplitter {

    public static Collection<String> split(String originalURL) {
        StringBuffer tmpOriginalURL = new StringBuffer(StringUtils.trim(originalURL));

        Set<String> urlList = new HashSet<String>();
        urlList.add(StringUtils.trim(originalURL));

        int round = 2; // Records of protocol separator what the times of occurrence
        int index = 0; // Records of each protocol separator subscript position
        loop:
        for (; ; ) {
            outer:
            if ((index = StringUtils.ordinalIndexOf(tmpOriginalURL.toString(), DefaultProtocol.protocolSeparator, round)) != -1) {

                String tempUrl = tmpOriginalURL.substring(0, index);
                String firstUrl = "";
                for (String protocol : DefaultProtocol.INSTANCE.getSupportProtocolList()) {
                    if (tempUrl.endsWith(protocol)) {
                        int end = (index - protocol.length()) < 0 ? 0 : (index - protocol.length());
                        firstUrl = tmpOriginalURL.substring(0, end);
                        if (StringUtils.isNotBlank(firstUrl) && !urlList.contains(firstUrl)) {
                            urlList.add(StringUtils.trim(firstUrl));
                        }
                        tmpOriginalURL = new StringBuffer(tmpOriginalURL.substring(firstUrl.length()));
                        // To avoid special situation,termination outer loop
                        if (urlList.contains(tmpOriginalURL)) break loop;

                        // reset round value
                        round = 2;
                        // Out of the inner loop
                        break outer;
                    }
                }
                round++;
            } else {
                if (!urlList.contains(tmpOriginalURL)) {
                    urlList.add(StringUtils.trim(tmpOriginalURL.toString()));
                }
                // Jump out of a dead circulation
                break loop;
            }
        }
        return urlList;
    }
}
