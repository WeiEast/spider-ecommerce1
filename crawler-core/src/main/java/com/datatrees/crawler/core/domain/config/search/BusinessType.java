package com.datatrees.crawler.core.domain.config.search;

/**
 * User: yand
 * Date: 2018/4/9
 */
public enum BusinessType {
    /**
     * 电商类业务
     */
    BASE_INFO("base_info", "个人信息"),
    HUABEI("huabei", "花呗信息"),
    MY_RATE("my_rate", "淘宝我的评价"),
    BANK_CARD("bank_card", "银行卡信息"),
    FEES("fees", "缴费信息"),
    ADDRESS("address", "收货地址"),
    ZM_CREDIT("zm_credit", "芝麻分"),
    TAOBAO_RECORD("taobao_record", "淘宝交易记录"),
    ALIPA_RECORD("alipay_record", "支付宝交易记录"),
    TRADE_ADDRESS("trade_address", "淘宝交易地址");
    private String name;
    private String remark;

    BusinessType(String name, String remark) {
        this.name = name;
        this.remark = remark;
    }

    public static BusinessType getBusinessType(String name) {
        for (BusinessType businessType : BusinessType.values()) {
            if (name.equals(businessType.getName())) {
                return businessType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return "BusinessType{" + "remark='" + remark + '\'' + ", name='" + name + '\'' + '}';
    }

}