package com.datatrees.rawdatacentral.domain.enums;

/**
 * 爬取的各业务类型枚举
 * User: yand
 * Date: 2018/3/27
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
    ALIPA_RECORD("alipay_record", "支付宝交易记录");

    private String code;
    private String name;

    BusinessType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BusinessType{" + "code='" + code + '\'' + ", name='" + name + '\'' + '}';
    }

}
