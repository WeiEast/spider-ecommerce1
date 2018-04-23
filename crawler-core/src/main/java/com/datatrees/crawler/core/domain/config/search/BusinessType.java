package com.datatrees.crawler.core.domain.config.search;

import com.datatrees.rawdatacentral.domain.enums.WebsiteType;

/**
 * User: yand
 * Date: 2018/4/9
 */
public enum BusinessType {
    /**
     * 电商类业务
     */
    BASE_INFO("base_info", "个人信息", WebsiteType.ECOMMERCE.getValue()),
    HUABEI("huabei", "花呗信息", WebsiteType.ECOMMERCE.getValue()),
    MY_RATE("my_rate", "淘宝我的评价", WebsiteType.ECOMMERCE.getValue()),
    BANK_CARD("bank_card", "银行卡信息", WebsiteType.ECOMMERCE.getValue()),
    FEES("fees", "缴费信息", WebsiteType.ECOMMERCE.getValue()),
    ADDRESS("address", "收货地址", WebsiteType.ECOMMERCE.getValue()),
    ZM_CREDIT("zm_credit", "芝麻分", WebsiteType.ECOMMERCE.getValue()),
    TAOBAO_RECORD("taobao_record", "淘宝交易记录", WebsiteType.ECOMMERCE.getValue()),
    ALIPA_RECORD("alipay_record", "支付宝交易记录", WebsiteType.ECOMMERCE.getValue()),
    TRADE_ADDRESS("trade_address", "淘宝交易地址", WebsiteType.ECOMMERCE.getValue()),
    /**
     * 运营商
     */
    PERSONAL_INFO("personal_info", "基本信息", WebsiteType.OPERATOR.getValue()),
    BILL_DETAILS("bill_details", "账单信息", WebsiteType.OPERATOR.getValue()),
    CALL_DETAILS("call_details", "通话详单", WebsiteType.OPERATOR.getValue());
    private String code;
    private String name;
    private String websiteType;

    BusinessType(String code, String name, String websiteType) {
        this.code = code;
        this.name = name;
        this.websiteType = websiteType;
    }

    public static BusinessType getBusinessType(String code) {
        for (BusinessType businessType : BusinessType.values()) {
            if (code.equals(businessType.getCode())) {
                return businessType;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getWebsiteType() {
        return websiteType;
    }

    @Override
    public String toString() {
        return "BusinessType{" + "code='" + code + '\'' + ", name='" + name + '\'' + ", websiteType='" + websiteType + '\'' + '}';
    }


}