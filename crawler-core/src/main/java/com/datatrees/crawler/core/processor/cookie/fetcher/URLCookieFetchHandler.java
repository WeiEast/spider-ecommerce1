/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.cookie.fetcher;

import com.datatrees.common.protocol.Protocol;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.ProtocolInput.CookieScope;
import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.common.protocol.WebClientUtil;
import com.datatrees.common.protocol.http.HTTPConstants;
import com.datatrees.common.protocol.metadata.Metadata;
import com.datatrees.common.protocol.util.CookieFormater;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 26, 2014 5:02:26 PM
 */
public class URLCookieFetchHandler extends CookieFetchHandler {

    private static final Logger   log    = LoggerFactory.getLogger(URLCookieFetchHandler.class);
    private static       Protocol client = WebClientUtil.getWebClient();
    private              String   url    = null;

    public URLCookieFetchHandler(String url) {
        this.url = url;
    }

    public URLCookieFetchHandler() {}

    @Override
    public String getCookie() {
        if (StringUtils.isEmpty(url)) {
            log.warn("url is empty!");
            return "";
        }

        if (client != null) {
            // cookies.addAll();

            ProtocolInput input = new ProtocolInput().setUrl(url).setFollowRedirect(true).setCookieScope(CookieScope.REQUEST);
            ProtocolOutput output = client.getProtocolOutput(input);
            int responseCode = output.getContent().getResponseCode();
            log.debug("url cookie fetcher.." + url + " response: " + responseCode);
            Metadata metadata = output.getContent().getMetadata();
            //
            String[] cookieVals = (String[]) ArrayUtils.addAll(metadata.getValues(HTTPConstants.HTTP_HEADER_SET_COOKIE), metadata.getValues(HTTPConstants.HTTP_HEADER_SET_COOKIE2));
            if (cookieVals != null) {
                log.debug("url cookie fetcher.." + url + "cookie " + cookieVals);
                return CookieFormater.INSTANCE.parserCookie(cookieVals, false);
            }
        } else {
            log.warn("web client can't init!" + url);
        }
        return "";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
