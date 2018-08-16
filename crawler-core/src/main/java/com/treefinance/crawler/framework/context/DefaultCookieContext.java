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

package com.treefinance.crawler.framework.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.treefinance.crawler.framework.util.CookieFormater;
import com.treefinance.crawler.framework.consts.Constants;
import com.google.common.collect.ImmutableMap;
import com.treefinance.crawler.lang.AtomicAttributes;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 00:50 2018/6/19
 */
public class DefaultCookieContext extends AtomicAttributes implements CookieContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCookieContext.class);

    @Override
    public void addCookies(String cookies) {
        addCookieString(cookies);
    }

    @Override
    public void addCookies(@Nullable String cookies, boolean retainQuote) {
        addCookieString(cookies);
        Map<String, String> cookieMap = CookieFormater.INSTANCE.parserCookieToMap(cookies, retainQuote);
        addCookieMap(cookieMap);
    }

    @Override
    public void addCookies(Map<String, String> cookies) {
        addCookieMap(cookies);
    }

    @Override
    public void appendCookies(Map<String, String> cookies) {
        if (MapUtils.isEmpty(cookies)) {
            LOGGER.warn("There was no cookies to append into process context!");
            return;
        }

        Map<String, String> cookieMap = getCookies(true);
        cookieMap.putAll(cookies);

        String cookieString = CookieFormater.INSTANCE.listToString(cookieMap);
        addCookieString(cookieString);
    }

    @Override
    public void deleteCookies() {
        removeAttribute(Constants.COOKIE_STRING);
        removeAttribute(Constants.COOKIE);
    }

    @Override
    public void copyCookies(@Nonnull CookieContext context) {
        Objects.requireNonNull(context);
        addCookieString(context.getCookiesAsString());
        addCookieMap(context.getCookies());
    }

    private void addCookieString(String cookies) {
        addAttribute(Constants.COOKIE_STRING, StringUtils.defaultString(cookies));
    }

    private void addCookieMap(Map<String, String> cookies) {
        if (MapUtils.isNotEmpty(cookies)) {
            addAttribute(Constants.COOKIE, cookies);
        } else {
            LOGGER.warn("There was no cookies to add into process context!");
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String> getCookies(boolean init) {
        Object cookies;
        if (init) {
            cookies = getAttributes().computeIfAbsent(Constants.COOKIE, key -> new HashMap<String, String>());
        } else {
            cookies = getAttribute(Constants.COOKIE);
        }
        return (Map<String, String>) cookies;
    }

    @Override
    public Map<String, String> getCookies() {
        return getCookies(true);
    }

    @Override
    public String getCookiesAsString() {
        return getAttribute(Constants.COOKIE_STRING, String.class);
    }

    @Override
    public Map<String, String> getCookiesAsMap() {
        Map<String, String> cookies = getCookies(false);
        return cookies == null ? Collections.emptyMap() : ImmutableMap.copyOf(cookies);
    }
}
