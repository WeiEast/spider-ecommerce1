/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl.triple;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 上午11:07:36
 */
public enum TripleType {
    EQ("eq", "="),
    NE("ne", "!="),
    GT("gt", ">"),
    LT("lt", "<"),
    GE("ge", ">="),
    LE("le", "<="),
    REGEX("regex", " matches:"),
    CONTAINS("contains", " contains:");// contains：正则不区分大小写
    private static Map<String, TripleType> OperationTypeMap = new HashMap<String, TripleType>();

    static {
        for (TripleType obj : values()) {
            OperationTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;
    private final String expression;

    TripleType(String value, String expression) {
        this.value = value;
        this.expression = expression;
    }

    public static TripleType getOperationType(String value) {
        return OperationTypeMap.get(value);
    }

    public String getValue() {
        return value;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
