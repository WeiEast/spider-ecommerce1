/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.extractor.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.datatrees.common.util.PatternUtils;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 25, 2014 10:52:48 AM
 */
public class TextUrlExtractor {


    public static List<String> extractor(String data, String regex, int index) {
        List<String> result = new ArrayList<String>();
        if (StringUtils.isNotEmpty(data) && StringUtils.isNotEmpty(regex)) {
            result.addAll(PatternUtils.getContents(data, regex, index));
        }
        return result;
    }

    public static List<String> extractor(List<String> data, String regex, int index) {
        StringBuilder sb = new StringBuilder();
        if (data != null && data.size() > 0) {
            for (String split : data) {
                sb.append(split);
            }
        }
        return extractor(sb.toString(), regex, index);
    }


}
