/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.format.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.DateUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 13, 2014 9:57:14 AM
 */
public class DateFormatImpl extends AbstractFormat {

    private static final Logger log                    = LoggerFactory.getLogger(DateFormatImpl.class);
    private              String DATE_PATTERN_SEPERATOR = ";";

    /**
     * @param request
     * @param pattern
     * @return
     */
    protected DateFormat getDateFormat(Request req, String pattern) {
        DateFormat result = null;
        Map<String, DateFormat> formatMap = RequestUtil.getDateFormat(req);
        if (formatMap == null) {
            formatMap = new HashMap<String, DateFormat>();
            RequestUtil.setDateFormat(req, formatMap);
        }

        result = formatMap.get(pattern);
        if (result == null) {
            result = new SimpleDateFormat(pattern);
            result.setLenient(true);
            formatMap.put(pattern, result);
        }
        return result;
    }

    @Override
    public Object format(Request request, Response response, String orginal, String pattern) {
        Date result = null;
        if (StringUtils.isEmpty(orginal)) {
            log.warn("orginal empty!");
            return result;
        }

        if (getConf() == null) {
            setConf(PropertiesConfiguration.getInstance());
        }

        if (StringUtils.isEmpty(pattern)) {
            String matchResult = PatternUtils.group(orginal, "(\\d+)", 1);
            if (matchResult != null && matchResult.equals(orginal)) {
                return new Date(Long.parseLong(orginal));
            }
            pattern = getConf().get("DEFAULT_DATE_PATTERN", Constants.DEFAULT_DATE_PATTERN);
        } else {
            orginal = orginal.trim();
        }

        DATE_PATTERN_SEPERATOR = getConf().get("DEFAULT_DATE_PATTERN_SEPERATOR", ";");

        String[] patterns = pattern.split(DATE_PATTERN_SEPERATOR);

        for (String item : patterns) {
            if (item.isEmpty()) {
                continue;
            }

            DateFormat dateFormat = getDateFormat(request, item);
            result = DateUtils.parseDate(orginal, dateFormat);
            if (result != null) {
                // handle pattern has no year
                if (!item.toLowerCase().contains("yy")) {
                    result.setYear(new Date().getYear());
                }
                break;
            }

        }

        if (result == null) {
            log.warn("Parse Date failed: orignal-" + orginal + ",pattern-" + pattern);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.format.AbstractFormat#isResultType(java.lang.Object)
     */
    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof Date) {
            return true;
        } else {
            return false;
        }
    }
}
