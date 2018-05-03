package com.datatrees.crawler.core.domain.config.search;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datatrees.rawdatacentral.domain.enums.WebsiteType;

/**
 * User: yand
 * Date: 2018/4/9
 */
public enum BusinessType {
    /**
     * 电商类业务
     */
    BASE_INFO("base_info", "个人信息", WebsiteType.ECOMMERCE),
    HUABEI("huabei", "花呗信息", WebsiteType.ECOMMERCE),
    MY_RATE("my_rate", "买家信用", WebsiteType.ECOMMERCE),
    BANK_CARD("bank_card", "银行卡信息", WebsiteType.ECOMMERCE),
    FEES("fees", "缴费信息", WebsiteType.ECOMMERCE),
    ADDRESS("address", "收货地址", WebsiteType.ECOMMERCE),
    ZM_CREDIT("zm_credit", "芝麻分", WebsiteType.ECOMMERCE),
    TAOBAO_RECORD("taobao_record", "淘宝交易记录", WebsiteType.ECOMMERCE),
    ALIPAY_RECORD("alipay_record", "支付宝交易记录", WebsiteType.ECOMMERCE),
    TRADE_ADDRESS("trade_address", "淘宝交易地址", WebsiteType.ECOMMERCE),
    /**
     * 运营商
     */
    PERSONAL_INFO("personal_info", "基本信息", WebsiteType.OPERATOR),
    BILL_DETAILS("bill_details", "账单信息", WebsiteType.OPERATOR),
    CALL_DETAILS("call_details", "通话详单", WebsiteType.OPERATOR);
    private String      code;
    private String      name;
    private WebsiteType websiteType;

    BusinessType(String code, String name, WebsiteType websiteType) {
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

    public WebsiteType getWebsiteType() {
        return websiteType;
    }

    public static Map<WebsiteType, List<BusinessType>> getGroup() {
        return Holder.MAP;
    }

    @Override
    public String toString() {
        return "BusinessType{" + "code='" + code + '\'' + ", name='" + name + '\'' + ", websiteType='" + websiteType + '\'' + '}';
    }

    private static class Holder {

        private static final Map<WebsiteType, List<BusinessType>> MAP = Arrays.stream(BusinessType.values()).collect(Collectors.groupingBy(BusinessType::getWebsiteType));
    }
}