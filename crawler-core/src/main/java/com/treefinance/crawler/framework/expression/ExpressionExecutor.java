package com.treefinance.crawler.framework.expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author Jerry
 * @since 15:11 2018/5/16
 */
final class ExpressionExecutor {

    private ExpressionExecutor() {
    }

    public static boolean findExp(@Nullable String value) {
        return ExpressionMatcher.match(value).findExp();
    }

    public static String evalExp(@Nullable String value, @Nullable Map<String, Object> placeholder) {
        return ExpressionMatcher.match(value).evalExp(placeholder);
    }

    public static String evalExp(@Nullable String value, @Nullable Map<String, Object> placeholder, @Nullable BiFunction<String, String, String> mappingFunction) {
        return ExpressionMatcher.match(value).evalExp(placeholder, mappingFunction);
    }

    public static String evalExp(@Nullable String value, @Nullable Map<String, Object> placeholder, @Nullable List<String> urlEncodedKeys, @Nullable String charset) {
        return ExpressionMatcher.match(value).evalExp(placeholder, urlEncodedKeys, charset);
    }

    public static Object evalExpWithObject(@Nullable String value, @Nullable Map<String, Object> placeholder) {
        return ExpressionMatcher.match(value).evalExpWithObject(placeholder);
    }

    public static String evalExpSpecial(@Nullable String value, @Nullable Map<String, Object> placeholder) {
        return ExpressionMatcher.match(value).evalExpSpecial(placeholder);
    }

    public static String evalExp(@Nullable String value, @Nullable Supplier<Map<String, Object>> placeholderSupplier) {
        return ExpressionMatcher.match(value).evalExp(() -> new ExpEvalContext(placeholderSupplier == null ? null : placeholderSupplier.get()));
    }

    public static String evalExp(@Nullable String value, @Nullable Supplier<Map<String, Object>> placeholderSupplier, @Nullable List<String> urlEncodedKeys, @Nullable String charset) {
        return ExpressionMatcher.match(value).evalExp(() -> new UrlExpEvalContext(placeholderSupplier == null ? null : placeholderSupplier.get(), urlEncodedKeys, charset));
    }

    public static Object evalExpWithObject(@Nullable String value, @Nullable Supplier<Map<String, Object>> placeholderSupplier) {
        return ExpressionMatcher.match(value).evalExpWithObject(() -> new ExpEvalContext(placeholderSupplier == null ? null : placeholderSupplier.get()));
    }

    public static String evalExpSpecial(@Nullable String value, @Nullable Supplier<Map<String, Object>> placeholderSupplier) {
        return ExpressionMatcher.match(value).evalExpSpecial(() -> new ExpEvalContext(placeholderSupplier == null ? null : placeholderSupplier.get()));
    }

    public static String evalExp(@Nullable String value, @Nonnull ExpEvalContext context) {
        return ExpressionMatcher.match(value).evalExp(context);
    }

    public static String evalExp(@Nullable String value, @Nonnull ExpEvalContext context, BiFunction<String, String, String> mappingFunction) {
        return ExpressionMatcher.match(value).evalExp(context, mappingFunction);
    }

    public static Object evalExpWithObject(@Nullable String value, @Nonnull ExpEvalContext context) {
        return ExpressionMatcher.match(value).evalExpWithObject(context);
    }

    public static String evalExpSpecial(@Nullable String value, @Nonnull ExpEvalContext context) {
        return ExpressionMatcher.match(value).evalExpSpecial(context);
    }

}
