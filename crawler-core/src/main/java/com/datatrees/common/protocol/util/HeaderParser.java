/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2014
 */

package com.datatrees.common.protocol.util;

import java.util.*;

import com.datatrees.common.protocol.NameValuePair;
import com.datatrees.common.util.GsonUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * it
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 14, 2014 10:19:37 AM
 */
public class HeaderParser {

    public static final Logger LOG = LoggerFactory.getLogger(HeaderParser.class);

    public static List<NameValuePair> getHeaders(String header) {
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        try {
            if (StringUtils.isNotEmpty(header)) {
                Map<String, String> headerMap = (Map) GsonUtils.fromJson(header, new TypeToken<Map<String, String>>() {}.getType());
                Iterator<String> iterator = headerMap.keySet().iterator();
                int i = 0;
                NameValuePair temp = null;
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = headerMap.get(key);
                    temp = new NameValuePair(key, value);
                    headers.add(temp);
                }
            }

        } catch (Exception e) {
            LOG.error("parser json error! " + header);
        }

        return headers;
    }

    public static Map<String, String> getHeaderMaps(String header) {
        Map<String, String> headers = new HashMap<String, String>();
        try {
            if (StringUtils.isNotEmpty(header)) {
                headers.putAll((Map) GsonUtils.fromJson(header, new TypeToken<Map<String, String>>() {}.getType()));
            }

        } catch (Exception e) {
            LOG.error("parser json error! " + header);
        }

        return headers;
    }
}
