package com.datatrees.crawler.core.domain;

import java.io.Serializable;

import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 13, 2014 2:47:02 PM
 */
public class Website implements Serializable {

    private           Integer         id;
    private           String          websiteName;
    private           String          websiteDomain;
    private           String          websiteType;
    private           Boolean         isEnabled;
    private transient SearchConfig    searchConfig;
    private           String          searchConfigSource;
    private transient ExtractorConfig extractorConfig;
    private           String          extractorConfigSource;
    private           String          taskRegion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteDomain() {
        return websiteDomain;
    }

    public void setWebsiteDomain(String websiteDomain) {
        this.websiteDomain = websiteDomain;
    }

    public String getWebsiteType() {
        return websiteType;
    }

    public void setWebsiteType(String websiteType) {
        this.websiteType = websiteType;
    }

    public Boolean getIdEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(SearchConfig searchConfig) {
        this.searchConfig = searchConfig;
    }

    public String getSearchConfigSource() {
        return searchConfigSource;
    }

    public void setSearchConfigSource(String searchConfigSource) {
        this.searchConfigSource = searchConfigSource;
    }

    public ExtractorConfig getExtractorConfig() {
        return extractorConfig;
    }

    public void setExtractorConfig(ExtractorConfig extractorConfig) {
        this.extractorConfig = extractorConfig;
    }

    public String getExtractorConfigSource() {
        return extractorConfigSource;
    }

    public void setExtractorConfigSource(String extractorConfigSource) {
        this.extractorConfigSource = extractorConfigSource;
    }

    public String getTaskRegion() {
        return taskRegion;
    }

    public void setTaskRegion(String taskRegion) {
        this.taskRegion = taskRegion;
    }

}
