/**
 * www.gf-dai.com.cn
 * Copyright (c) 2015 All Rights Reserved.
 */

package com.datatrees.crawler.core.processor.crawler.operator.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * 月账单信息
 * @author likun
 * @version $Id: BillDetail.java, v 0.1 Jul 18, 2015 4:37:15 PM likun Exp $
 */
public class BillDetail implements Serializable {

    private static final long serialVersionUID = 5923546344368948356L;
    /**
     * 账单月
     */
    private Date   billMonth;
    /**
     * 月基本费
     */
    private Double baseFee;
    /**
     * 语音通话费
     */
    private Double voiceCallFee;
    /**
     * 上网费
     */
    private Double networkFee;
    /**
     * 本月消费合计
     */
    private Double totalFee;
    /**
     * 本月抵扣合计
     */
    private Double deductionFee;
    /**
     * 实际应缴合计
     */
    private Double payFee;
    /**
     * 姓名
     */
    private String name;

    public Date getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(Date billMonth) {
        this.billMonth = billMonth;
    }

    public Double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(Double baseFee) {
        this.baseFee = baseFee;
    }

    public Double getVoiceCallFee() {
        return voiceCallFee;
    }

    public void setVoiceCallFee(Double voiceCallFee) {
        this.voiceCallFee = voiceCallFee;
    }

    public Double getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(Double networkFee) {
        this.networkFee = networkFee;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    public Double getDeductionFee() {
        return deductionFee;
    }

    public void setDeductionFee(Double deductionFee) {
        this.deductionFee = deductionFee;
    }

    public Double getPayFee() {
        return payFee;
    }

    public void setPayFee(Double payFee) {
        this.payFee = payFee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
