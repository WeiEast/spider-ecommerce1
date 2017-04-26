/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.domain.model;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月27日 下午7:17:49 
 */
public class Keyword {
    
    private int id;
    private String keyword;
    private boolean isEnabled;
    private String keywordType;
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }
    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    /**
     * @return the isEnabled
     */
    public boolean isEnabled() {
        return isEnabled;
    }
    /**
     * @param isEnabled the isEnabled to set
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    /**
     * @return the keywordType
     */
    public String getKeywordType() {
        return keywordType;
    }
    /**
     * @param keywordType the keywordType to set
     */
    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
    }

}
