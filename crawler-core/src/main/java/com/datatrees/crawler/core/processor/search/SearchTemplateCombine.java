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
import java.util.List;
import java.util.Map;

import com.datatrees.crawler.core.processor.common.CalculateUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.MapUtils;
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
            log.debug("new encoded keyword after replacement: " + encodedKeyword);
            if (StringUtils.isNotBlank(urlCharset) && Charset.isSupported(urlCharset)) {
                encodedKeyword = URLEncoder.encode(encodedKeyword, urlCharset);
            } else {
                log.warn("Charset unsupported, use UTF-8 as default, url charset: " + urlCharset);
                encodedKeyword = URLEncoder.encode(encodedKeyword, "UTF-8");
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return encodedKeyword;
    }

    public static String constructSearchEngineURL(String searchURLTemplate, String keyword, String urlCharset, int pageNum, boolean notOverloadMax, String domain, Map<String, Object> fieldMap) {
        String encodedDomain = null;
        if (StringUtils.isNotEmpty(keyword)) {
            encodedDomain = encodeKeyword(domain, urlCharset);
            log.debug("original domain: " + domain + ", encoded domain: " + domain);
        }
        try {
            String searchURL = SearchTemplateCombine.constructSearchURL(searchURLTemplate, keyword, urlCharset, pageNum, notOverloadMax, fieldMap);
            if (StringUtils.isNotEmpty(encodedDomain)) {
                searchURL = searchURL.replace("${domain}", encodedDomain);
            }
            log.debug("final SearchEngine URL: " + searchURL);
            return searchURL;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    /**
     * construct search url from template with keyword, charset and page num.
     * @param searchURLTemplate
     * @param keyword
     * @param urlCharset
     * @param pageNum
     * @return the search url
     */
    public static String constructSearchURL(String searchURLTemplate, String keyword, String urlCharset, int pageNum, boolean notOverloadMax, Map<String, Object> fieldMap) {
        try {
            String encodedKeyword = null;
            if (StringUtils.isNotEmpty(keyword)) {
                encodedKeyword = encodeKeyword(keyword, urlCharset);
                log.debug("original keyword: " + keyword + ",urlCharset: " + urlCharset + ", encoded keyword: " + encodedKeyword);
            }
            List<String> pagingParams = RegExp.findAll(searchURLTemplate, "(\\$\\{page,[0-9,]+\\+\\})", 1);
            int paramValue = 0;
            int beginOffset = 0;
            int maxOffset = 0;
            int offset = 0;
            String searchURL = searchURLTemplate;
            for (String pstr : pagingParams) {
                log.debug("pstr: " + pstr);
                int i = 0;
                if (!StringUtils.isBlank(pstr)) {
                    String pagingNums = RegExp.group(pstr, ",(.*)\\+", 1);
                    for (String p : pagingNums.split(",")) {
                        log.debug("p:" + p);
                        if (i == 0) {
                            paramValue = Integer.parseInt(p.trim());
                            beginOffset = paramValue;
                        } else if (i == 1) {
                            paramValue = Integer.parseInt(p.trim());
                            maxOffset = paramValue;
                        } else if (i == 2) {
                            paramValue = Integer.parseInt(p.trim());
                            offset = paramValue;
                        }
                        i++;
                    }
                    int convertedPageNum = beginOffset + offset * (pageNum - 1);
                    if (convertedPageNum > maxOffset && notOverloadMax) {
                        convertedPageNum = maxOffset;
                    }
                    log.info("begin: " + beginOffset + ",end: " + maxOffset + ",offset: " + offset + ",final page num: " + Integer.toString(convertedPageNum));
                    searchURL = searchURL.replace(pstr, Integer.toString(convertedPageNum));
                }
            }
            if (StringUtils.isNotEmpty(encodedKeyword)) {
                searchURL = searchURL.replace("${keyword}", encodedKeyword);
            }
            log.debug("final search URL: " + searchURL);
            if (MapUtils.isNotEmpty(fieldMap)) {
                searchURL = ReplaceUtils.replaceMap(fieldMap, searchURL);
            }
            return searchURL;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return null;
    }

    private static int arithmetic(String param) {
        String result = RegExp.group(param, "(\\d+)", 1);
        if (result != null && param.equals(result)) {
            return Integer.parseInt(param);
        } else if (RegExp.find(param,"[\\d\\.\\+\\-\\*/]+")) {
            double num = CalculateUtil.calculate(param);
            if (num > (int) num) {
                num = num + 1;
            }
            return (int) num;
        } else {
            log.warn("error input for arithmetic...");
            return 0;
        }
    }

    /**
     * custom Template search url from template with page num.
     * @param customURLTemplate
     * @param pageNum
     */
    public static String customTemplate(String customURLTemplate, int pageNum) {
        try {
            List<String> pagingParams = RegExp.findAll(customURLTemplate, "(\\#\\{page.*?,[\\d+\\.\\+\\-\\/\\*]+,\\d+[\\+\\-]\\})", 1);
            for (String pstr : pagingParams) {
                Map<Integer, String> map = RegExp.groupAsMap(pstr, "\\#\\{page,(.*)\\}");
                if (MapUtils.isNotEmpty(map) && map.size() == 2) {
                    String paramTemp = map.get(1); // eg: #{page,7 * 3,2,1-}
                    int i = 0;
                    int beginValue = 0;
                    int maxPageNum = 0;
                    int offset = 0;
                    String offsetSign = "+";
                    for (String param : paramTemp.split(",")) {
                        if (i == 0) {
                            beginValue = (int) arithmetic(param);// eg:7 * 3
                        } else if (i == 1) {
                            maxPageNum = (int) arithmetic(param);// eg:2
                        } else if (i == 2) {
                            // eg:1-
                            offset = Integer.parseInt(param.substring(0, param.length() - 1)); // eg:1
                            offsetSign = param.substring(param.length() - 1);// eg: - or + ,defult:+
                        }
                        i++;
                    }
                    int convertedPageNum = 0;

                    if (pageNum < 0) {
                        log.info("This PageNum is less than 0 ,pageNum : " + pageNum);
                        pageNum = 1;
                    }
                    if (pageNum > maxPageNum) {
                        log.info("This PageNum is greater than the maximum value ,pageNum : " + pageNum);
                        pageNum = maxPageNum;
                    }

                    if ("-".equals(offsetSign)) {
                        convertedPageNum = beginValue - offset * (pageNum - 1);
                    } else {
                        convertedPageNum = beginValue + offset * (pageNum - 1);
                    }
                    log.debug("convertedPageNum : " + convertedPageNum + " pageNum : " + pageNum + "\nbeginOffset : " + beginValue + "\noffset : " + offset * (pageNum - 1) + "\nmaxPageNum : " + maxPageNum + "\noffsetSign:" + offsetSign);
                    customURLTemplate = customURLTemplate.replace(map.get(0), Integer.toString(convertedPageNum));
                }
            }
            log.info("Converted custom url : " + customURLTemplate);
            return customURLTemplate;
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return customURLTemplate;
    }

}
