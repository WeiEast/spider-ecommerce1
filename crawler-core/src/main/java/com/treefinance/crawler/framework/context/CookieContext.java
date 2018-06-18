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

    void copyCookies(@Nonnull final CrawlerContext context);

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
