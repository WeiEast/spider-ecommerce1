package com.treefinance.crawler.framework.expression;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;

import com.treefinance.crawler.framework.util.UrlUtils;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 12:07 2018/5/18
 */
class ExpressionMatcher {

    private static final Logger LOGGER             = LoggerFactory.getLogger(ExpressionMatcher.class);
    private static final String EXPRESSION_PATTERN = "\\$\\{\\s*([^\\s]+?)\\s*}";
    private static final int    MATCH_GROUP        = 1;
    private String  text;
    private boolean empty;
    private Matcher matcher;
    private Boolean find;

    private ExpressionMatcher() {
    }

    private ExpressionMatcher(String text) {
        this.text = StringUtils.defaultString(text);
        this.empty = this.text.isEmpty();
        if (!this.empty) {
            this.matcher = getMatcher(this.text);
            this.find = null;
        } else {
            this.matcher = null;
            this.find = false;
        }
    }

    public static ExpressionMatcher match(String text) {
        return new ExpressionMatcher(text);
    }

    @Nonnull
    private Matcher getMatcher(String value) {
        return RegExp.getMatcher(EXPRESSION_PATTERN, value);
    }

    private String getPlaceholder() {
        return matcher.group(MATCH_GROUP);
    }

    public void reset(String newText) {
        this.text = StringUtils.defaultString(newText);
        this.empty = this.text.isEmpty();
        if (this.empty) {
            this.matcher = null;
            this.find = false;
        } else if (matcher == null) {
            this.matcher = getMatcher(this.text);
            this.find = null;
        } else {
            this.matcher.reset(this.text);
            this.find = null;
        }
    }

    public boolean findExp() {
        if (find == null) {
            find = matcher.find();
        }
        return find;
    }

    private boolean find() {
        if (find == null) {
            find = matcher.find();
        } else if (find) {
            return matcher.find(0);
        }

        return find;
    }

    public String evalExp(@Nonnull ExpEvalContext context) {
        return this.evalExp(context, null);
    }

    public String evalExp(@Nonnull ExpEvalContext context, BiFunction<String, String, String> mappingFunction) {
        Objects.requireNonNull(context);
        if (find()) {
            LOGGER.debug("Find expression and eval. input: {}", text);

            String result = replace(context, mappingFunction);

            LOGGER.debug("Exp-eval result: {}", result);

            return result;
        }

        return text;
    }

    private String replace(@Nonnull ExpEvalContext context, BiFunction<String, String, String> mappingFunction) {
        BiFunction<String, String, String> function = mappingFunction;

        if (context instanceof UrlExpEvalContext && function == null) {
            function = (placeholder, replacement) -> {
                if (((UrlExpEvalContext) context).needUrlEncode(placeholder)) {
                    LOGGER.info("Encoding url key[{}] with charset[{}]", placeholder, ((UrlExpEvalContext) context).getCharset());

                    return UrlUtils.urlEncode(replacement, ((UrlExpEvalContext) context).getCharset(), false);
                }
                return null;
            };
        }

        return toReplace(context, function);
    }

    private String toReplace(@Nonnull ExpEvalContext context, BiFunction<String, String, String> mappingFunction) {
        PlaceholderResolver resolver = new PlaceholderResolver(context);
        resolver.validate();

        StringBuffer sb = new StringBuffer();
        do {
            String placeholder = getPlaceholder();

            String replacement = resolver.resolveAsString(placeholder);
            if (replacement != null) {
                if (mappingFunction != null) {
                    String newValue = mappingFunction.apply(placeholder, replacement);
                    if (newValue != null) {
                        replacement = newValue;
                    }
                }
                matcher.appendReplacement(sb, replacement);
            }
        } while (matcher.find());
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String evalExp(Supplier<ExpEvalContext> contextSupplier) {
        return this.evalExp(contextSupplier, null);
    }

    public String evalExp(Supplier<ExpEvalContext> contextSupplier, BiFunction<String, String, String> mappingFunction) {
        if (find()) {
            ExpEvalContext context = supplyContext(contextSupplier);

            LOGGER.debug("Find expression and eval. input: {}", text);

            String result = replace(context, mappingFunction);

            LOGGER.debug("Exp-eval result: {}", result);

            return result;
        }

        return text;
    }

    @Nonnull
    private ExpEvalContext supplyContext(Supplier<ExpEvalContext> contextSupplier) {
        ExpEvalContext context = null;
        if (contextSupplier != null) {
            context = contextSupplier.get();
        }

        if (context == null) {
            context = ExpEvalContext.DEFAULT;
        }
        return context;
    }

    public String evalExp(@Nullable Map<String, Object> placeholderMapping) {
        return this.evalExp(placeholderMapping, null);
    }

    public String evalExp(@Nullable Map<String, Object> placeholderMapping, @Nullable BiFunction<String, String, String> mappingFunction) {
        return this.evalExp(new ExpEvalContext(placeholderMapping), mappingFunction);
    }

    public String evalExp(@Nullable Map<String, Object> placeholderMapping, @Nullable List<String> urlEncodedKeys, @Nullable String charset) {
        return this.evalExp(new UrlExpEvalContext(placeholderMapping, urlEncodedKeys, charset));
    }

    public Object evalExpWithObject(Supplier<ExpEvalContext> contextSupplier) {
        if (Boolean.FALSE.equals(find)) {
            return this.empty ? null : text;
        }

        ExpEvalContext context = supplyContext(contextSupplier);

        return findOrEvalExp(context);
    }

    public Object evalExpWithObject(Map<String, Object> placeholderMapping) {
        return this.evalExpWithObject(new ExpEvalContext(placeholderMapping));
    }

    public Object evalExpWithObject(@Nonnull ExpEvalContext context) {
        Objects.requireNonNull(context);
        if (Boolean.FALSE.equals(find)) {
            return this.empty ? null : text;
        }

        return findOrEvalExp(context);
    }

    private Object findOrEvalExp(@Nonnull ExpEvalContext context) {
        if (matcher.matches()) {
            find = true;

            LOGGER.debug("Eval expression and return object. input: {}", text);

            Object result = new PlaceholderResolver(context).resolve(getPlaceholder());

            LOGGER.debug("Eval expression and return object. result: {}, type: {}", result, result != null ? result.getClass() : "");

            return result;
        }

        return evalExp(context);
    }

    public String evalExpSpecial(Supplier<ExpEvalContext> contextSupplier) {
        if (Boolean.FALSE.equals(find)) {
            return this.empty ? null : text;
        }

        ExpEvalContext context = supplyContext(contextSupplier);

        return findOrEvalExpSpecial(context);
    }

    public String evalExpSpecial(Map<String, Object> placeholderMapping) {
        return this.evalExpSpecial(new ExpEvalContext(placeholderMapping));
    }

    public String evalExpSpecial(ExpEvalContext context) {
        Objects.requireNonNull(context);
        if (Boolean.FALSE.equals(find)) {
            return this.empty ? null : text;
        }

        return findOrEvalExpSpecial(context);
    }

    public String findOrEvalExpSpecial(ExpEvalContext context) {
        if (matcher.matches()) {
            find = true;

            context.setFailOnUnknown(false);
            context.setAllowNull(true);
            return new PlaceholderResolver(context).resolveAsString(getPlaceholder());
        }

        return evalExp(context);
    }
}
