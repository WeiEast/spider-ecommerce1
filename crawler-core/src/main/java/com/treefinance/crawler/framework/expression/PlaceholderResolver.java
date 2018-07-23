package com.treefinance.crawler.framework.expression;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 15:01 2018/5/30
 */
class PlaceholderResolver {

    private static final Logger              LOGGER = LoggerFactory.getLogger(PlaceholderResolver.class);

    private final        Map<String, Object> placeholderMapping;

    private final        boolean             failOnUnknown;

    private final        boolean             allowNull;

    private final        boolean             nullToEmpty;

    PlaceholderResolver(@Nonnull ExpEvalContext context) {
        this.placeholderMapping = context.getPlaceholderMapping();
        this.failOnUnknown = context.isFailOnUnknown();
        this.allowNull = context.isAllowNull();
        this.nullToEmpty = context.isNullToEmpty();
    }

    public void validate() {
        if (failOnUnknown && placeholderMapping.isEmpty()) {
            throw new PlaceholderResolveException("Can not resolve placeholder. - Not found placeholder mapping.");
        }
    }

    public String resolveAsString(@Nonnull String placeholder) {
        Object value = findValue(placeholderMapping, placeholder);

        if (value == null) {
            if (!allowNull) {
                throw new PlaceholderResolveException("Placeholder[" + placeholder + "] value must not be null.");
            }
            LOGGER.warn("Can not resolve placeholder '{}'", placeholder);

            return nullToEmpty ? StringUtils.EMPTY : null;
        }

        return value.toString();
    }

    public Object resolve(@Nonnull String placeholder) {
        Object value = findValue(placeholderMapping, placeholder);

        if (value == null) {
            if (!allowNull) {
                throw new PlaceholderResolveException("Placeholder[" + placeholder + "] value must not be null.");
            }

            LOGGER.warn("Can not resolve placeholder '{}'", placeholder);
        }

        return value;
    }

    private Object findValue(@Nonnull Map<String, Object> map, @Nonnull String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Invalid expression placeholder : " + name);
        }

        String key = name;
        String next = null;
        int i = name.indexOf(".");
        if (i == 0) {
            throw new IllegalArgumentException("Invalid expression placeholder : " + name);
        } else if (i != -1) {
            key = name.substring(0, i);
            next = name.substring(i + 1);
        }

        if (failOnUnknown && (map.isEmpty() || !map.containsKey(key))) {
            throw new PlaceholderResolveException("Can not resolve placeholder '" + key + "'");
        }

        Object value = map.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof Map) {
            return findValue((Map) value, next);
        }

        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.isEmpty()) {
                return null;
            } else if (value instanceof List) {
                return ((List) value).get(0);
            } else {
                return ((Collection) value).iterator().next();
            }
        }

        if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                Object[] array = (Object[]) value;
                return array.length > 0 ? array[0] : null;
            } else if (value instanceof byte[]) {
                return new String((byte[]) value);
            } else if (value instanceof char[]) {
                return new String((char[]) value);
            } else if (value instanceof short[]) {
                short[] array = (short[]) value;
                return array.length > 0 ? array[0] : null;
            } else if (value instanceof int[]) {
                int[] array = (int[]) value;
                return array.length > 0 ? array[0] : null;
            } else if (value instanceof long[]) {
                long[] array = (long[]) value;
                return array.length > 0 ? array[0] : null;
            } else if (value instanceof float[]) {
                float[] array = (float[]) value;
                return array.length > 0 ? array[0] : null;
            } else if (value instanceof double[]) {
                double[] array = (double[]) value;
                return array.length > 0 ? array[0] : null;
            } else if (value instanceof boolean[]) {
                boolean[] array = (boolean[]) value;
                return array.length > 0 ? array[0] : null;
            }
            return null;
        }

        return value;
    }
}
