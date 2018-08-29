/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.framework.expression;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import com.treefinance.toolkit.util.json.Jackson;
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
        LOGGER.debug("placeholder: {}, Fields context: {}", placeholder, placeholderMapping);
        Object value = findValue(placeholderMapping, placeholder);

        if (value == null) {
            if (!allowNull) {
                throw new PlaceholderResolveException("Placeholder[" + placeholder + "] value must not be null.");
            }

            LOGGER.warn("Can not resolve placeholder '{}'", placeholder);

            return nullToEmpty ? StringUtils.EMPTY : null;
        }

        LOGGER.debug("placeholder: {}, result: {}", placeholder, value);


        if (value instanceof Map || value.getClass().isArray()) {
            return Jackson.toJSONString(value);
        }

        return value.toString();
    }

    public Object resolve(@Nonnull String placeholder) {
        LOGGER.debug("placeholder: {}, Fields context: {}", placeholder, placeholderMapping);
        Object value = findValue(placeholderMapping, placeholder);

        if (value == null) {
            if (!allowNull) {
                throw new PlaceholderResolveException("Placeholder[" + placeholder + "] value must not be null.");
            }

            LOGGER.warn("Can not resolve placeholder '{}'", placeholder);
        } else {
            LOGGER.debug("placeholder: {}, result: {}", placeholder, value);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private Object findValue(@Nonnull Map<String, Object> map, @Nonnull String name) {
        Placeholder placeholder = Placeholder.parse(name);
        LOGGER.debug("Parsed placeholder: {}", placeholder);
        String key = placeholder.getName();

        if (failOnUnknown && (map.isEmpty() || !map.containsKey(key))) {
            throw new PlaceholderResolveException("Can not resolve placeholder '" + key + "'");
        }

        Object value = map.get(key);
        if (value == null) {
            return null;
        }

        String next = placeholder.getSubname();
        if (StringUtils.isNotEmpty(next)) {
            if (value instanceof Map) {
                return findValue((Map) value, next);
            } else if (value instanceof Collection) {
                Collection<Object> collection = (Collection<Object>) value;
                return collection.stream().filter(item -> item instanceof Map).map(item -> this.findValue((Map) item, next)).filter(Objects::nonNull).collect(Collectors.toList());
            } else if (value instanceof Object[]) {
                Class<?> componentType = value.getClass().getComponentType();
                if (Map.class.isAssignableFrom(componentType)) {
                    Object[] array = (Object[]) value;
                    return Arrays.stream(array).map(item -> this.findValue((Map) item, next)).filter(Objects::nonNull).collect(Collectors.toList());
                } else if (componentType == Object.class) {
                    Object[] array = (Object[]) value;
                    return Arrays.stream(array).filter(item -> item instanceof Map).map(item -> this.findValue((Map) item, next)).filter(Objects::nonNull).collect(Collectors.toList());
                }
                return Collections.emptyList();
            }

            return null;
        } else if (value instanceof byte[]) {
            return new String((byte[]) value);
        } else if (value instanceof char[]) {
            return new String((char[]) value);
        }

        return value;
    }
}