package com.datatrees.rawdatacentral.domain.model;

import java.io.Serializable;

public class WebsiteInfoWithBLOBs extends WebsiteInfo implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column website_info.search_config
     *
     * @mbg.generated
     */
    private String searchConfig;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column website_info.extractor_config
     *
     * @mbg.generated
     */
    private String extractorConfig;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table website_info
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column website_info.search_config
     *
     * @return the value of website_info.search_config
     *
     * @mbg.generated
     */
    public String getSearchConfig() {
        return searchConfig;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column website_info.search_config
     *
     * @param searchConfig the value for website_info.search_config
     *
     * @mbg.generated
     */
    public void setSearchConfig(String searchConfig) {
        this.searchConfig = searchConfig == null ? null : searchConfig.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column website_info.extractor_config
     *
     * @return the value of website_info.extractor_config
     *
     * @mbg.generated
     */
    public String getExtractorConfig() {
        return extractorConfig;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column website_info.extractor_config
     *
     * @param extractorConfig the value for website_info.extractor_config
     *
     * @mbg.generated
     */
    public void setExtractorConfig(String extractorConfig) {
        this.extractorConfig = extractorConfig == null ? null : extractorConfig.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table website_info
     *
     * @mbg.generated
     */
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