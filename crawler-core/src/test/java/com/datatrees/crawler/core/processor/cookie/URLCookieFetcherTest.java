/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.cookie;

import java.net.HttpCookie;
import java.util.List;

import com.datatrees.crawler.core.processor.cookie.fetcher.URLCookieFetchHandler;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 28, 2014 9:33:10 AM
 */
public class URLCookieFetcherTest {

    @Ignore
    @Test
    public void testCookieParser() {
        String cookie = "BAIDUID=CD71C59BDC34995D86794255769930CA:FG=1; expires=Thu, 31-Dec-37 23:55:55 GMT; max-age=2147483647; path=/; domain=.baidu.com;";// "BDSVRTM=0; path=/;H_PS_PSSID=5778_5229_1450_5224_5288_5723_4264_4759; path=/; domain=.baidu.com";
        List<HttpCookie> cookies = HttpCookie.parse(cookie);
        for (HttpCookie httpCookie : cookies) {
            System.out.println(httpCookie.getName() + "\t" + httpCookie.getValue());
        }

    }

    //    @Ignore
    @Test
    public void testURLFetcher() {
        String url = "http://www.baidu.com";
        URLCookieFetchHandler handler = new URLCookieFetchHandler(url);
        String cookie = handler.getCookie();
        System.out.println(cookie);
    }

}
