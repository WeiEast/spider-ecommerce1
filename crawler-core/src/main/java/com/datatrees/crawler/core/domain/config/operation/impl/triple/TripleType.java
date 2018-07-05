/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.domain.config.operation.impl.triple;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.treefinance.crawler.framework.util.CalculateUtils;
import com.treefinance.toolkit.util.RegExp;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月21日 上午11:07:36
 */
public enum TripleType {
    EQ("eq", "=") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            return firstParams.trim().equals(secondParams.trim()) ? firstResult : secondResult;
        }
    },
    NE("ne", "!=") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            return !firstParams.equals(secondParams) ? firstResult : secondResult;
        }
    },
    GT("gt", ">") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            String result;

            if (CalculateUtils.calculate(firstParams) > CalculateUtils.calculate(secondParams)) {
                result = CalculateUtils.calculate(firstResult, "0", String.class);
            } else {
                result = CalculateUtils.calculate(secondResult, "0", String.class);
            }

            return result;
        }
    },
    LT("lt", "<") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            String result;

            if (CalculateUtils.calculate(firstParams) < CalculateUtils.calculate(secondParams)) {
                result = CalculateUtils.calculate(firstResult, "0", String.class);
            } else {
                result = CalculateUtils.calculate(secondResult, "0", String.class);
            }

            return result;
        }
    },
    GE("ge", ">=") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            String result;

            if (CalculateUtils.calculate(firstParams) >= CalculateUtils.calculate(secondParams)) {
                result = CalculateUtils.calculate(firstResult, "0", String.class);
            } else {
                result = CalculateUtils.calculate(secondResult, "0", String.class);
            }

            return result;
        }
    },
    LE("le", "<=") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            String result;

            if (CalculateUtils.calculate(firstParams) <= CalculateUtils.calculate(secondParams)) {
                result = CalculateUtils.calculate(firstResult, "0", String.class);
            } else {
                result = CalculateUtils.calculate(secondResult, "0", String.class);
            }

            return result;
        }
    },
    REGEX("regex", " matches:") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            return RegExp.find(firstParams, secondParams) ? firstResult : secondResult;
        }
    },
    CONTAINS("contains", " contains:") {
        @Override
        public String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult) {
            return RegExp.find(firstParams, secondParams, Pattern.CASE_INSENSITIVE) ? firstResult : secondResult;
        }
    };// contains：正则不区分大小写


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

    public abstract String calculate(@Nonnull String firstParams, @Nonnull String secondParams, @Nonnull String firstResult, @Nonnull String secondResult);

    @Override
    public String toString() {
        return this.getValue();
    }
}
