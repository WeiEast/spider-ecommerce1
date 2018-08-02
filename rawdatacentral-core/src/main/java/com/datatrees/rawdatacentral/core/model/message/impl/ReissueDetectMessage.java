/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.core.model.message.impl;

import java.util.List;

import com.datatrees.rawdatacentral.core.model.MailBill;
import com.datatrees.spider.share.domain.MessageInfo;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月18日 上午10:41:53
 */
public class ReissueDetectMessage extends MessageInfo {

    private int            userId;

    private String         bankBillsKey;

    private List<MailBill> mailBills;

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the bankBillsKey
     */
    public String getBankBillsKey() {
        return bankBillsKey;
    }

    /**
     * @param bankBillsKey the bankBillsKey to set
     */
    public void setBankBillsKey(String bankBillsKey) {
        this.bankBillsKey = bankBillsKey;
    }

    /**
     * @return the mailBills
     */
    public List<MailBill> getMailBills() {
        return mailBills;
    }

    /**
     * @param mailBills the mailBills to set
     */
    public void setMailBills(List<MailBill> mailBills) {
        this.mailBills = mailBills;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ReissueDetectMessage [userId=" + userId + ", bankBillsKey=" + bankBillsKey + ", mailBills=" + mailBills + "]";
    }

}
