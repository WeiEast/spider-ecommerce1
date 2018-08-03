/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.bank.domain;

import com.datatrees.spider.share.domain.AbstractData;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月26日 下午1:32:20
 */
@SuppressWarnings({"serial", "unchecked"})
public class EBankData extends AbstractData {

    public static String BANKID = "bankid";

    public Integer getBankId() {
        return (Integer) this.get(BANKID);
    }

    public void setBankId(int bankid) {
        this.put(BANKID, bankid);
    }

}
