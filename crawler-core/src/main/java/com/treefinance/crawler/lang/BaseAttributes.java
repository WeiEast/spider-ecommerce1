package com.treefinance.crawler.lang;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Jerry
 * @since 15:01 2018/5/8
 */
public class BaseAttributes implements Attributes {

    private Map<String, Object> map = new HashMap<>();

    @Override
    public void setAttribute(String name, Object attribute) {
        if (attribute == null) {
            removeAttribute(name);
        } else {
            map.put(name, attribute);
        }
    }

    @Override
    public Object getAttribute(String name) {
        return map.get(name);
    }

    @Override
    public Object computeAttribute(String name, BiFunction<String, Object, Object> mappingFunction) {
        return map.compute(name, mappingFunction);
    }

    @Override
    public Object computeAttributeIfAbsent(String name, Function<String, Object> mappingFunction) {
        return map.computeIfAbsent(name, mappingFunction);
    }

    @Override
    public void removeAttribute(String name) {
        map.remove(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(Collections.unmodifiableSet(map.keySet()));
    }

    @Override
    public void clearAttributes() {
        map.clear();
    }

    @Override
    public void addAttributes(Map<String, Object> attributes) {
        if (attributes != null) map.putAll(attributes);
    }

    @Override
    public String toString() {
        return map == null ? "{}" : map.toString();
    }
}
