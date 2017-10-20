/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor;

import java.util.*;

import com.datatrees.common.util.PatternUtils;
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
import com.datatrees.crawler.core.processor.common.resource.LoginResource;
import com.datatrees.crawler.core.processor.common.resource.ProxyManager;
import com.datatrees.crawler.core.processor.login.Login;
import com.datatrees.crawler.core.processor.page.DummyPage;
import com.datatrees.webrobot.driver.WebRobotClientDriver;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 2:14:19 PM
 */
public class SearchProcessorContext extends AbstractProcessorContext {

    private static final Logger                                                            log                         = LoggerFactory
            .getLogger(SearchProcessorContext.class);
    private final        Map<SearchTemplateConfig, Map<Integer, List<SearchSequenceUnit>>> depthPageMap                = new HashMap<>();
    private final        Map<SearchTemplateConfig, Map<String, SearchSequenceUnit>>        pathPageMap                 = new HashMap<>();
    private final        Map<String, SearchTemplateConfig>                                 searchTemplateConfigMap     = new HashMap<>();
    private final        Map<SearchType, List<SearchTemplateConfig>>                       searchTemplateConfigListMap = new HashMap<>();
    // page id ===> page
    private final        Map<String, Page>                                                 pageMap                     = new HashMap<String, Page>();
    private ProxyManager   proxyManager;
    private LoginResource  loginResource;
    private String         webServiceUrl;
    private Proxy          proxyConf;
    private AbstractCookie cookieConf;
    private Login.Status   status;
    private Map<String, String> defaultHeader     = new HashMap<String, String>();
    private Map<Page, Integer>  pageVisitCountMap = new HashMap<Page, Integer>();
    private boolean              loginCheckIgnore;
    private WebRobotClientDriver webRobotClientDriver;

    /**
     * @param website
     */
    public SearchProcessorContext(Website website) {
        super(website);
        Preconditions.checkNotNull(website.getSearchConfig(), "website search config should not be empty!");
    }

    public void release() {
        // proxy release
        if (proxyManager != null && proxyConf != null && proxyConf.getScope().equals(Scope.SESSION)) {
            try {
                // mark sessionproxy
                getProcessorResult().put("sessionProxy", "" + proxyManager.getProxy());
                proxyManager.release();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (webRobotClientDriver != null) {
            try {
                log.info("try to release webRobotClientDriver ...");
                webRobotClientDriver.release();
            } catch (Exception e) {
                log.warn("release webRobot error " + e.getMessage());
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
            if (searchTemplateConfigListMap.containsKey(taskType)) {
                searchTemplateConfigListMap.get(taskType).add(searchTemplateConfig);
            } else {
                List<SearchTemplateConfig> templateConfigList = new ArrayList<SearchTemplateConfig>();
                templateConfigList.add(searchTemplateConfig);
                searchTemplateConfigListMap.put(taskType, templateConfigList);
            }

            Map<String, SearchSequenceUnit> pathPageMaps = new HashMap<String, SearchSequenceUnit>();
            Map<Integer, List<SearchSequenceUnit>> depthPageMaps = new HashMap<Integer, List<SearchSequenceUnit>>();
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
                List<SearchSequenceUnit> pages = depthPageMaps.get(depth);
                if (pages == null) {
                    pages = new ArrayList<SearchSequenceUnit>();
                    depthPageMaps.put(depth, pages);
                }
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
        Properties propeties = getSearchConfig().getProperties();
        if (propeties != null) {
            proxyConf = propeties.getProxy();
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

    public SearchConfig getSearchConfig() {
        return website.getSearchConfig();
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

    public Page getPageDefination(LinkNode url, String templateId) {
        Page page = null;
        SearchTemplateConfig stc = getSearchTempldateConfig(templateId);
        if (stc != null) {
            if (CollectionUtils.isNotEmpty(stc.getSearchSequence())) {
                Map<String, SearchSequenceUnit> urlPathMap = pathPageMap.get(stc);
                if (MapUtils.isNotEmpty(urlPathMap)) {
                    Iterator<String> pathIterator = urlPathMap.keySet().iterator();
                    while (pathIterator.hasNext()) {
                        String key = pathIterator.next();
                        if (StringUtils.isNotEmpty(key) && PatternUtils.match(key, url.getUrl())) {
                            page = urlPathMap.get(key).getPage();
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
            log.info("page " + page + " reach the max visitCount " + page.getMaxPageCount() + ", return empty");
            return null;
        }
        return page;
    }

    public void adjustUrlDepth(LinkNode curr, String templateId, int parent) {
        String url = curr.getUrl();
        int result = parent + 1;
        SearchTemplateConfig stc = getSearchTempldateConfig(templateId);
        if (stc != null) {
            // just for keyword search
            if (CollectionUtils.isNotEmpty(stc.getSearchSequence())) {
                Map<String, SearchSequenceUnit> urlPathMap = pathPageMap.get(stc);
                Iterator<String> pathIterator = urlPathMap.keySet().iterator();
                while (pathIterator.hasNext()) {
                    String key = pathIterator.next();
                    if (PatternUtils.match(key, url)) {
                        SearchSequenceUnit unit = urlPathMap.get(key);
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
            log.warn("not find SearchTemplateConfig for id:" + templateId);
        }
        curr.setDepth(result);
    }

    public boolean needProxy() {
        return proxyConf != null;
    }

    public boolean needProxyByUrl(String url) {
        boolean needProxy = false;
        if (null == proxyConf || StringUtils.isEmpty(url)) {
            return needProxy;
        }

        String pattern = proxyConf.getPattern();
        if (StringUtils.isEmpty(pattern)) {
            // If not, will maintain the original logic
            needProxy = (proxyConf.getProxy() != null);
            log.info(" needProxy :  " + needProxy + "  url : " + url + "  regex : " + pattern + " proxyConf.getProxy()  : " + proxyConf.getProxy());
            return needProxy;
        } else {
            try {
                if (PatternUtils.match(pattern, url)) {
                    needProxy = true;
                    log.info(" needProxy :  " + needProxy + "  url : " + url + "  regex : " + pattern);
                }
            } catch (Exception e) {
                // non standard URL
                log.warn("Get urlDomain has encountered a problem ,url : " + url + "  Exception message  :" + e.getMessage());
            }
        }
        return needProxy;
    }

    public SearchTemplateConfig getSearchTempldateConfig(String id) {
        SearchTemplateConfig stc = searchTemplateConfigMap.get(id);
        return stc;
    }

    public List<SearchTemplateConfig> getSearchTempldateConfigList(SearchType taskType) {
        List<SearchTemplateConfig> searchTemplateConfigs = searchTemplateConfigListMap.get(taskType);
        return searchTemplateConfigs == null ? Collections.emptyList() : searchTemplateConfigs;
    }

    /**
     * @return the searchTemplateConfigListMap
     */
    public Map<SearchType, List<SearchTemplateConfig>> getSearchTemplateConfigListMap() {
        return searchTemplateConfigListMap;
    }

    public Set<String> getPageIdMap(String templateId) {
        Set<String> pidSet = new HashSet<String>();
        SearchTemplateConfig stc = getSearchTempldateConfig(templateId);
        if (stc != null) {
            List<SearchSequenceUnit> sunits = stc.getSearchSequence();
            if (CollectionUtils.isNotEmpty(sunits)) {
                for (SearchSequenceUnit searchSequenceUnit : sunits) {
                    pidSet.add(searchSequenceUnit.getPage().getId());
                }
            }
        }
        return pidSet;
    }

    /**
     * @return the webServiceUrl
     */
    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    /**
     * @param webServiceUrl the webServiceUrl to set
     */
    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public boolean needDecoder() {
        boolean result = false;
        Properties properties = this.getSearchConfig().getProperties();
        if (properties != null) {
            UnicodeMode unicodeMode = properties.getUnicodeMode();
            if (unicodeMode != null) {
                result = true;
            }
        }
        return result;
    }

    public String getHttpClientType() {
        Properties properties = this.getSearchConfig().getProperties();
        if (properties != null) {
            return properties.getHttpClientType();
        }
        return null;
    }

    public Boolean getRedirectUriEscaped() {
        Properties properties = this.getSearchConfig().getProperties();
        if (properties != null) {
            return properties.getRedirectUriEscaped();
        }
        return null;
    }

    public Boolean getAllowCircularRedirects() {
        Properties properties = this.getSearchConfig().getProperties();
        if (properties != null) {
            return properties.getAllowCircularRedirects();
        }
        return null;
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
     * @return the searchTemplateConfigMap
     */
    public Map<String, SearchTemplateConfig> getSearchTemplateConfigMap() {
        return searchTemplateConfigMap;
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

    public Integer getCaptchaCode() {
        Properties properties = this.getSearchConfig().getProperties();
        if (properties != null) {
            return properties.getCaptchaCode();
        }
        return null;
    }

    /**
     * @return the webRobotClientDriver
     */
    public WebRobotClientDriver getWebRobotClientDriver() {
        return webRobotClientDriver;
    }

    /**
     * @param webRobotClientDriver the webRobotClientDriver to set
     */
    public void setWebRobotClientDriver(WebRobotClientDriver webRobotClientDriver) {
        this.webRobotClientDriver = webRobotClientDriver;
    }

}
