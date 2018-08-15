/**
 * www.gf-dai.com.cn Copyright (c) 2015 All Rights Reserved.
 */

package com.datatrees.crawler.core.domain.config.operation.impl;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.datetime.BaseType;
import com.datatrees.crawler.core.domain.config.operation.impl.datetime.DateTimeFieldType;
import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;
import com.treefinance.crawler.framework.config.annotation.Path;
import com.treefinance.crawler.framework.config.annotation.Tag;

/**
 * @author likun
 * @version $Id: DateTimeOperation.java, v 0.1 Jul 22, 2015 11:58:37 AM likun Exp $
 */
@Tag("operation")
@Path(".[@type='datetime']")
public class DateTimeOperation extends AbstractOperation {

    private static final long              serialVersionUID = 9065578536551734019L;

    private              BaseType          baseType;

    private              DateTimeFieldType dateTimeFieldType;

    private              String            offset;

    private              String            format;

    private              Boolean           calibrate;

    private              String            sourceFormat;

    @Attr("source-format")
    public String getSourceFormat() {
        return sourceFormat;
    }

    @Node("@source-format")
    public void setSourceFormat(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    @Attr("base-type")
    public BaseType getBaseType() {
        return baseType;
    }

    @Node("@base-type")
    public void setBaseType(String baseType) {
        this.baseType = BaseType.getBaseType(baseType);
    }

    @Attr("datetime-field-type")
    public DateTimeFieldType getDateTimeFieldType() {
        return dateTimeFieldType;
    }

    @Node("@datetime-field-type")
    public void setDateTimeFieldType(String dateTimeFieldType) {
        this.dateTimeFieldType = DateTimeFieldType.getDateTimeFieldType(dateTimeFieldType);
    }

    @Attr("offset")
    public String getOffset() {

        return offset;
    }

    @Node("@offset")
    public void setOffset(String offsetLiteral) {
        this.offset = offsetLiteral;
    }

    @Attr("format")
    public String getFormat() {
        return format;
    }

    @Node("@format")
    public void setFormat(String format) {
        this.format = format;
    }

    @Attr("calibrate")
    public Boolean getCalibrate() {
        return calibrate;
    }

    @Node("@calibrate")
    public void setCalibrate(Boolean calibrate) {
        this.calibrate = calibrate;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DateTimeOperation [baseType=" + baseType + ", dateTimeFieldType=" + dateTimeFieldType + ", offset=" + offset + ", format=" + format + ", calibrate=" + calibrate + ", sourceFormat=" + sourceFormat + "]";
    }

}
