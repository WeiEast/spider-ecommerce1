/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.filter;

import java.util.List;
import java.util.regex.Pattern;

import com.datatrees.crawler.core.domain.config.filter.FilterType;
import com.datatrees.crawler.core.domain.config.filter.UrlFilter;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 6:56:23 PM
 */
public class URLRegexFilter extends RegexURLFilterBase {

    public URLRegexFilter(List<UrlFilter> filters) {
        super(filters);
    }

    @Override
    public RegexRule createRule(UrlFilter urlFilter) {
        boolean sign = (urlFilter.getType() == FilterType.WHITE_LIST);
        if (StringUtils.isNotBlank(urlFilter.getFilter())) {
            return new Rule(sign, urlFilter.getFilter());
        }
        return null;
    }

    private class Rule extends RegexRule {

        private Pattern pattern;

        Rule(boolean sign, String regex) {
            super(sign, regex);
            pattern = RegExp.compile(regex);
        }

        protected boolean match(String url) {
            return pattern.matcher(url).find();
        }
    }

}
