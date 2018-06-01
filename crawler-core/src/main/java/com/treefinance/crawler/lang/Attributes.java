package com.treefinance.crawler.lang;

import java.util.Enumeration;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Jerry
 * @since 14:59 2018/5/8
 */
public interface Attributes {

    void setAttribute(String name, Object attribute);

    Object getAttribute(String name);

    default <T> T getAttribute(String name, Class<T> type) {
        Object value = getAttribute(name);
        return value == null ? null : type.cast(value);
    }

    default Object getOrDefaultAttribute(String name, Object defaultValue) {
        Object value = getAttribute(name);
        return value == null ? defaultValue : value;
    }

    default <T> T getOrDefaultAttribute(String name, T defaultValue, Class<T> type) {
        T value = getAttribute(name, type);
        return value == null ? defaultValue : value;
    }

    Object computeAttribute(String name, BiFunction<String, Object, Object> mappingFunction);

    Object computeAttributeIfAbsent(String name, Function<String, Object> mappingFunction);

    void removeAttribute(String name);

    Map<String, Object> getAttributes();

    Enumeration<String> getAttributeNames();

    void clearAttributes();

    void addAttributes(Map<String, Object> attributes);

    default void addAttributes(Attributes attributes) {
        if (attributes != null) {
            Enumeration<String> e = attributes.getAttributeNames();
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                setAttribute(name, attributes.getAttribute(name));
            }
        }
    }
}
