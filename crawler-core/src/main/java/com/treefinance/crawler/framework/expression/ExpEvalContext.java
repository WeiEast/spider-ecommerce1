package com.treefinance.crawler.framework.expression;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import com.treefinance.crawler.lang.Copyable;
import com.treefinance.toolkit.util.kryo.KryoUtils;

/**
 * @author Jerry
 * @since 13:34 2018/5/30
 */
public class ExpEvalContext implements Serializable,Copyable<ExpEvalContext> {

    public static final ExpEvalContext DEFAULT = new ExpEvalContext();
    private final Map<String, Object> placeholderMapping;
    private boolean failOnUnknown = true;
    private boolean allowNull     = false;
    private boolean nullToEmpty   = false;

    public ExpEvalContext() {
        this(null);
    }

    public ExpEvalContext(Map<String, Object> placeholderMapping) {
        this(placeholderMapping, true);
    }

    public ExpEvalContext(Map<String, Object> placeholderMapping, boolean failOnUnknown) {
        this(placeholderMapping, failOnUnknown, false);
    }

    public ExpEvalContext(Map<String, Object> placeholderMapping, boolean failOnUnknown, boolean allowNull) {
        this.placeholderMapping = placeholderMapping == null ? Collections.emptyMap() : placeholderMapping;
        this.failOnUnknown = failOnUnknown;
        this.allowNull = allowNull;
    }

    public Map<String, Object> getPlaceholderMapping() {
        return Collections.unmodifiableMap(placeholderMapping);
    }

    public boolean isFailOnUnknown() {
        return failOnUnknown;
    }

    public void setFailOnUnknown(boolean failOnUnknown) {
        this.failOnUnknown = failOnUnknown;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public boolean isNullToEmpty() {
        return nullToEmpty;
    }

    public void setNullToEmpty(boolean nullToEmpty) {
        this.nullToEmpty = nullToEmpty;
    }

    @Override
    public ExpEvalContext copy() {
        return KryoUtils.copy(this);
    }
}
