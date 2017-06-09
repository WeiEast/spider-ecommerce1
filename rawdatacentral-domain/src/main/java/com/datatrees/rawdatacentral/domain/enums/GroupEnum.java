/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.domain.enums;


/**
 *
 * @author  <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since   2017年6月8日 下午5:33:59 
 */
public enum GroupEnum {
                       ZHEJIANG10086("浙江移动", "zj_10086"), ZHEJIANG10000("浙江电信", "zj_10000");
    String groupName;

    String groupId;

    GroupEnum(String groupName, String groupId) {
        this.groupName = groupName;
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

}
