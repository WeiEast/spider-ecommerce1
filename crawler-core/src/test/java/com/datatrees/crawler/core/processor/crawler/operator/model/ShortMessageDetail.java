package com.datatrees.crawler.core.processor.crawler.operator.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * 短信记录信息
 * @author likun
 * @version $Id: ShortMessageDetail.java, v 0.1 Jul 18, 2015 5:47:21 PM likun Exp $
 */
public class ShortMessageDetail implements Serializable {

    private static final long serialVersionUID = -4001240239142312790L;
    /**
     * 序号
     */
    private Integer serialNum;
    /**
     * 业务类型，表示短信指向的套餐项目
     */
    private String  businessType;
    /**
     * 起始时间
     */
    private Date    smsDateTime;
    /**
     * 短信收发类型, 1-接受；2-发送
     */
    private String  smsType;
    /**
     * 对方号码
     */
    private String  otherNum;
    /**
     * 费用
     */
    private Double  fee;

    public Integer getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(Integer serialNum) {
        this.serialNum = serialNum;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Date getSmsDateTime() {
        return smsDateTime;
    }

    public void setSmsDateTime(Date smsDateTime) {
        this.smsDateTime = smsDateTime;
    }

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getOtherNum() {
        return otherNum;
    }

    public void setOtherNum(String otherNum) {
        this.otherNum = otherNum;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
