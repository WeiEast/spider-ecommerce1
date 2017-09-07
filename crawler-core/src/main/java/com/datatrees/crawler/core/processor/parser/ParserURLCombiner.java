/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.parser;

import java.util.ArrayList;
import java.util.List;

import com.datatrees.crawler.core.processor.Constants;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 11:03:02 AM
 */
public class ParserURLCombiner {

    public static final String EMP = "EMP";

    public static String encodeUrl(String url, String... args) {
        StringBuilder sb = new StringBuilder().append(url);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                sb.append(Constants.PARSER_SPLIT).append(args[i]);
            }
        }
        return sb.toString();
    }

    public static String[] decodeParserUrl(String url) {
        List<String> splits = new ArrayList<String>();
        String split = Constants.PARSER_SPLIT;
        int start = 0;
        int next = -1;
        int length = url.length();
        while ((next = url.indexOf(split, start)) > -1) {
            String tmp = url.substring(start, next);
            start = next + split.length();
            splits.add(tmp);
        }
        if (start <= length) {
            splits.add(url.substring(start, length));
        }
        return splits.toArray(new String[]{});
    }
}
