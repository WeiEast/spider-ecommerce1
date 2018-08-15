package com.treefinance.crawler.framework.context.function;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Jerry
 * @since 10:46 2018/7/31
 */
public interface RequestMetadata {

    Map<String, Object> getExtra();

    void addExtra(String name, Object value);

    void addExtra(Map<String, Object> extra);

    void setExtra(Map<String, Object> extra);

    Object computeExtraIfAbsent(@Nonnull String name, @Nonnull Function<String, Object> mappingFunction);

    default <T> T computeExtraIfAbsent(@Nonnull String name, @Nonnull Function<String, Object> mappingFunction, @Nonnull Class<T> type) {
        Object value = computeExtraIfAbsent(name, mappingFunction);

        return value == null ? null : type.cast(value);
    }
}
