/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.core.model;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月18日 下午6:06:52
 */
public class MailBill {

    private String mailId;
    private int    billId;

    /**
     *
     */
    public MailBill() {
        super();
    }

    /**
     * @param mailId
     * @param billId
     */
    public MailBill(String mailId, int billId) {
        super();
        this.mailId = mailId;
        this.billId = billId;
    }

    /**
     * @return the mailId
     */
    public String getMailId() {
        return mailId;
    }

    /**
     * @param mailId the mailId to set
     */
    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    /**
     * @return the billId
     */
    public int getBillId() {
        return billId;
    }

    /**
     * @param billId the billId to set
     */
    public void setBillId(int billId) {
        this.billId = billId;
    }
}
