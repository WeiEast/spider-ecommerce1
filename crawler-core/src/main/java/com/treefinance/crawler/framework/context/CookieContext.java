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
import java.util.Map;

/**
 * @author Jerry
 * @since 00:41 2018/6/19
 */
public interface CookieContext extends Context {

    void addCookies(@Nullable final String cookies);

    void addCookies(@Nullable final String cookies, boolean retainQuote);

    void addCookies(@Nullable final Map<String, String> cookies);

    void appendCookies(@Nullable final Map<String, String> cookies);

    void deleteCookies();

    void copyCookies(@Nonnull final CookieContext context);

    /**
     * if not to add,update or delete the cookies directly, recommend to use {@link #getCookiesAsMap()}
     * @return the cookies container
     */
    Map<String, String> getCookies();

    /**
     * @return the formatted cookies string.
     */
    String getCookiesAsString();

    /**
     * <p>
     * The returned map is immutable
     * </p>
     * @return the immutable cookie map.
     */
    Map<String, String> getCookiesAsMap();
}
