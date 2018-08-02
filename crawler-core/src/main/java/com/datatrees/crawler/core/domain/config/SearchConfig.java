package com.datatrees.crawler.core.domain.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.domain.config.filter.UrlFilter;
import com.datatrees.crawler.core.domain.config.login.LoginConfig;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.parser.Parser;
import com.datatrees.crawler.core.domain.config.properties.Properties;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.domain.config.service.impl.GrabService;
import com.datatrees.crawler.core.domain.config.service.impl.PluginService;
import com.datatrees.crawler.core.domain.config.service.impl.TaskHttpService;
import com.datatrees.crawler.core.util.xml.annotation.ChildTag;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 3:14:15 PM
 */
@Tag("config")
@Path("/config")
public class SearchConfig extends AbstractWebsiteConfig {

    /**
     *
     */
    private static final long                       serialVersionUID = -3594853402134944912L;

    private              List<String>               protocolTypeList;

    private              List<UrlFilter>            urlFilterList;

    private              Properties                 properties;

    private              List<AbstractService>      serviceList;

    private              List<Parser>               parserList;

    private              List<Page>                 pageList;

    private              List<SearchTemplateConfig> searchTemplateConfigList;

    private              LoginConfig                loginConfig;

    private              List<String>               resultTagList;

    public SearchConfig() {
        super();
        protocolTypeList = new ArrayList<String>();
        urlFilterList = new ArrayList<UrlFilter>();
        serviceList = new ArrayList<AbstractService>();
        parserList = new ArrayList<Parser>();
        pageList = new ArrayList<Page>();
        searchTemplateConfigList = new ArrayList<SearchTemplateConfig>();
        resultTagList = new ArrayList<String>();
    }

    @ChildTag("result-tag-list/result-tag")
    public List<String> getResultTagList() {
        return Collections.unmodifiableList(resultTagList);
    }

    @Node("result-tag-list/result-tag/text()")
    public void setResultTagList(String resultTag) {
        this.resultTagList.add(resultTag);
    }

    @ChildTag("protocol-type-list/protocol-type")
    public List<String> getProtocolTypeList() {
        return Collections.unmodifiableList(protocolTypeList);
    }

    @Node("protocol-type-list/protocol-type/text()")
    public void setProtocolTypeList(String protocolTypeList) {
        this.protocolTypeList.add(protocolTypeList);
    }

    @Tag("url-filters")
    public List<UrlFilter> getUrlFilterList() {
        return Collections.unmodifiableList(urlFilterList);
    }

    @Node("url-filters/url-filter")
    public void setUrlFilterList(UrlFilter urlFilter) {
        this.urlFilterList.add(urlFilter);
    }

    @Tag("properties")
    public Properties getProperties() {
        return properties;
    }

    @Node("properties")
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Tag("service-definition")
    public List<AbstractService> getServiceList() {
        return Collections.unmodifiableList(serviceList);
    }

    @Node(value = "service-definition/service", types = {PluginService.class, GrabService.class, TaskHttpService.class}, registered = true)
    public void setServiceList(AbstractService service) {
        this.serviceList.add(service);
    }

    @Tag("parser-definition")
    public List<Parser> getParserList() {
        return Collections.unmodifiableList(parserList);
    }

    @Node(value = "parser-definition/parser", registered = true)
    public void setParserList(Parser parser) {
        this.parserList.add(parser);
    }

    @Tag("page-definition")
    public List<Page> getPageList() {
        return Collections.unmodifiableList(pageList);
    }

    @Node(value = "page-definition/page", registered = true)
    public void setPageList(Page page) {
        this.pageList.add(page);
    }

    @Tag("search")
    public List<SearchTemplateConfig> getSearchTemplateConfigList() {
        return Collections.unmodifiableList(searchTemplateConfigList);
    }

    @Node("search/search-template")
    public void setSearchTemplateConfigList(SearchTemplateConfig searchTemplateConfig) {
        this.searchTemplateConfigList.add(searchTemplateConfig);
    }

    @Tag("login")
    public LoginConfig getLoginConfig() {
        return loginConfig;
    }

    @Node("login")
    public void setLoginConfig(LoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }

    public void clone(SearchConfig cloneFrom) {
        super.clone(cloneFrom);
        if (CollectionUtils.isEmpty(protocolTypeList)) {
            this.protocolTypeList.addAll(cloneFrom.getProtocolTypeList());
        }
        if (CollectionUtils.isEmpty(urlFilterList)) {
            this.urlFilterList.addAll(cloneFrom.getUrlFilterList());
        }
        if (properties == null) {
            this.properties = cloneFrom.getProperties();
        }
        if (CollectionUtils.isEmpty(serviceList)) {
            this.serviceList.addAll(cloneFrom.getServiceList());
        }
        if (CollectionUtils.isEmpty(parserList)) {
            this.parserList.addAll(cloneFrom.getParserList());
        }
        if (CollectionUtils.isEmpty(pageList)) {
            this.pageList.addAll(cloneFrom.getPageList());
        }
        if (CollectionUtils.isEmpty(searchTemplateConfigList)) {
            this.searchTemplateConfigList.addAll(cloneFrom.getSearchTemplateConfigList());
        }
        if (loginConfig == null) {
            this.loginConfig = cloneFrom.getLoginConfig();
        }
        if (CollectionUtils.isEmpty(resultTagList)) {
            this.resultTagList.addAll(cloneFrom.getResultTagList());
        }
    }

}
