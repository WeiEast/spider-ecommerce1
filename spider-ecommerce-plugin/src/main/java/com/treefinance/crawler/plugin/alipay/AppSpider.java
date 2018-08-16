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

package com.treefinance.crawler.plugin.alipay;

import java.io.IOException;
import java.util.Map;

import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.proxy.Proxy;
import com.treefinance.crawler.framework.extension.spider.BaseSpider;
import com.treefinance.crawler.framework.extension.spider.page.SimplePage;
import com.treefinance.crawler.plugin.util.HttpHelper;
import com.treefinance.crawler.plugin.util.HttpSender;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * @author Jerry
 * @since 19:55 28/12/2017
 */
public abstract class AppSpider extends BaseSpider {

    private static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_2 like Mac OS X) AppleWebKit/604.4.7 (KHTML, like Gecko) Mobile/15C114GCanvas/ AliApp(TB/7.2.1) WindVane/8.3.0 750x1334";
    private HttpSender sender;
    private String[]   domains;

    public AppSpider(String... domains) {
        this.domains = domains;
    }

    protected HttpSender getSender() {
        return sender;
    }

    @Override
    public void run() throws InterruptedException {
        Preconditions.notNull("context", getContext());
        Map<String, String> cookies = ProcessorContextUtil.getCookieMap(getContext());
        if (MapUtils.isEmpty(cookies)) {
            logger.warn("Warn! Warn! Warn! Cookies must not be empty!");
            return;
        }

        init(cookies);

        try {
            process();
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Something is wrong when invoking custom extensional spider!", e);
        } finally {
            if (completed()) {
                destroy();
            }
        }
    }

    protected void init(Map<String, String> cookies) {
        String proxyAddr = null;
        try {
            Proxy proxy;
            if (getProxyManager() != null) {
                proxy = getProxyManager().getProxy();
            } else {
                proxy = getContext().getProxy();
            }

            if (proxy != null) {
                proxyAddr = proxy.format();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Use proxy for app spider : {}", proxyAddr);

        BasicCookieStore cookieStore = HttpHelper.createCookieStore(cookies, this.domains);

        this.sender = new HttpSender(cookieStore, USER_AGENT, proxyAddr);
    }

    protected void destroy() {
        sender.close();
    }

    protected boolean completed() {
        return true;
    }

    protected abstract void process() throws Exception;

    protected String sendRequest(String url) throws IOException {
        return sendRequest(url, null);
    }

    protected String sendRequest(String url, String referer) throws IOException {
        return sender.send(url, referer);
    }

    protected static String getCToken(CookieStore cookieStore) {
        return HttpHelper.getCookieValue(cookieStore, "ctoken", true);
    }

    protected void extractPageContent(String url, String content) {
        if (logger.isDebugEnabled()) {
            logger.debug("Url >>> {}", url);
            logger.debug("PageContent >>> {}", content);
        }
        if (getPageProcessor() != null) {
            getPageProcessor().process(new SimplePage(url, content, "EcommerceData"));
        }
    }
}
