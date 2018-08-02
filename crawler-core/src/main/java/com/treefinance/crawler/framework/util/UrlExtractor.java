/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.treefinance.crawler.framework.util;

import java.util.Collections;
import java.util.List;

import com.datatrees.common.protocol.Constant;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:52:48 AM
 */
public class UrlExtractor {

    private UrlExtractor() {
    }

    public static List<String> extract(String data) {
        if (StringUtils.isNotEmpty(data)) {
            return RegExp.findAll(data, Constant.URL_REGEX, 1);
        }
        return Collections.emptyList();
    }
}
