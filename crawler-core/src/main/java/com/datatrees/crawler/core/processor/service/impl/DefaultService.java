/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.*;
import com.datatrees.common.protocol.ProtocolInput.CookieScope;
import com.datatrees.common.protocol.http.HttpResponse;
import com.datatrees.common.protocol.metadata.Metadata;
import com.datatrees.common.protocol.util.CookieFormater;
import com.datatrees.common.protocol.util.CookieParser;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.page.impl.RetryMode;
import com.datatrees.crawler.core.domain.config.properties.Properties;
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
import com.google.common.net.HttpHeaders;
import com.treefinance.toolkit.util.Assert;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * default httpclient service
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 10:55:36 AM
 */
public class DefaultService extends ServiceBase {

    private static final int DEFAULT_MAX_RETRIES   = PropertiesConfiguration.getInstance().getInt("default.request.max.retrycount", 3);

    private static final int DEFAULT_WAIT_INTERVAL = PropertiesConfiguration.getInstance().getInt("default.wait.interval", 500);

    @Override
    public void process(@Nonnull Request request, @Nonnull Response response) throws Exception {
        SearchProcessorContext context = (SearchProcessorContext) RequestUtil.getProcessorContext(request);

        Assert.notNull(context, "Search context for default service must not be null!");

        LinkNode current = RequestUtil.getCurrentUrl(request);

        Assert.notNull(current, "The request link node is missing!");

        String url = current.getUrl();

        logger.info("Request url: {}", url);

        ProtocolInput input = new ProtocolInput();
        input.setUrl(url);

        // add headers in request
        this.addHeaders(input, context, current);

        // get cookie config
        AbstractCookie cookie = context.getCookieConf();
        CookieScope scope = getCookieScope(cookie);
        input.setCookieScope(scope);
        input.setCookie(ProcessorContextUtil.getCookieString(context));

        Boolean coexist = cookie instanceof BaseCookie ? ((BaseCookie) cookie).getCoexist() : null;
        if (coexist != null) {
            input.setCoExist(coexist);
        }

        input.setFollowRedirect(true).setRedirectUriEscaped(context.isRedirectUriEscaped()).setState(ProcessorContextUtil.getHttpState(context))
                .setAllowCircularRedirects(context.isAllowCircularRedirects());

        Protocol protocol = this.getHttpClient(context.getHttpClientType());

        boolean visitSucess = false;
        int retryCount = this.getMaxRetries(request, context);
        int i = Math.max(current.getRetryCount(), 0);
        Proxy proxy;
        for (; i < retryCount; i++) {
            try {
                proxy = this.setProxy(input, context, url);// set proxy url,if needed
            } catch (Exception e) {
                long sleepSecond = this.sleep(0L, i, context);
                current.increaseRetryCount();
                logger.warn("Error setting proxy >>> {}, sleep {}ms, request []retry, url: {}", e.getMessage(), sleepSecond, i, url);
                if (i == retryCount - 1) {
                    ResponseUtil.setResponseStatus(response, Status.NO_PROXY);
                    logger.warn("set no proxy status url: {}", url);
                    break;
                } else {
                    continue;
                }
            }
            ProtocolOutput output = protocol.getProtocolOutput(input);
            if (logger.isDebugEnabled()) {
                Content content = output.getContent();
                logger.debug("Response code: {}, response body: {}", content.getResponseCode(), content.getContentAsString());
            }
            ResponseUtil.setProtocolResponse(response, output);
            ResponseUtil.setResponseStatus(response, output.getStatusCode());
            if (output.isSuccess() || output.isRedirector()) {
                visitSucess = true;

                // reset cookies while cookie scope is <code>USER_SESSION</code>
                resetCookiesIfNeed(context, scope, coexist, output);

                handleRedirect(output, current, request);

                String content = output.getContent().getContentAsString();

                Page page = RequestUtil.getCurrenPage(request);

                // check if need retry
                if (page != null && StringUtils.isNotBlank(page.getPageRetryPattern()) && RegExp.find(content, page.getPageRetryPattern())) {
                    this.notifyProxyStatus(proxy, ProxyStatus.FAIL, context);

                    long sleepMills = getSleepMills(page, i);
                    sleepMills = this.sleep(sleepMills, i, context);

                    current.increaseRetryCount();

                    if (page.getRetryMode() == null || page.getRetryMode() == RetryMode.RETRY) {
                        logger.warn("sleep {}ms, request [{}] retry, url:{}, content: {}", sleepMills, current.getRetryCount(), url, content);
                        continue;
                    } else if (page.getRetryMode() == RetryMode.REQUEUE) {
                        logger.warn("sleep {}ms, request [{}] retry, node will requeue url: {}, content: {}", sleepMills, current.getRetryCount(),
                                url, content);
                        // node requeue
                        current.setNeedRequeue(true);
                        ResponseUtil.setResponseStatus(response, Status.REQUEUE);
                    } else if (page.getRetryMode() == RetryMode.PROXY_RETRY) {
                        logger.warn("wait proxy change sleep {}ms, request [{}] retry, url: {}, content: {}", sleepMills, current.getRetryCount(),
                                url, content);
                        this.proxyRelease(proxy, context);
                        continue;
                    } else if (page.getRetryMode() == RetryMode.PROXY_REQUEUE) {
                        logger.warn("wait proxy change sleep {}ms, request [{}] retry, node will requeue url: {}, content: {}", sleepMills,
                                current.getRetryCount(), url, content);
                        // node requeue
                        current.setNeedRequeue(true);
                        ResponseUtil.setResponseStatus(response, Status.REQUEUE);
                        this.proxyRelease(proxy, context);
                    }
                }

                this.notifyProxyStatus(proxy, ProxyStatus.SUCCESS, context);

                response.setOutPut(content);

                RequestUtil.setContent(request, content);
                RequestUtil.setContentCharset(request, output.getContent().getCharSet());
                break;
            } else if (output.needRetry()) {
                this.notifyProxyStatus(proxy, ProxyStatus.FAIL, context);
                // 由于暂时无法分清异常，出现异常都重置代理
                logger.warn("release proxy if need when request exception or server exception.");
                this.proxyRelease(proxy, context);

                long sleepMills = this.sleep(0L, i, context);

                current.increaseRetryCount();
                logger.warn("sleep {}ms, request [{}] error, url: {}, statusCode: {}", sleepMills, (i + 1), url, output.getStatusCode());
            } else {
                logger.warn("ignore request error, statusCode: {}, url: {}", output.getStatusCode(), url);
                break;
            }
        }
        ProcessorContextUtil.addThreadLocalLinkNode(context, current);
        ProcessorContextUtil.addThreadLocalResponse(context, response);
        if (!visitSucess) {
            throw new Exception("request error :" + url + ", after " + (i + 1) + " retry.");
        }
    }

    private void resetCookiesIfNeed(SearchProcessorContext context, CookieScope scope, Boolean coexist, ProtocolOutput output) {
        if (scope == CookieScope.USER_SESSION) {
            Metadata metadata = output.getContent().getMetadata();

            String[] setCookies = metadata.getValues(HttpHeaders.SET_COOKIE);
            String[] setCookies2 = metadata.getValues(HttpHeaders.SET_COOKIE2);
            setCookies = ArrayUtils.addAll(setCookies, setCookies2);

            if (ArrayUtils.isNotEmpty(setCookies)) {
                com.datatrees.common.protocol.Response response = output.getResponse();

                String cookieString = "";
                if (response instanceof HttpResponse && BooleanUtils.isTrue(coexist)) {
                    HttpResponse httpResponse = (HttpResponse) response;
                    ProcessorContextUtil.setHttpState(context, httpResponse.getState());
                    if (httpResponse.getState() != null) {
                        cookieString = CookieParser.formatCookies(httpResponse.getState().getCookies());
                    }
                } else {
                    Map<String, String> cookieMap = ProcessorContextUtil.getCookieMap(context);
                    cookieMap.putAll(CookieFormater.INSTANCE.parserCookietToMap(setCookies, scope.isRetainQuote()));
                    cookieString = CookieFormater.INSTANCE.listToString(cookieMap);
                }
                logger.info("Reset cookies: {}", cookieString);
                ProcessorContextUtil.setCookieString(context, cookieString);
            }
        }
    }

    private long getSleepMills(@Nonnull final Page page, final int seed) {
        int sleepSeconds = page.getRetrySleepSecond() == null ? 0 : page.getRetrySleepSecond() * (seed + 1);
        return TimeUnit.SECONDS.toMillis(sleepSeconds);
    }

    private long sleep(final long sleepMillis, final int seed, @Nonnull final SearchProcessorContext context) throws InterruptedException {
        long millis = sleepMillis;

        if (millis == 0L) {
            millis = 100 * (seed + 1) + (int) (Math.random() * getWaitInterval(context) * (seed + 1));
        }

        Thread.sleep(millis);

        return millis;
    }

    private int getWaitInterval(@Nonnull final SearchProcessorContext context) {
        Integer waitIntervalMillis = null;

        Properties searchProperties = context.getSearchProperties();
        if (searchProperties != null) {
            waitIntervalMillis = searchProperties.getWaitIntervalMillis();
        }

        if (waitIntervalMillis == null || waitIntervalMillis < 0) {
            waitIntervalMillis = DEFAULT_WAIT_INTERVAL;
        }

        return waitIntervalMillis;
    }

    private void proxyRelease(@Nullable final Proxy proxy, @Nonnull final SearchProcessorContext context) throws Exception {
        if (proxy != null) {
            ProxyManager proxyManager = context.getProxyManager();
            if (proxyManager != null) {
                proxyManager.release();
            }
        }
    }

    private void notifyProxyStatus(@Nullable final Proxy proxy, @Nonnull final ProxyStatus status,
            @Nonnull final SearchProcessorContext context) throws Exception {
        if (proxy != null) {
            ProxyManager proxyManager = context.getProxyManager();
            if (proxyManager != null) {
                proxyManager.callBackProxy(status);
            }
        }
    }

    private Proxy setProxy(@Nonnull final ProtocolInput input, @Nonnull final SearchProcessorContext context,
            @Nonnull final String url) throws Exception {
        Proxy proxy = context.getProxy(url, false);
        if (proxy != null) {
            input.setProxy(proxy.format());
        }
        return proxy;
    }

    private int getMaxRetries(@Nonnull final Request request, @Nonnull final SearchProcessorContext context) {
        Integer retry = RequestUtil.getRetryCount(request);
        if (retry == null) {
            Properties searchProperties = context.getSearchProperties();
            if (searchProperties != null) {
                retry = searchProperties.getMaxRetryCount();
            }
        }

        if (retry == null || retry <= 0) {
            retry = DEFAULT_MAX_RETRIES;
        }

        return retry;
    }

    @Nonnull
    private Protocol getHttpClient(String httpClientType) {
        Protocol protocol;
        if (StringUtils.isNotEmpty(httpClientType)) {
            protocol = WebClientUtil.getWebClient(httpClientType);
        } else {
            protocol = WebClientUtil.getWebClient();
        }

        Assert.notNull(protocol);

        return protocol;
    }

    private CookieScope getCookieScope(@Nullable final AbstractCookie cookie) {
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
        return scope;
    }

    private void addHeaders(@Nonnull final ProtocolInput input, @Nonnull final SearchProcessorContext context, @Nonnull final LinkNode current) {
        input.addHeaders(context.getDefaultHeader());
        input.addHeaders(current.getHeaders());
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(current.getReferer())) {
            input.addHeader(HttpHeaders.REFERER, current.getReferer());
        }
    }

    /**
     * @param output
     * @param current
     */
    private void handleRedirect(ProtocolOutput output, LinkNode current, Request request) {
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
