/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.extractor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.*;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import com.datatrees.crawler.core.util.xml.definition.AbstractBeanDefinition;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:23:17 PM
 */
@Tag("field-extractor")
public class FieldExtractor extends AbstractBeanDefinition implements Serializable {

    /**
     *
     */
    private static final long                    serialVersionUID = 169636932735670442L;

    private              String                  field;

    private              String                  sourceId;

    private              String                  encoding;

    private              ResultType              resultType;

    private              String                  format;

    private              AbstractPlugin          plugin;

    private              List<AbstractOperation> operationList;

    private              Boolean                 notEmpty;

    private              FieldVisibleType        fieldVisibleType;

    private              Boolean                 standBy;

    private              String                  defaultValue;

    private              String                  businessType;

    public FieldExtractor() {
        super();
        operationList = new ArrayList<AbstractOperation>();
    }

    @Attr("default-value")
    public String getDefaultValue() {
        return defaultValue;
    }

    @Node("@default-value")
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Attr("field")
    public String getField() {
        return field;
    }

    @Node("@field")
    public void setField(String field) {
        this.field = field;
    }

    @Attr("source")
    public String getSourceId() {
        return sourceId;
    }

    @Node("@source")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Attr("encoding")
    public String getEncoding() {
        return encoding;
    }

    @Node("@encoding")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Attr("result-type")
    public ResultType getResultType() {
        return resultType;
    }

    @Node("@result-type")
    public void setResultType(String resultType) {
        this.resultType = ResultType.getResultType(resultType);
    }

    @Attr("field-visible-type")
    public FieldVisibleType getFieldVisibleType() {
        return fieldVisibleType;
    }

    @Node("@field-visible-type")
    public void setFieldVisibleType(String fieldVisibleType) {
        this.fieldVisibleType = FieldVisibleType.getFieldVisibleType(fieldVisibleType);
    }

    @Attr("format")
    public String getFormat() {
        return format;
    }

    @Node("@format")
    public void setFormat(String format) {
        this.format = format;
    }

    @Attr(value = "plugin-ref", referenced = true)
    public AbstractPlugin getPlugin() {
        return plugin;
    }

    @Node(value = "@plugin-ref", referenced = true)
    public void setPlugin(AbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @Tag
    public List<AbstractOperation> getOperationList() {
        return Collections.unmodifiableList(operationList);
    }

    @Node(value = "operation", types = {ParserOperation.class, RegexOperation.class, ReplaceOperation.class, TemplateOperation.class,
            XpathOperation.class, JsonPathOperation.class, CodecOperation.class, TrimOperation.class, ReturnOperation.class, SetOperation.class,
            ExtractOperation.class, AppendOperation.class, MatchGroupOperation.class, DateTimeOperation.class, TripleOperation.class,
            MailParserOperation.class, CalculateOperation.class, EscapeOperation.class, DecodeOperation.class, ProxySetOperation.class,
            MappingOperation.class, SleepOperation.class, ReturnMatchOperation.class})
    public void setOperationList(AbstractOperation operation) {
        this.operationList.add(operation);
    }

    @Attr("not-empty")
    public Boolean getNotEmpty() {
        return notEmpty;
    }

    @Node("@not-empty")
    public void setNotEmpty(Boolean notEmpty) {
        this.notEmpty = notEmpty;
    }

    @Attr("stand-by")
    public Boolean getStandBy() {
        return standBy;
    }

    @Node("@stand-by")
    public void setStandBy(Boolean standBy) {
        this.standBy = standBy;
    }

    @Attr("business-type")
    public String getBusinessType() {
        return businessType;
    }

    @Node("@business-type")
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FieldExtractor [id =" + getId() + ",field=" + field + ",businessType=" + businessType + "]";
    }

}
