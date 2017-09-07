package com.datatrees.crawler.core.processor.crawler.operator.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * 手机使用者在运营商处登记基本信息(个人信息，套餐信息，余额信息...)
 * @author likun
 * @version $Id: PersonalInformation.java, v 0.1 Jul 18, 2015 5:29:05 PM likun Exp $
 */
public class PersonalInformation implements Serializable {

    private static final long serialVersionUID = 5923546344368948356L;
    /**
     * 运营商处登记姓名
     */
    private String name;
    /**
     * 运营商处登记身份证号
     */
    private String identityCard;
    /**
     * 手机号码
     */
    private String telNum;
    /**
     * 手机号码归属地：中国移动，中国联通，中国电信
     */
    private String telNumAttribution;
    /**
     * 号码所在省份
     */
    private String telNumProvince;
    /**
     * 运营商用户等级
     */
    private String telCustomerLevel;
    /**
     * 账户余额
     */
    private Double accountBalance;
    /**
     * 入网时间
     */
    private Date   netJoinDate;
    /**
     * 当前开机状态
     */
    private String telNumStatus;
    /**
     * 当前套餐
     */
    private String telPackage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String getTelNumAttribution() {
        return telNumAttribution;
    }

    public void setTelNumAttribution(String telNumAttribution) {
        this.telNumAttribution = telNumAttribution;
    }

    public String getTelNumProvince() {
        return telNumProvince;
    }

    public void setTelNumProvince(String telNumProvince) {
        this.telNumProvince = telNumProvince;
    }

    public String getTelCustomerLevel() {
        return telCustomerLevel;
    }

    public void setTelCustomerLevel(String telCustomerLevel) {
        this.telCustomerLevel = telCustomerLevel;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Date getNetJoinDate() {
        return netJoinDate;
    }

    public void setNetJoinDate(Date netJoinDate) {
        this.netJoinDate = netJoinDate;
    }

    public String getTelNumStatus() {
        return telNumStatus;
    }

    public void setTelNumStatus(String telNumStatus) {
        this.telNumStatus = telNumStatus;
    }

    public String getTelPackage() {
        return telPackage;
    }

    public void setTelPackage(String telPackage) {
        this.telPackage = telPackage;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
