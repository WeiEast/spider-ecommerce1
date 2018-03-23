package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;

public class WebsiteInfoWithBLOBs extends WebsiteInfo implements Serializable {
    private String searchConfig;

    private String extractorConfig;

    private static final long serialVersionUID = 1L;

    public String getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(String searchConfig) {
        this.searchConfig = searchConfig == null ? null : searchConfig.trim();
    }

    public String getExtractorConfig() {
        return extractorConfig;
    }

    public void setExtractorConfig(String extractorConfig) {
        this.extractorConfig = extractorConfig == null ? null : extractorConfig.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", searchConfig=").append(searchConfig);
        sb.append(", extractorConfig=").append(extractorConfig);
        sb.append("]");
        return sb.toString();
    }
}