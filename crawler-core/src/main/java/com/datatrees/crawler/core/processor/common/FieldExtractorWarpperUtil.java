/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月8日 上午1:39:23
 */
public class FieldExtractorWarpperUtil {

    public static Map<String, Object> fieldWrapperMapToField(Map<String, FieldExtractorWarpper> fieldMap) {

        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (MapUtils.isEmpty(fieldMap)) {
            return resultMap;
        }
        Iterator<String> fieldIds = fieldMap.keySet().iterator();
        while (fieldIds.hasNext()) {
            String id = fieldIds.next();
            FieldExtractorWarpper fWarpper = fieldMap.get(id);
            Object result = fWarpper.getResult();
            if (result != null && !(result instanceof List)) {
                resultMap.put(id, result);
            }
        }
        return resultMap;
    }
}
