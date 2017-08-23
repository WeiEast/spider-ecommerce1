/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.data;

import java.util.Date;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月26日 下午1:32:20
 */
@SuppressWarnings({"serial", "unchecked"})
public class MailBillData extends AbstractData {

    public static String SENDER     = "sender";
    public static String SUBJECT    = "subject";
    public static String RECEIVED   = "receiveAt";
    public static String RECEIVER   = "receiver";
    public static String MAILHEADER = "mailHeader";
    public static String FIRSTHAND  = "firstHand";
    public static String FOLDER     = "folder";
    public static String BANKID     = "bankid";

    public Integer getBankId() {
        return (Integer) this.get(BANKID);
    }

    public void setBankId(int bankid) {
        this.put(BANKID, bankid);
    }

    public String getFolder() {
        return (String) this.get(FOLDER);
    }

    public void setFolder(String folder) {
        this.put(FOLDER, folder);
    }

    public Boolean getFirstHand() {
        return (Boolean) this.get(FIRSTHAND);
    }

    public void setFirstHand(Boolean isFirstHand) {
        this.put(FIRSTHAND, isFirstHand);
    }

    public String getMailHeader() {
        return (String) this.get(MAILHEADER);
    }

    public void setMailHeader(String mailHeader) {
        this.put(MAILHEADER, mailHeader);
    }

    public String getSender() {
        return (String) this.get(SENDER);
    }

    public void setSender(String sender) {
        this.put(SENDER, sender);
    }

    public String getSubject() {
        return (String) this.get(SUBJECT);

    }

    public void setSubject(String subject) {
        this.put(SUBJECT, subject);
    }

    public String getReceiver() {
        return (String) this.get(RECEIVER);

    }

    public void setReceiver(String receiver) {
        this.put(RECEIVER, receiver);
    }

    public Date getReceiveAt() {
        return (Date) this.get(RECEIVED);
    }

    public void setReceiveAt(Date receiveAt) {
        this.put(RECEIVED, receiveAt);
    }

}
