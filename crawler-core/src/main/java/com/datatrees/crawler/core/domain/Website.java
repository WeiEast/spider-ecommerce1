package com.datatrees.crawler.core.domain;

import java.io.Serializable;

import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.google.gson.annotations.SerializedName;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 13, 2014 2:47:02 PM
 */
public class Website implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2630430042887742711L;
    private           Integer         id;
    private           String          websiteName;
    @Deprecated
    private           String          websiteDomain;
    private           String          websiteType;
    private           Boolean         isEnabled;
    private           SearchConfig    searchConfig;
    @SerializedName("searchconfig")
    private transient String          searchConfigSource;
    private           ExtractorConfig extractorConfig;
    @SerializedName("extractorconfig")
    private transient String          extractorConfigSource;
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

    /**
     * @return the isEnabled
     */
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * @param isEnabled the isEnabled to set
     */
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * @return the searchConfig
     */
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    /**
     * @param searchConfig the searchConfig to set
     */
    public void setSearchConfig(SearchConfig searchConfig) {
        this.searchConfig = searchConfig;
    }

    /**
     * @return the searchConfigSource
     */
    public String getSearchConfigSource() {
        return searchConfigSource;
    }

    /**
     * @param searchConfigSource the searchConfigSource to set
     */
    public void setSearchConfigSource(String searchConfigSource) {
        this.searchConfigSource = searchConfigSource;
    }

    /**
     * @return the extractorConfig
     */
    public ExtractorConfig getExtractorConfig() {
        return extractorConfig;
    }

    /**
     * @param extractorConfig the extractorConfig to set
     */
    public void setExtractorConfig(ExtractorConfig extractorConfig) {
        this.extractorConfig = extractorConfig;
    }

    /**
     * @return the extractorConfigSource
     */
    public String getExtractorConfigSource() {
        return extractorConfigSource;
    }

    /**
     * @param extractorConfigSource the extractorConfigSource to set
     */
    public void setExtractorConfigSource(String extractorConfigSource) {
        this.extractorConfigSource = extractorConfigSource;
    }

    public String getTaskRegion() {
        return taskRegion;
    }

    public void setTaskRegion(String taskRegion) {
        this.taskRegion = taskRegion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Website [id=" + id + ", websiteName=" + websiteName + ", websiteDomain=" + websiteDomain + ", websiteType=" + websiteType + ", isEnabled=" + isEnabled + "]";
    }

}
