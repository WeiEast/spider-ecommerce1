/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.data;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月26日 下午1:37:33
 */
@SuppressWarnings("serial")
public class OperatorData extends AbstractData {

    public static String OPERATORID = "operatorid";

    public Integer getOperatorId() {
        return (Integer) this.get(OPERATORID);
    }

    public void setOperatorId(int operatorId) {
        this.put(OPERATORID, operatorId);
    }
}
