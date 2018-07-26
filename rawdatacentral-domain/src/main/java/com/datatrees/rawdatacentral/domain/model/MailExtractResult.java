/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.domain.model;

import java.util.Date;

import com.datatrees.spider.share.domain.AbstractExtractResult;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月27日 下午2:52:15
 */
public class MailExtractResult extends AbstractExtractResult {

    private int     bankId;

    private String  sender;

    private String  subject;

    private Date    receiveAt;

    private String  receiver;

    private Boolean firstHand;

    private String  mailHeader;//邮件头

    /**
     * @return the firstHand
     */
    public Boolean getFirstHand() {
        return firstHand;
    }

    /**
     * @param firstHand the firstHand to set
     */
    public void setFirstHand(Boolean firstHand) {
        this.firstHand = firstHand;
    }

    /**
     * @return the bankId
     */
    public int getBankId() {
        return bankId;
    }

    /**
     * @param bankId the bankId to set
     */
    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the receiveAt
     */
    public Date getReceiveAt() {
        return receiveAt;
    }

    /**
     * @param receiveAt the receiveAt to set
     */
    public void setReceiveAt(Date receiveAt) {
        this.receiveAt = receiveAt;
    }

    /**
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMailHeader() {
        return mailHeader;
    }

    public void setMailHeader(String mailHeader) {
        this.mailHeader = mailHeader;
    }
}
