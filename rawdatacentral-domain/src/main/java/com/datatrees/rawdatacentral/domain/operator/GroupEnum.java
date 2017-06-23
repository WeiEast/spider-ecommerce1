/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.domain.operator;

/**
 * 运营商
 * Created by zhouxinghai on 2017/6/22
 */
public enum GroupEnum {


    ZHE_JIANG_10086("ZHE_JAING_10086","浙江移动","zhe_jiang_10086_shop"),
    ZHE_JIANG_10000("ZHE_JAING_10000","浙江电信","zhe_jiang_10000_shop");



    /**
     * 运营商类别代码
     */
    private final String groopCode;

    /**
     * 运营商类别名称
     */
    private final String groupName;

    /**
     * 默认站点名称
     */
    private final String websiteName;

    GroupEnum(String groopCode, String groupName, String websiteName) {
        this.groopCode = groopCode;
        this.groupName = groupName;
        this.websiteName = websiteName;
    }

    public String getGroopCode() {
        return groopCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getWebsiteName() {
        return websiteName;
    }
}
