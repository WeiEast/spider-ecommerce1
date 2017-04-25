package com.datatrees.rawdatacentral.collector.listener.message;

public class BusinessDetectMessage {
    private String websiteName;
    private String businessType;
    private String keyword;
    private String entryName;
    private String disContainKey;
    private String containKey;
    private String guid;
    private Integer userId;
    // private String qqAccount;

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }



    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDisContainKey() {
        return disContainKey;
    }

    public void setDisContainKey(String disContainKey) {
        this.disContainKey = disContainKey;
    }

    public String getContainKey() {
        return containKey;
    }

    public void setContainKey(String containKey) {
        this.containKey = containKey;
    }



    /**
     * @return the guid
     */
    public String getGuid() {
        return guid;
    }

    /**
     * @param guid the guid to set
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    // /**
    // * @return the qqAccount
    // */
    // public String getQqAccount() {
    // return qqAccount;
    // }
    //
    // /**
    // * @param qqAccount the qqAccount to set
    // */
    // public void setQqAccount(String qqAccount) {
    // this.qqAccount = qqAccount;
    // }

    @Override
    public String toString() {
        return "BusinessDetectMessage [websiteName=" + websiteName + ", businessType=" + businessType + ", keyword=" + keyword + ", entryName="
                + entryName + ", disContainKey=" + disContainKey + ", containKey=" + containKey + ", guid=" + guid + ", userId=" + userId + "]";
    }

}
