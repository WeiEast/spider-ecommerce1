package com.datatrees.crawler.core.processor.crawler.operator.model;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by zhangke on 7/17/15.
 */
public class OperatorData implements Serializable {
    private static final long        serialVersionUID = -3376020743505858335L;

    /**
     * 通话详单
     */
    private List<CallDetail>         callDetails;

    /**
     * 短信详单
     */
    private List<ShortMessageDetail> shortMessageDetails;

    /**
     * 个人信息
     */
    private PersonalInformation      personalInformation;

    /**
     * 账单
     */
    private List<BillDetail>         billDetails;

    public List<CallDetail> getCallDetails() {
        return callDetails;
    }

    public void setCallDetails(List<CallDetail> callDetails) {
        this.callDetails = callDetails;
    }

    public List<ShortMessageDetail> getShortMessageDetails() {
        return shortMessageDetails;
    }

    public void setShortMessageDetails(List<ShortMessageDetail> shortMessageDetails) {
        this.shortMessageDetails = shortMessageDetails;
    }

    public PersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    public List<BillDetail> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(List<BillDetail> billDetails) {
        this.billDetails = billDetails;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
