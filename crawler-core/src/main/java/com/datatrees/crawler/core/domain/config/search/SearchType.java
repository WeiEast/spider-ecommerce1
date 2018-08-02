/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.search;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 5:12:44 PM
 */
public enum SearchType {
    KEYWORD_SEARCH("keyword_search"),
    DEEP("deep"),
    CATEGORY_SEARCH("category_search");

    private static Map<String, SearchType> SearchTypeMap = new HashMap<String, SearchType>();

    static {
        for (SearchType obj : values()) {
            SearchTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    SearchType(String value) {
        this.value = value;
    }

    public static SearchType getSearchType(String value) {
        return SearchTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}