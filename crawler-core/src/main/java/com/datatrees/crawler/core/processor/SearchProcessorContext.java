/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor;

import java.util.*;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.domain.config.login.LoginConfig;
import com.datatrees.crawler.core.domain.config.login.LoginType;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.domain.config.properties.Properties;
import com.datatrees.crawler.core.domain.config.properties.Proxy;
import com.datatrees.crawler.core.domain.config.properties.Scope;
import com.datatrees.crawler.core.domain.config.properties.UnicodeMode;
import com.datatrees.crawler.core.domain.config.properties.cookie.AbstractCookie;
import com.datatrees.crawler.core.domain.config.search.SearchSequenceUnit;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.domain.config.search.SearchType;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.exception.NoProxyException;
import com.datatrees.crawler.core.processor.common.resource.LoginResource;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.login.Login;
import com.datatrees.crawler.core.processor.page.DummyPage;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 2:14:19 PM
 */
public class SearchProcessorContext extends AbstractProcessorContext {

    private final        Map<SearchTemplateConfig, Map<Integer, List<SearchSequenceUnit>>> depthPageMap                = new HashMap<>();
    private final        Map<SearchTemplateConfig, Map<String, SearchSequenceUnit>>        pathPageMap                 = new HashMap<>();
    private final        Map<String, SearchTemplateConfig>                                 searchTemplateConfigMap     = new HashMap<>();
    private final        Map<SearchType, List<SearchTemplateConfig>>                       searchTemplateConfigListMap = new HashMap<>();
    // page id ===> page
    private final        Map<String, Page>                                                 pageMap                     = new HashMap<>();
    private ProxyManager   proxyManager;
    private LoginResource  loginResource;
    private Proxy          proxyConf;
    private AbstractCookie cookieConf;
    private Login.Status   status;
    private Map<String, String> defaultHeader     = new HashMap<>();
    private Map<Page, Integer>  pageVisitCountMap = new HashMap<>();
    private boolean loginCheckIgnore;

    public SearchProcessorContext(Website website, Long taskId) {
        super(website, taskId);
        Preconditions.checkNotNull(website.getSearchConfig(), "website search config should not be empty!");
    }

    public void release() {
        // proxy release
        if (proxyManager != null && proxyConf != null && proxyConf.getScope().equals(Scope.SESSION)) {
            try {
                // mark sessionproxy
                getProcessorResult().put("sessionProxy", "" + getProxy());
                proxyManager.release();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     *
     */
    public void init() {
        // init search template map
        List<SearchTemplateConfig> searchTemplateConfigs = website.getSearchConfig().getSearchTemplateConfigList();
        for (SearchTemplateConfig searchTemplateConfig : searchTemplateConfigs) {
            searchTemplateConfigMap.put(searchTemplateConfig.getId(), searchTemplateConfig);
            SearchType taskType = searchTemplateConfig.getType();

            List<SearchTemplateConfig> configList = searchTemplateConfigListMap.computeIfAbsent(taskType, searchType -> new ArrayList<>());
            configList.add(searchTemplateConfig);

            Map<String, SearchSequenceUnit> pathPageMaps = new HashMap<>();
            Map<Integer, List<SearchSequenceUnit>> depthPageMaps = new HashMap<>();
            pathPageMap.put(searchTemplateConfig, pathPageMaps);
            depthPageMap.put(searchTemplateConfig, depthPageMaps);

            List<SearchSequenceUnit> searchSequenceUnits = searchTemplateConfig.getSearchSequence();

            for (SearchSequenceUnit searchSequenceUnit : searchSequenceUnits) {
                int depth = searchSequenceUnit.getDepth();
                Page pg = searchSequenceUnit.getPage();
                String path = (null == pg) ? null : pg.getPath();

                if (StringUtils.isNotEmpty(path)) {
                    pathPageMaps.put(path, searchSequenceUnit);
                }

                List<SearchSequenceUnit> pages = depthPageMaps.computeIfAbsent(depth, k -> new ArrayList<>());
                pages.add(searchSequenceUnit);
            }
        }

        // init page Map
        List<Page> pageList = website.getSearchConfig().getPageList();
        if (CollectionUtils.isNotEmpty(pageList)) {
            for (Page p : pageList) {
                pageMap.put(p.getId(), p);
            }
        }

        // init plugin
        List<AbstractPlugin> plugins = getSearchConfig().getPluginList();
        if (CollectionUtils.isNotEmpty(plugins)) {
            for (AbstractPlugin plugin : plugins) {
                pluginMaps.put(plugin.getId(), plugin);
            }
        }

        // init cookie conf
        try {
            cookieConf = website.getSearchConfig().getProperties().getCookie().clone();
        } catch (Exception e) {
            // ignore
        }

        //init proxy
        Properties properties = getSearchConfig().getProperties();
        if (properties != null) {
            proxyConf = properties.getProxy();
        }
    }

    /**
     * @return the status
     */
    public Login.Status getLoginStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setLoginStatus(Login.Status status) {
        this.status = status;
    }

    public AbstractCookie getCookieConf() {
        return cookieConf;
    }


    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    /**
     * wrapper for proxy manager with scope states
     * @param proxyManager
     */
    public void setProxyManager(ProxyManager proxyManager) {
        this.proxyManager = proxyManager;
    }

    public Page getPage(String pid) {
        return pageMap.get(pid);
    }

    public Page getPageDefinition(LinkNode url, String templateId) {
        Page page = null;
        SearchTemplateConfig stc = getSearchTemplateConfig(templateId);
        if (stc != null) {
            if (CollectionUtils.isNotEmpty(stc.getSearchSequence())) {
                Map<String, SearchSequenceUnit> urlPathMap = pathPageMap.get(stc);
                if (MapUtils.isNotEmpty(urlPathMap)) {
                    for (Map.Entry<String, SearchSequenceUnit> entry : urlPathMap.entrySet()) {
                        if (StringUtils.isNotEmpty(entry.getKey()) && RegExp.find(url.getUrl(), entry.getKey())) {
                            page = entry.getValue().getPage();
                            break;
                        }
                    }
                }
                if (page == null) {
                    List<SearchSequenceUnit> pageList = depthPageMap.get(stc).get(url.getDepth());
                    if (CollectionUtils.isNotEmpty(pageList)) {
                        for (SearchSequenceUnit sequence : pageList) {
                            Page tempPage = sequence.getPage();
                            if (tempPage.getService() == null) {
                                page = tempPage;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (page == null) {
            page = new DummyPage();
        }
        return this.pageVisitCountCheck(page);
    }

    private Page pageVisitCountCheck(Page page) {
        Integer count = pageVisitCountMap.get(page);
        if (count == null) {
            count = 1;
            pageVisitCountMap.put(page, count);
        } else {
            pageVisitCountMap.put(page, ++count);
        }
        if (page.getMaxPageCount() != null && page.getMaxPageCount() < count - 1) {
            logger.info("page " + page + " reach the max visitCount " + page.getMaxPageCount() + ", return empty");
            return null;
        }
        return page;
    }

    public void adjustUrlDepth(LinkNode curr, String templateId, int parent) {
        String url = curr.getUrl();
        int result = parent + 1;
        SearchTemplateConfig stc = getSearchTemplateConfig(templateId);
        if (stc != null) {
            // just for keyword search
            if (CollectionUtils.isNotEmpty(stc.getSearchSequence())) {
                Map<String, SearchSequenceUnit> urlPathMap = pathPageMap.get(stc);
                for (Map.Entry<String, SearchSequenceUnit> entry : urlPathMap.entrySet()) {
                    if (RegExp.find(url, entry.getKey())) {
                        SearchSequenceUnit unit = entry.getValue();
                        result = unit.getDepth();
                        curr.setpId(unit.getPage().getId());
                        break;
                    }
                }

                if (StringUtils.isEmpty(curr.getpId())) {
                    List<SearchSequenceUnit> units = depthPageMap.get(stc).get(result);
                    if (CollectionUtils.isNotEmpty(units)) {
                        curr.setpId(units.get(0).getPage().getId());
                    }
                }
            }
        } else {
            logger.warn("not find SearchTemplateConfig for id:" + templateId);
        }
        curr.setDepth(result);
    }

    public boolean needProxy() {
        return proxyConf != null || supportProxy();
    }

    private boolean supportProxy() {
        return taskId != null && ProxyUtils.getProxyEnable(taskId);
    }

    public boolean needProxyByUrl(String url) {
        if (supportProxy()) {
            return true;
        }

        if (null == proxyConf || StringUtils.isEmpty(url)) {
            return false;
        }

        String pattern = proxyConf.getPattern();

        if (logger.isDebugEnabled()) {
            logger.debug("Proxy-conf >>> pattern: {}, proxy: {}, url: {}", pattern, proxyConf.getProxy(), url);
        }

        if (StringUtils.isEmpty(pattern)) {
            // If not, will maintain the original logic
            return StringUtils.isNotBlank(proxyConf.getProxy());
        }

        try {
            return RegExp.find(url, pattern);
        } catch (Exception e) {
            logger.error("Unexpected exception!", e);
        }
        return false;
    }


    public com.datatrees.crawler.core.processor.proxy.Proxy getProxy(String url) throws Exception {
        return getProxy(url, false);
    }

    public com.datatrees.crawler.core.processor.proxy.Proxy getProxy(String url, boolean strict) throws Exception {
        if (needProxyByUrl(url)) {
            com.datatrees.crawler.core.processor.proxy.Proxy proxy = getProxy();

            if (proxy == null) {
                if (strict) throw new NoProxyException("Not found available proxy in remote server! >>> " + url);

                logger.warn("Not found available proxy in remote server! >>> " + url);
            }

            return proxy;
        }
        return null;
    }

    public com.datatrees.crawler.core.processor.proxy.Proxy getProxy() throws Exception {
        return getProxyManager().getProxy();
    }

    public SearchTemplateConfig getSearchTemplateConfig(String id) {
        return searchTemplateConfigMap.get(id);
    }

    /**
     * @see #isRedirectUriEscaped() ()
     */
    @Deprecated
    public Boolean getRedirectUriEscaped() {
        return isRedirectUriEscaped();
    }

    /**
     * @see #isAllowCircularRedirects()
     */
    @Deprecated
    public Boolean getAllowCircularRedirects() {
        return isAllowCircularRedirects();
    }

    /**
     * @return the loginResource
     */
    public LoginResource getLoginResource() {
        return loginResource;
    }

    /**
     * @param loginResource the loginResource to set
     */
    public void setLoginResource(LoginResource loginResource) {
        this.loginResource = loginResource;
    }

    public LoginConfig getLoginConfig() {
        return getSearchConfig().getLoginConfig();
    }

    public boolean needLogin() {
        return getLoginConfig() != null && getLoginConfig().getType() != LoginType.NONE && !loginCheckIgnore;
    }

    /**
     * 目前当LoginType为plugin时，一般都需要前后端交互
     * @return
     */
    public boolean needInteractive() {
        return needLogin() && getLoginConfig().getType() == LoginType.PLUGIN;
    }

    /**
     * @return the defaultHeader
     */
    public Map<String, String> getDefaultHeader() {
        return defaultHeader;
    }

    /**
     * @return the loginCheckIgnore
     */
    public boolean isLoginCheckIgnore() {
        return loginCheckIgnore;
    }

    /**
     * @param loginCheckIgnore the loginCheckIgnore to set
     */
    public void setLoginCheckIgnore(boolean loginCheckIgnore) {
        this.loginCheckIgnore = loginCheckIgnore;
    }

    /**
     * @return search config
     */
    public SearchConfig getSearchConfig() {
        return getWebsite().getSearchConfig();
    }

    /**
     * @return common properties for searching
     */
    public Properties getSearchProperties() {
        return getSearchConfig() != null ? getSearchConfig().getProperties() : null;
    }

    /**
     * @return immutable list of search template configuration.
     */
    public List<SearchTemplateConfig> getSearchTemplates() {
        SearchConfig searchConfig = getSearchConfig();
        if (searchConfig != null) {
            return ImmutableList.copyOf(searchConfig.getSearchTemplateConfigList());
        }

        return Collections.emptyList();
    }

    public UnicodeMode getUnicodeMode() {
        Properties properties = getSearchProperties();
        return properties != null ? properties.getUnicodeMode() : null;
    }

    public boolean isRedirectUriEscaped() {
        Properties properties = this.getSearchProperties();
        return properties != null && BooleanUtils.isTrue(properties.getRedirectUriEscaped());
    }

    public boolean isAllowCircularRedirects() {
        Properties properties = this.getSearchProperties();
        return properties != null && BooleanUtils.isTrue(properties.getAllowCircularRedirects());
    }

    public Integer getCaptchaCode() {
        Properties properties = this.getSearchProperties();
        if (properties != null) {
            return properties.getCaptchaCode();
        }
        return null;
    }

    public String getHttpClientType() {
        Properties properties = this.getSearchProperties();
        if (properties != null) {
            return properties.getHttpClientType();
        }
        return null;
    }
}
