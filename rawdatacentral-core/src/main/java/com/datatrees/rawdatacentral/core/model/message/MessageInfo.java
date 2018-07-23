/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.core.model.message;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年4月27日 下午7:05:44
 */
public class MessageInfo {

    private int    reconsumeTimes;

    private String msgId;

    private long   bornTimestamp;

    /**
     * @return the msgId
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * @param msgId the msgId to set
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * @return the reconsumeTimes
     */
    public int getReconsumeTimes() {
        return reconsumeTimes;
    }

    /**
     * @param reconsumeTimes the reconsumeTimes to set
     */
    public void setReconsumeTimes(int reconsumeTimes) {
        this.reconsumeTimes = reconsumeTimes;
    }

    /**
     * @return the bornTimestamp
     */
    public long getBornTimestamp() {
        return bornTimestamp;
    }

    /**
     * @param bornTimestamp the bornTimestamp to set
     */
    public void setBornTimestamp(long bornTimestamp) {
        this.bornTimestamp = bornTimestamp;
    }

}
