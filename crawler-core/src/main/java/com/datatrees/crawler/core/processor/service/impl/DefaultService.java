/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.*;
import com.datatrees.common.protocol.ProtocolInput.CookieScope;
import com.datatrees.common.protocol.http.HTTPConstants;
import com.datatrees.common.protocol.http.HttpResponse;
import com.datatrees.common.protocol.util.CookieFormater;
import com.datatrees.common.protocol.util.CookieParser;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.page.impl.RetryMode;
import com.datatrees.crawler.core.domain.config.properties.cookie.AbstractCookie;
import com.datatrees.crawler.core.domain.config.properties.cookie.BaseCookie;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.datatrees.crawler.core.processor.proxy.ProxyStatus;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * default httpclient service
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 10:55:36 AM
 */
public class DefaultService extends ServiceBase {

    private static final Logger log                  = LoggerFactory.getLogger(DefaultService.class);
    private static       int    defaultMaxRetryCount = PropertiesConfiguration.getInstance().getInt("default.request.max.retrycount", 3);
    private static       int    defaultWaitInterval  = PropertiesConfiguration.getInstance().getInt("default.wait.interval", 500);

    private int getMaxRetryCount(Request request, SearchProcessorContext context) {
        if (RequestUtil.getRetryCount(request) != null) {
            return RequestUtil.getRetryCount(request);
        } else if (context != null && context.getSearchConfig() != null && context.getSearchConfig().getProperties() != null && context.getSearchConfig().getProperties().getMaxRetryCount() != null && context.getSearchConfig().getProperties().getMaxRetryCount() > 0) {
            return context.getSearchConfig().getProperties().getMaxRetryCount();
        } else {
            return defaultMaxRetryCount;
        }
    }

    private int getWaitInterval(SearchProcessorContext context) {
        if (context != null && context.getSearchConfig() != null && context.getSearchConfig().getProperties() != null && context.getSearchConfig().getProperties().getWaitIntervalMillis() != null) {
            return context.getSearchConfig().getProperties().getWaitIntervalMillis();
        } else {
            return defaultWaitInterval;
        }
    }

    // private void cookieRefresh() {
    // CustomCookie cookieConf = (CustomCookie) cookie;
    // String failPattern = cookieConf.getFailPattern();
    // String handlerConf = cookieConf.getHandleConfig();
    // String pageContent = output.getContent().getContentAsString();
    // if (StringUtils.isNotEmpty(pageContent) && StringUtils.isNotEmpty(failPattern) &&
    // StringUtils.isNotEmpty(pageContent)) {
    // boolean matched = PatternUtils.match(failPattern, pageContent);
    // if (matched) {
    // try {
    // CookieFetchHandler cfh = CookieFetchFactory.getCookieHandler(context);
    // if (cfh != null) {
    // String ck = cfh.getCookie();
    // String orginal = current.getHeader("Cookie");
    // if (StringUtils.isNotEmpty(orginal)) {
    // ck = orginal + "; " + ck;
    // ck = CookieFormater.INSTANCE.parserCookie(ck);
    // }
    // log.info("new cookie..." + ck);
    // current.addHeader("Cookie", ck);
    // needRun = true;
    // continue;
    // }
    // } catch (Exception e) {
    // log.error("invoke cookie fetcher error!", e);
    // needRun = false;
    // }
    //
    // }
    // }
    // }
    private Proxy setProxy(ProtocolInput input, SearchProcessorContext context, String url) throws Exception {
        Proxy proxy = null;
        if (context.needProxyByUrl(url)) {
            ProxyManager proxyManager = context.getProxyManager();
            Preconditions.checkNotNull(proxyManager);
            proxy = proxyManager.getProxy();
            Preconditions.checkNotNull(proxy);
            if (proxy != Proxy.LOCALNET) {
                input.setProxy(proxy.format());
            }
        }
        return proxy;
    }

    private void proxyStatusCallBack(Proxy proxy, SearchProcessorContext context, ProxyStatus status) throws Exception {
        if (proxy != null && proxy != Proxy.LOCALNET) {
            ProxyManager proxyManager = context.getProxyManager();
            Preconditions.checkNotNull(proxyManager);
            proxyManager.callBackProxy(status);
        }
    }

    private void proxyRelease(Proxy proxy, SearchProcessorContext context) throws Exception {
        if (proxy != null && proxy != Proxy.LOCALNET) {
            ProxyManager proxyManager = context.getProxyManager();
            Preconditions.checkNotNull(proxyManager);
            proxyManager.release();
        }
    }

    private void resetCookie(AbstractCookie cookie, SearchProcessorContext context, String[] setCookies, com.datatrees.common.protocol.Response response) {
        String cookieString = "";
        if (cookie != null && cookie instanceof BaseCookie && BooleanUtils.isTrue(((BaseCookie) cookie).getCoexist()) && response != null && response instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse) response;
            ProcessorContextUtil.setHttpState(context, httpResponse.getState());
            if (httpResponse.getState() != null) {
                cookieString = CookieParser.formatCookies(httpResponse.getState().getCookies());
            }
        } else {
            Map<String, String> cookieMap = ProcessorContextUtil.getCookieMap(context);
            cookieMap.putAll(CookieFormater.INSTANCE.parserCookietToMap(setCookies, cookie.getRetainQuote()));
            cookieString = CookieFormater.INSTANCE.listToString(cookieMap);
        }
        log.info("reset cookie string " + cookieString);
        ProcessorContextUtil.setCookieString(context, cookieString);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        LinkNode current = RequestUtil.getCurrentUrl(request);
        String url = current.getUrl();
        log.info("request url:" + url);
        SearchProcessorContext context = (SearchProcessorContext) RequestUtil.getProcessorContext(request);
        ProtocolInput input = new ProtocolInput();
        // get cookie config
        AbstractCookie cookie = context.getCookieConf();
        CookieScope scope = CookieScope.SESSION;
        if (cookie != null) {
            switch (cookie.getScope()) {
                case REQUEST:
                    scope = CookieScope.REQUEST;
                    break;
                case USER_SESSION:
                    scope = CookieScope.USER_SESSION;
                    break;
                default:
                    scope = CookieScope.SESSION;
                    break;
            }
            scope.setRetainQuote(cookie.getRetainQuote());
        }
        // header set
        Map<String, String> headers = new HashMap<String, String>();
        headers.putAll(context.getDefaultHeader());
        headers.putAll(current.getHeaders());
        if (StringUtils.isNotEmpty(current.getReferer())) {
            headers.put("Referer", current.getReferer());
        }

        input.setUrl(url).addHeaders(headers).setFollowRedirect(true).setRedirectUriEscaped(context.getRedirectUriEscaped()).setCookieScope(scope).setCookie(ProcessorContextUtil.getCookieString(context)).setState(ProcessorContextUtil.getHttpState(context)).setAllowCircularRedirects(context.getAllowCircularRedirects());

        if (cookie instanceof BaseCookie) {
            input.setCoExist(((BaseCookie) cookie).getCoexist());
        }

        String HttpClientType = context.getHttpClientType();
        Protocol protocol = null;
        if (StringUtils.isNotEmpty(HttpClientType)) {
            protocol = WebClientUtil.getWebClient(HttpClientType);
        } else {
            protocol = WebClientUtil.getWebClient();
        }

        // request
        Preconditions.checkNotNull(protocol);
        int retryCount = this.getMaxRetryCount(request, context);
        boolean visitSucess = false;
        int i = current.getRetryCount() > 0 ? current.getRetryCount() : 0;
        Proxy proxy = null;
        for (; i < retryCount; i++) {
            try {
                proxy = this.setProxy(input, context, url);// set proxy url,if needed
            } catch (Exception e) {
                long sleepSecond = this.sleep(i, context, 0l);
                current.increaseRetryCount();
                log.warn("get proxy error" + e.getMessage() + ", sleep " + sleepSecond + "ms, request [" + i + "] retry, url:" + url);
                if (i == retryCount - 1) {
                    ResponseUtil.setResponseStatus(response, Status.NO_PROXY);
                    log.warn("set no proxy status url:" + url);
                    break;
                } else {
                    continue;
                }
            }
            ProtocolOutput output = protocol.getProtocolOutput(input);
            ResponseUtil.setProtocolResponse(response, output);
            ResponseUtil.setResponseStatus(response, output.getStatusCode());
            if (output.isSuccess() || output.isRedirector()) {
                visitSucess = true;
                // cookie reset while USER_SESSION
                String[] vals = (String[]) ArrayUtils.addAll(output.getContent().getMetadata().getValues(HTTPConstants.HTTP_HEADER_SET_COOKIE), output.getContent().getMetadata().getValues(HTTPConstants.HTTP_HEADER_SET_COOKIE2));
                if (scope == CookieScope.USER_SESSION && ArrayUtils.isNotEmpty(vals)) {
                    this.resetCookie(cookie, context, vals, output.getResponse());
                }
                hanlderRedirect(output, current, request);
                String content = output.getContent().getContentAsString();
                Page page = RequestUtil.getCurrenPage(request);

                // check if need retry
                if (page != null && StringUtils.isNotBlank(page.getPageRetryPattern()) && PatternUtils.match(page.getPageRetryPattern(), content)) {
                    this.proxyStatusCallBack(proxy, context, ProxyStatus.FAIL);
                    int defaultsleepSecond = page.getRetrySleepSecond() == null ? 0 : page.getRetrySleepSecond() * (i + 1);
                    long sleepSecond = this.sleep(i, context, 1000l * defaultsleepSecond);
                    current.increaseRetryCount();
                    if (page.getRetryMode() == null || page.getRetryMode() == RetryMode.RETRY) {
                        log.warn("sleep " + sleepSecond + "ms, request [" + current.getRetryCount() + "] retry, url:" + url + ", content:" + content);
                        continue;
                    } else if (page.getRetryMode() == RetryMode.REQUEUE) {
                        // node requeue
                        current.setNeedRequeue(true);
                        ResponseUtil.setResponseStatus(response, Status.REQUEUE);
                        log.warn("sleep " + sleepSecond + "ms, request [" + current.getRetryCount() + "] retry, node will requeue url:" + url + ", content:" + content);
                    } else if (page.getRetryMode() == RetryMode.PROXY_RETRY) {
                        log.warn("wait proxy change sleep " + sleepSecond + "ms, request [" + current.getRetryCount() + "] retry, url:" + url + ", content:" + content);
                        this.proxyRelease(proxy, context);
                        continue;
                    } else if (page.getRetryMode() == RetryMode.PROXY_REQUEUE) {
                        // node requeue
                        current.setNeedRequeue(true);
                        ResponseUtil.setResponseStatus(response, Status.REQUEUE);
                        this.proxyRelease(proxy, context);
                        log.warn("wait proxy change sleep " + sleepSecond + "ms, request [" + current.getRetryCount() + "] retry, node will requeue url:" + url + ", content:" + content);
                    }
                }

                this.proxyStatusCallBack(proxy, context, ProxyStatus.SUCCESS);
                ResponseUtil.setResponseContent(response, content);
                RequestUtil.setContent(request, content);
                RequestUtil.setContentCharset(request, output.getContent().getCharSet());
                break;
            } else if (output.needRetry()) {
                this.proxyStatusCallBack(proxy, context, ProxyStatus.FAIL);
                long sleepSecond = this.sleep(i, context, 0l);
                current.increaseRetryCount();
                log.warn("sleep " + sleepSecond + "ms, request [" + (i + 1) + "] error, url:" + url + ", statusCode:" + output.getStatusCode());
            } else {
                log.warn("ignore request error, statusCode:" + output.getStatusCode() + ", url:" + url);
                break;
            }
        }
        ProcessorContextUtil.addThreadLocalLinkNode(context, current);
        ProcessorContextUtil.addThreadLocalResponse(context, response);
        if (!visitSucess) {
            throw new Exception("request error :" + url + ",after " + (i + 1) + " retry.");
        }
    }

    private long sleep(int seed, SearchProcessorContext context, long sleepMillis) throws InterruptedException {
        if (sleepMillis == 0L) {
            sleepMillis = 100 * (seed + 1) + (int) (Math.random() * getWaitInterval(context) * (seed + 1));
        }
        Thread.sleep(sleepMillis);
        return sleepMillis;
    }

    /**
     * @param output
     * @param current
     */
    private void hanlderRedirect(ProtocolOutput output, LinkNode current, Request request) {
        String baseUrl = output.getContent().getBaseUrl();
        String url = output.getContent().getUrl();
        if (!url.equalsIgnoreCase(baseUrl)) {
            current.setRedirectUrl(url);
            RequestUtil.getContext(request).put(Constants.PAGE_REQUEST_CONTEXT_REDIRECT_URL, url);
        }

        // get redirect Url from headers
        if (StringUtils.isBlank(current.getRedirectUrl())) {
            String redirectUrl = output.getContent().getMetadata().get(Constant.REDIRECT_URL);
            if (StringUtils.isNotBlank(redirectUrl)) {
                current.setRedirectUrl(redirectUrl);
                RequestUtil.getContext(request).put(Constants.PAGE_REQUEST_CONTEXT_REDIRECT_URL, redirectUrl);
            }
        }
    }

}
