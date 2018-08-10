
package com.datatrees.spider.share.service.domain.data;

import java.util.Date;

import com.datatrees.spider.share.domain.AbstractData;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月26日 下午1:32:20
 */
public class MailBillData extends AbstractData {

    public static final String SENDER     = "sender";

    public static final String SUBJECT    = "subject";

    public static final String RECEIVED   = "receiveAt";

    public static final String RECEIVER   = "receiver";

    public static final String MAILHEADER = "mailHeader";

    public static final String FIRSTHAND  = "firstHand";

    public static final String FOLDER     = "folder";

    public static final String BANKID     = "bankid";

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
