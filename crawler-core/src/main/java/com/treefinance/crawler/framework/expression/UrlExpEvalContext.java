package com.treefinance.crawler.framework.expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @since 14:29 2018/5/30
 */
public class UrlExpEvalContext extends ExpEvalContext {

    private final List<String> urlEncodedKeys;

    private final String       charset;

    public UrlExpEvalContext(Map<String, Object> placeholderMapping, List<String> urlEncodedKeys, String charset) {
        this(placeholderMapping, true, false, urlEncodedKeys, charset);
    }

    public UrlExpEvalContext(Map<String, Object> placeholderMapping, boolean failOnUnknown, boolean allowNull, List<String> urlEncodedKeys,
            String charset) {
        super(placeholderMapping, failOnUnknown, allowNull);
        this.urlEncodedKeys = urlEncodedKeys == null ? Collections.emptyList() : urlEncodedKeys;
        this.charset = charset;
    }

    public List<String> getUrlEncodedKeys() {
        return Collections.unmodifiableList(urlEncodedKeys);
    }

    public String getCharset() {
        return charset;
    }

    public boolean needUrlEncode(String key) {
        return urlEncodedKeys.contains(key);
    }
}
