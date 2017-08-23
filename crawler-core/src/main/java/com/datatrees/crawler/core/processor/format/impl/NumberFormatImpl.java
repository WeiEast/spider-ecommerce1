/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.format.impl;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import com.datatrees.crawler.core.processor.format.container.NumberMapContainer;
import com.datatrees.crawler.core.processor.format.unit.NumberUnit;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 13, 2014 9:57:05 AM
 */
public class NumberFormatImpl extends AbstractFormat {

    private static final Logger log = LoggerFactory.getLogger(NumberFormatImpl.class);

    /**
     * number format
     */
    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        Number result = null;
        if (StringUtils.isEmpty(orginal)) {
            log.warn("orginal empty!");
            return result;
        } else {
            orginal = orginal.trim().replaceAll("\\s", "");
        }

        NumberMapContainer container = initNumberContainer(req);
        Map<String, NumberUnit> numberMap = container.getNumberMapper();
        NumberUnit unit = findTimeUnitForNumber(numberMap, orginal);
        if (unit != null) {
            result = getNumber(orginal);
            if (result != null) {
                result = calcResult(result, unit);
            }
        }
        return result;
    }

    /**
     * @param result
     * @param unit
     * @return
     */
    private Number calcResult(Number result, NumberUnit unit) {
        Double rs = null;
        try {
            rs = unit.getProportion() * result.doubleValue();
        } catch (Exception e) {
            // ignore
        }
        return rs;
    }

    /**
     * @param orginal
     * @return
     */
    protected Number getNumber(String orginal) {
        Number result = null;
        try {
            result = DecimalFormat.getInstance().parse(orginal);
        } catch (Exception e) {
            log.warn("parse Number error! " + orginal);
        }
        return result;
    }

    /**
     * @param periodMap
     * @param orginal
     * @return
     */
    private NumberUnit findTimeUnitForNumber(Map<String, NumberUnit> numberMap, String orginal) {
        NumberUnit result = null;
        if (MapUtils.isNotEmpty(numberMap)) {
            Iterator<String> periodPatterns = numberMap.keySet().iterator();
            while (periodPatterns.hasNext()) {
                String pattern = periodPatterns.next();
                if (PatternUtils.match(pattern, orginal)) {
                    result = numberMap.get(pattern);
                    log.debug("find period " + orginal + " unit :" + result.name());
                    break;
                }

            }
        }
        if (result == null) {
            log.debug("can't find correct number format  conf! " + orginal + "set to default!");
            result = NumberUnit.ONE;
        }
        return result;
    }

    /**
     *
     */
    protected NumberMapContainer initNumberContainer(Request req) {
        NumberMapContainer container = null;
        container = RequestUtil.getNumberFormat(req);
        if (container == null) {
            container = NumberMapContainer.get(getConf());
            RequestUtil.setNumberFormat(req, container);
        }
        return container;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.format.AbstractFormat#isResultType(java.lang.Object)
     */
    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof Number) {
            return true;
        } else {
            return false;
        }
    }

}
