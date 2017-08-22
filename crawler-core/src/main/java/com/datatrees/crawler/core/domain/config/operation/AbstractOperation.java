/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.domain.config.operation;

import java.io.Serializable;

import com.datatrees.common.util.json.annotation.Description;
import com.datatrees.crawler.core.domain.config.operation.impl.AppendOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.CalculateOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.CodecOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.DateTimeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.DecodeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.EscapeOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.ExtractOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.JsonPathOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.MailParserOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.MappingOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.MatchGroupOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.ParserOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.ProxySetOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.RegexOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.ReplaceOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.ReturnMatchOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.ReturnOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.SetOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.SleepOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.TemplateOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.TrimOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.TripleOperation;
import com.datatrees.crawler.core.domain.config.operation.impl.XpathOperation;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.Node;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 2:24:35 PM
 */
@Description(value = "type", keys = {"REPLACE", "XPATH", "JSONPATH", "REGEX", "PARSER", "TEMPLATE", "CODEC", "TRIM", "RETURN", "SET", "EXTRACT",
        "APPEND", "MATCHGROUP", "DATETIME", "TRIPLE", "MAILPARSER", "CALCULATE", "ESCAPE", "DECODE", "PROXYSET", "MAPPING", "SLEEP","RETURNMATCH"}, types = {
        ReplaceOperation.class, XpathOperation.class, JsonPathOperation.class, RegexOperation.class, ParserOperation.class, TemplateOperation.class,
        CodecOperation.class, TrimOperation.class, ReturnOperation.class, SetOperation.class, ExtractOperation.class, AppendOperation.class,
        MatchGroupOperation.class, DateTimeOperation.class, TripleOperation.class, MailParserOperation.class, CalculateOperation.class,
        EscapeOperation.class, DecodeOperation.class, ProxySetOperation.class, MappingOperation.class, SleepOperation.class,ReturnMatchOperation.class})
public abstract class AbstractOperation implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3764777526967983011L;
    private OperationType type;

    @Attr("type")
    public OperationType getType() {
        return type;
    }

    @Node("@type")
    public void setType(String type) {
        this.type = OperationType.getOperationType(type);
    }

}
