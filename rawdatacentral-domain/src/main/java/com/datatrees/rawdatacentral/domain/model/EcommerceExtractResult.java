/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.domain.model;

import com.datatrees.rawdatacentral.domain.result.AbstractExtractResult;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月27日 下午7:02:27
 */
public class EcommerceExtractResult extends AbstractExtractResult {
    private int ecommerceId;

    /**
     * @return the ecommerceId
     */
    public int getEcommerceId() {
        return ecommerceId;
    }

    /**
     * @param ecommerceId the ecommerceId to set
     */
    public void setEcommerceId(int ecommerceId) {
        this.ecommerceId = ecommerceId;
    }

}
