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
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.treefinance.crawler.framework.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 12:07 2018/5/18
 */
class ExpressionMatcher {

    private static final Logger  LOGGER             = LoggerFactory.getLogger(ExpressionMatcher.class);

    private static final String  EXPRESSION_REGEXP  = "(\\s*)\\$\\{\\s*([^}]+)\\s*}(\\s*)";

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_REGEXP);

    private static final int     PLACEHOLDER_GROUP  = 2;

    private              String  text;

    private              boolean empty;

    private              Matcher matcher;

    private              Boolean find;

    private ExpressionMatcher() {
    }

    private ExpressionMatcher(@Nullable String text) {
        this.text = StringUtils.defaultString(text);
        this.empty = this.text.isEmpty();
        if (!this.empty) {
            this.matcher = makeMatcher(this.text);
            this.find = null;
        } else {
            this.matcher = null;
            this.find = false;
        }
    }

    public static ExpressionMatcher match(@Nullable String text) {
        return new ExpressionMatcher(text);
    }

    @Nonnull
    private Matcher makeMatcher(String value) {
        return EXPRESSION_PATTERN.matcher(value);
    }

    private String getPlaceholder() {
        return matcher.group(PLACEHOLDER_GROUP);
    }

    public void reset(String newText) {
        this.text = StringUtils.defaultString(newText);
        this.empty = this.text.isEmpty();
        if (this.empty) {
            this.matcher = null;
            this.find = false;
        } else if (matcher == null) {
            this.matcher = makeMatcher(this.text);
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
            String prefix = StringUtils.defaultString(matcher.group(1));
            String suffix = StringUtils.defaultString(matcher.group(3));

            String placeholder = getPlaceholder();
            String replacement = resolver.resolveAsString(placeholder);
            if (replacement != null) {
                if (mappingFunction != null) {
                    String newValue = mappingFunction.apply(placeholder, replacement);
                    if (newValue != null) {
                        replacement = newValue;
                    }
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement(prefix + replacement + suffix));
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

        matcher.reset();

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

    private String findOrEvalExpSpecial(ExpEvalContext context) {
        if (matcher.matches()) {
            find = true;

            ExpEvalContext ctx = new ExpEvalContext(context.getPlaceholderMapping(), false, true);
            return new PlaceholderResolver(ctx).resolveAsString(getPlaceholder());
        }

        matcher.reset();

        return evalExp(context);
    }
}
