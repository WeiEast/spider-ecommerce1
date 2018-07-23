/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.search;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import com.treefinance.crawler.framework.expression.special.PageExpParser;
import com.treefinance.crawler.framework.expression.special.SearchUrlExpParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 15, 2014 2:57:41 PM
 */
public class SearchTemplateCombine {

    private static final Logger log = LoggerFactory.getLogger(SearchTemplateCombine.class);

    /**
     * encode keyword in URL format
     * @param keyword
     * @param urlCharset
     * @return the encoded keyword
     */
    public static String encodeKeyword(String keyword, String urlCharset) {
        String encodedKeyword = keyword;
        try {
            // encodedKeyword = encodedKeyword.replace(':', ' ');
            // encodedKeyword = encodedKeyword.replace('_', ' ');
            // encodedKeyword = encodedKeyword.replace('/', ' ');
            // encodedKeyword = encodedKeyword.replace('\'', ' ');
            encodedKeyword = encodedKeyword.replaceAll("[ ]+", " ");
            log.debug("new encoded keyword after replacement: {}", encodedKeyword);
            if (StringUtils.isNotBlank(urlCharset) && Charset.isSupported(urlCharset)) {
                encodedKeyword = URLEncoder.encode(encodedKeyword, urlCharset);
            } else {
                log.warn("Charset unsupported, use UTF-8 as default, url charset: {}", urlCharset);
                encodedKeyword = URLEncoder.encode(encodedKeyword, "UTF-8");
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return encodedKeyword;
    }

    /**
     * construct search url from template with keyword, charset and page num.
     * @param searchURLTemplate
     * @param keyword
     * @param urlCharset
     * @param pageNum
     * @return the search url
     */
    public static String constructSearchURL(String searchURLTemplate, String keyword, String urlCharset, int pageNum, boolean notOverloadMax,
            Map<String, Object> fieldMap) {
        try {
            String encodedKeyword = null;
            if (StringUtils.isNotEmpty(keyword)) {
                encodedKeyword = encodeKeyword(keyword, urlCharset);
                log.debug("original keyword: {},urlCharset: {}, encoded keyword: {}", keyword, urlCharset, encodedKeyword);
            }

            String searchURL = SearchUrlExpParser.eval(searchURLTemplate, pageNum, notOverloadMax, encodedKeyword, fieldMap);

            log.debug("final search URL: {}", searchURL);

            return searchURL;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    /**
     * custom Template search url from template with page num.
     * @param customURLTemplate
     * @param pageNum
     */
    public static String customTemplate(String customURLTemplate, int pageNum) {
        try {
            String result = PageExpParser.eval(customURLTemplate, pageNum);

            log.info("Converted custom url : {}", result);
            return result;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return customURLTemplate;
    }

}
