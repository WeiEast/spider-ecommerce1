/**
 * www.gf-dai.com.cn Copyright (c) 2015 All Rights Reserved.
 */

package com.datatrees.crawler.core.processor.operation.impl;

import javax.annotation.Nonnull;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.StringUtils;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.operation.impl.DateTimeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.datetime.BaseType;
import com.datatrees.crawler.core.domain.config.operation.impl.datetime.DateTimeFieldType;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.operation.OperationHelper;
import com.treefinance.crawler.framework.expression.ExpressionEngine;
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
public class DateTimeOperationImpl extends Operation<DateTimeOperation> {

    public DateTimeOperationImpl(@Nonnull DateTimeOperation operation, @Nonnull FieldExtractor extractor) {
        super(operation, extractor);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        DateTimeOperation operation = getOperation();

        BaseType baseType = operation.getBaseType();

        ExpressionEngine expressionEngine = new ExpressionEngine(request, response);

        int offset = 0;
        if (StringUtils.isNotBlank(operation.getOffset())) {
            String offsetString = expressionEngine.eval(operation.getOffset());
            offset = Integer.valueOf(offsetString);
        }

        Object src = OperationHelper.getInput(request, response);
        if (src instanceof String) {
            src = expressionEngine.eval((String) src);
        }

        DateTimeFieldType dateTimeFieldType = operation.getDateTimeFieldType();
        logger.debug("baseType: {}, dateTimeFieldType: {}, offset: {}", baseType, dateTimeFieldType, offset);

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
                logger.debug("custom src: {}", src);

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
        logger.debug("raw result in date format: {}", result);
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
