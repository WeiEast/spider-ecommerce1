/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午10:12:20
 */
public enum ResultClassType {
    MAIL_BILL("Mail.Bill"),
    OPERATOR_PERSONALINFORMATION("Operator.PersonalInformation"),
    OPERATOR_BILLDETAIL("Operator.BillDetail"),
    OPERATOR_SHORTMESSAGEDETAIL("Operator.ShortMessageDetail"),
    OPERATOR_CALLDETAIL("Operator.CallDetail"),
    ECOMMERCE_BASEINFO("Ecommerce.BaseInfo"),
    ECOMMERCE_RECORDS("Ecommerce.Records"),
    ECOMMERCE_ADDRESSES("Ecommerce.Addresses"),
    ECOMMERCE_BANKCARDS("Ecommerce.BankCards"),
    ECOMMERCE_FEESACCOUNTS("Ecommerce.FeesAccounts");
    private static Map<String, ResultClassType> ResultTypeMap = new HashMap<String, ResultClassType>();

    static {
        for (ResultClassType obj : values()) {
            ResultTypeMap.put(obj.getValue(), obj);
        }
    }

    private final String value;

    ResultClassType(String value) {
        this.value = value;
    }

    public static ResultClassType getResultType(String value) {
        return ResultTypeMap.get(value);
    }

    public static Map<String, ResultClassType> getResultTypeMap() {
        return ResultTypeMap;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
