package com.datatrees.crawler.core.processor.crawler.operator.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * 通话记录信息
 * @author likun
 * @version $Id: CallDetail.java, v 0.1 Jul 18, 2015 5:46:51 PM likun Exp $
 */
public class CallDetail implements Serializable {

    private static final long serialVersionUID = -8262996250997753864L;
    /**
     * 序号
     */
    private String serialNum;
    /**
     * 业务类型，表示通话执行的套餐项目
     */
    private String businessType;
    /**
     * 通话起始时间
     */
    private Date   callStartDateTime;
    /**
     * 通话时长：数据格式 - ([0-9]*时)?([0-9]*分)?([0-9]*秒)?
     */
    private String callDuration;
    /**
     * 呼叫类型，1-主叫；2-被叫 ；3-呼叫转移；
     */
    private String callType;
    /**
     * 对方号码
     */
    private String otherTelNum;
    /**
     * 通话地点
     */
    private String callLocation;
    /**
     * 通话类型，本地通话或者漫游通话
     */
    private String callTypeDetail;
    /**
     * 基本通话费
     */
    private Double baseFee;
    /**
     * 漫游通话费
     */
    private Double roamingFee;
    /**
     * 长途通话费
     */
    private Double landFee;
    /**
     * 其他费
     */
    private Double otherFee;
    /**
     * 通话费合计
     */
    private Double totalFee;

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Date getCallStartDateTime() {
        return callStartDateTime;
    }

    public void setCallStartDateTime(Date callStartDateTime) {
        this.callStartDateTime = callStartDateTime;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getOtherTelNum() {
        return otherTelNum;
    }

    public void setOtherTelNum(String otherTelNum) {
        this.otherTelNum = otherTelNum;
    }

    public String getCallLocation() {
        return callLocation;
    }

    public void setCallLocation(String callLocation) {
        this.callLocation = callLocation;
    }

    public String getCallTypeDetail() {
        return callTypeDetail;
    }

    public void setCallTypeDetail(String callTypeDetail) {
        this.callTypeDetail = callTypeDetail;
    }

    public Double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(Double baseFee) {
        this.baseFee = baseFee;
    }

    public Double getRoamingFee() {
        return roamingFee;
    }

    public void setRoamingFee(Double roamingFee) {
        this.roamingFee = roamingFee;
    }

    public Double getLandFee() {
        return landFee;
    }

    public void setLandFee(Double landFee) {
        this.landFee = landFee;
    }

    public Double getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(Double otherFee) {
        this.otherFee = otherFee;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
