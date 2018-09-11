/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.bank.domain.model;

import java.util.Date;

import com.datatrees.spider.share.domain.AbstractExtractResult;

/**
 * @author <A HREF="">Cheng Wang</A>
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
