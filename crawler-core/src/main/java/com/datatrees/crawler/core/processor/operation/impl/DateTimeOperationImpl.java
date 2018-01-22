/**
 * www.gf-dai.com.cn Copyright (c) 2015 All Rights Reserved.
 */

package com.datatrees.crawler.core.processor.operation.impl;

import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.StringUtils;
import com.datatrees.crawler.core.domain.config.operation.impl.DateTimeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.datetime.BaseType;
import com.datatrees.crawler.core.domain.config.operation.impl.datetime.DateTimeFieldType;
import com.datatrees.crawler.core.processor.common.FieldExtractorWarpperUtil;
import com.datatrees.crawler.core.processor.common.ReplaceUtils;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.operation.Operation;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author likun
 * @version $Id: DateTimeOperationImpl.java, v 0.1 Jul 22, 2015 11:57:20 AM likun Exp $
 */
public class DateTimeOperationImpl extends Operation {

    @Override
    public void process(Request request, Response response) throws Exception {
        DateTimeOperation operation = (DateTimeOperation) getOperation();

        BaseType baseType = operation.getBaseType();
        // replace from context
        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(ResponseUtil.getResponseFieldResult(response));
        Map<String, Object> sourceMap = RequestUtil.getSourceMap(request);

        int offset = 0;
        if (StringUtils.isNotBlank(operation.getOffset())) {
            String offsetString = ReplaceUtils.replaceMap(fieldContext, sourceMap, operation.getOffset());
            offset = Integer.valueOf(offsetString);
        }

        Object src = getInputObject(request, response);
        if (src instanceof String) {
            src = ReplaceUtils.replaceMap(fieldContext, sourceMap, src.toString());
        }

        DateTimeFieldType dateTimeFieldType = operation.getDateTimeFieldType();
        logger.debug(String.format("baseType:%s, dateTimeFieldType:%s, offset:%s", baseType, dateTimeFieldType != null ? dateTimeFieldType : "", offset));

        Object result = null;

        DateTime baseDateTime = null;
        switch (baseType) {
            case NOW: {
                baseDateTime = new DateTime();
                break;
            }
            case FIRST_DAY_OF_THIS_WEEK: {
                baseDateTime = new LocalDate().dayOfWeek().withMinimumValue().toDateTimeAtStartOfDay();
                break;
            }
            case LAST_DAY_OF_THIS_WEEK: {
                baseDateTime = new LocalDate().dayOfWeek().withMaximumValue().toDateTimeAtStartOfDay();
                break;
            }
            case FIRST_DAY_OF_THIS_MONTH: {
                baseDateTime = new LocalDate().dayOfMonth().withMinimumValue().toDateTimeAtStartOfDay();
                break;
            }
            case LAST_DAY_OF_THIS_MONTH: {
                baseDateTime = new LocalDate().dayOfMonth().withMaximumValue().toDateTimeAtStartOfDay();
                break;
            }
            case FIRST_DAY_OF_THIS_YEAR: {
                baseDateTime = new LocalDate().dayOfYear().withMinimumValue().toDateTimeAtStartOfDay();
                break;
            }
            case LAST_DAY_OF_THIS_YEAR: {
                baseDateTime = new LocalDate().dayOfYear().withMaximumValue().toDateTimeAtStartOfDay();
                break;
            }
            case CUSTOM: {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("custom src:%s", src != null ? src : ""));
                }
                if (src instanceof String) {
                    String sourceFormat = StringUtils.isBlank(operation.getSourceFormat()) ? "yyyy-MM-dd" : operation.getSourceFormat();
                    DateTimeFormatter df = DateTimeFormat.forPattern(sourceFormat);
                    baseDateTime = df.parseDateTime((String) src);
                } else {
                    baseDateTime = new DateTime(src);
                }
                break;
            }
            default: {
                break;
            }
        }

        DateTime rawResultObj = null;
        if (offset != 0 && dateTimeFieldType != null) {
            switch (dateTimeFieldType) {
                case YEAR: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.years(), offset);
                    break;
                }
                case MONTH: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.months(), offset);
                    break;
                }
                case WEEK: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.weeks(), offset);
                    break;
                }
                case WEEK_YEAR: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.weekyears(), offset);
                    break;
                }
                case DATE: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.days(), offset);
                    break;
                }
                case HOUR: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.hours(), offset);
                    break;
                }
                case MINUTE: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.minutes(), offset);
                    break;
                }
                case SECOND: {
                    rawResultObj = baseDateTime.withFieldAdded(DurationFieldType.seconds(), offset);
                    break;
                }
                default: {
                    break;
                }
            }
        } else {
            rawResultObj = baseDateTime;
        }
        if (BooleanUtils.isTrue(operation.getCalibrate())) {
            rawResultObj = timeCalibrate(rawResultObj, baseType);
        }
        String format = operation.getFormat();
        if (StringUtils.isNotBlank(format) && rawResultObj != null) {
            if (format.equals("timestamp")) {
                result = rawResultObj.toDate().getTime();
            } else {
                result = rawResultObj.toString(format);
            }
        } else {
            result = rawResultObj != null ? rawResultObj.toDate() : "";
        }
        logger.debug("raw result in date format: " + result);
        response.setOutPut(result);
    }

    private DateTime timeCalibrate(DateTime baseDateTime, BaseType baseType) {
        switch (baseType) {
            case FIRST_DAY_OF_THIS_WEEK: {
                baseDateTime = baseDateTime.dayOfWeek().withMinimumValue();
                break;
            }
            case LAST_DAY_OF_THIS_WEEK: {
                baseDateTime = baseDateTime.dayOfWeek().withMaximumValue();
                break;
            }
            case FIRST_DAY_OF_THIS_MONTH: {
                baseDateTime = baseDateTime.dayOfMonth().withMinimumValue();
                break;
            }
            case LAST_DAY_OF_THIS_MONTH: {
                baseDateTime = baseDateTime.dayOfMonth().withMaximumValue();
                break;
            }
            case FIRST_DAY_OF_THIS_YEAR: {
                baseDateTime = baseDateTime.dayOfYear().withMinimumValue();
                break;
            }
            case LAST_DAY_OF_THIS_YEAR: {
                baseDateTime = baseDateTime.dayOfYear().withMaximumValue();
                break;
            }
            default: {
                break;
            }
        }
        return baseDateTime;
    }
}
